package com.bobotweaks.inhabitants.networking.handlers;

import com.bobotweaks.inhabitants.init.ModDataComponentTypes;
import com.bobotweaks.inhabitants.items.SpikeDrillItem;
import com.bobotweaks.inhabitants.networking.payloads.DrillDamagePayload;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;

import java.util.*;

public class DrillDamageHandler {

    private static final float DRILL_SPEED_MIN = 1.5f;
    private static final float DRILL_SPEED_MAX = 12.5f;
    private static final float DRILL_SPEED_BONUS_FULL_MOMENTUM = 75.0f;

    private static final int TICKS_PER_SEC = 20;
    private static final int OVERHEAT_COOLDOWN_TICKS = 2 * TICKS_PER_SEC;
    private static final int ELAPSED_CAP_TICKS = TICKS_PER_SEC;
    private static final int ELAPSED_DEFAULT_TICKS = 1;
    private static final int STALE_TICKS = 40;

    private static final float DESTROY_DIVISOR_CORRECT = 30.0f;
    private static final float DESTROY_DIVISOR_WRONG = 100.0f;
    private static final float DRILL_CONTINUOUS_DELAY = 2.25f;

    public static final int RAMP_UP_TICKS = 15 * TICKS_PER_SEC;

    private static class MiningData {
        public BlockPos pos;
        public float progress;
        public long lastTick;
        public long startTick;
    }

    private static final Map<UUID, MiningData> MINING_DATA = new HashMap<>();

    public static void receive(DrillDamagePayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();

        if (player != null) {
            ItemStack stack = player.getActiveItem();

            if (stack.getItem() instanceof SpikeDrillItem) {
                handleDamage(player);
            } else {
                clearMomentum(player);
            }
        }
    }

    public static void tickServer(MinecraftServer server) {
        for (UUID uuid : MINING_DATA.keySet().toArray(new UUID[0])) {
            MiningData data = MINING_DATA.get(uuid);

            if (data == null) continue;

            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            
            if (player == null) {
                MINING_DATA.remove(uuid);
                continue;
            }

            ServerWorld world = (ServerWorld) player.getEntityWorld();
            if (world.getTime() - data.lastTick > STALE_TICKS) {

                if (data.pos != null) {
                    world.setBlockBreakingInfo(player.getId() + 1000000, data.pos, -1);
                }

                MINING_DATA.remove(uuid);
            }
        }
    }

    private static void handleDamage(ServerPlayerEntity player) {
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        double blockReach = player.getBlockInteractionRange();
        HitResult rayTrace = player.raycast(blockReach, 1.0f, false);

        if (!(rayTrace instanceof BlockHitResult bhr)) {
            return;
        }

        BlockPos pos = bhr.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.isAir()) return;

        float hardness = state.getHardness(world, pos);
        if (hardness < 0) return;

        MiningData data = MINING_DATA.computeIfAbsent(player.getUuid(), k -> {
            MiningData d = new MiningData();
            d.startTick = world.getTime();
            d.lastTick = world.getTime();
            return d;
        });

        if (!pos.equals(data.pos)) {
            if (data.pos != null) {
                world.setBlockBreakingInfo(player.getId() + 1000000, data.pos, -1);
            }

            data.pos = pos;
            data.progress = 0f;
        }

        ItemStack drill = player.getActiveItem();
        long duration = world.getTime() - data.startTick;
        float ratio = Math.min(1.0f, (float) duration / RAMP_UP_TICKS);

        float tickProgress = calculateDrillSpeed(player, drill, state, hardness, ratio);

        long elapsed = world.getTime() - data.lastTick;
        if (elapsed <= 0 || elapsed > ELAPSED_CAP_TICKS) elapsed = ELAPSED_DEFAULT_TICKS;
        data.lastTick = world.getTime();

        data.progress += tickProgress * elapsed;

        if (data.progress >= 1.0f) {
            processBlockBreak(player, world, pos, drill, data);
        } else {
            world.setBlockBreakingInfo(player.getId() + 1000000, pos, (int) (data.progress * 10.0F));
        }
    }

    public static void clearMomentum(LivingEntity entity) {
        if (entity == null) return;
        MiningData data = MINING_DATA.remove(entity.getUuid());

        if (data != null &&
            data.pos != null &&
            entity.getEntityWorld() instanceof ServerWorld sw) {
            
            sw.setBlockBreakingInfo(entity.getId() + 1000000, data.pos, -1);
        }
    }

    private static float calculateDrillSpeed(
        ServerPlayerEntity player,
        ItemStack drill,
        BlockState state,
        float hardness,
        float ratio
    ) {
        boolean isPickaxeMineable = state.isIn(BlockTags.PICKAXE_MINEABLE);

        float baseSpeed = isPickaxeMineable ? DRILL_SPEED_MIN : 1.0f;
        float maxSpeed = isPickaxeMineable ? DRILL_SPEED_MAX : 1.0f;
        float speed = baseSpeed + (maxSpeed - baseSpeed) * ratio;

        if (ratio >= 1.0f) {
            speed += DRILL_SPEED_BONUS_FULL_MOMENTUM;
        }

        if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float fatigue = switch (Objects.requireNonNull(
                player.getStatusEffect(StatusEffects.MINING_FATIGUE)).getAmplifier()) {
                
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };

            speed *= fatigue;
        }

        if (player.isSubmergedIn(FluidTags.WATER) &&
                player.getAttributeValue(EntityAttributes.SUBMERGED_MINING_SPEED) <= 0.2) {
            
            speed /= 5.0F;
        }
        
        if (!player.isOnGround()) {
            speed /= 5.0F;
        }

        if (hardness <= 0.01f) return 1.0f;

        boolean canVanillaDrop = drill.isSuitableFor(state) || !state.isToolRequired();

        float vanillaTickProgress = speed /
            hardness /
            (canVanillaDrop ? DESTROY_DIVISOR_CORRECT : DESTROY_DIVISOR_WRONG);

        return 1.0f / (1.0f / vanillaTickProgress + DRILL_CONTINUOUS_DELAY);
    }

    private static void processBlockBreak(
        ServerPlayerEntity player,
        ServerWorld world,
        BlockPos pos,
        ItemStack drill,
        MiningData data
    ) {
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) return;

        BlockSoundGroup blockSound = state.getSoundGroup();

        world.playSound(null, pos, blockSound.getBreakSound(), SoundCategory.BLOCKS,
                (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);

        world.syncWorldEvent(null, WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));

        boolean canHarvest = !state.isToolRequired() || drill.isSuitableFor(state);
        
        if (canHarvest) {
            Block.dropStacks(state, world, pos, world.getBlockEntity(pos), player, drill);
        }

        world.removeBlock(pos, false);
        world.setBlockBreakingInfo(player.getId() + 1000000, pos, -1);
        data.pos = null;
        data.progress = 0f;

        if (drill.getItem() instanceof SpikeDrillItem) {
            if (!player.isCreative()) {
                SpikeDrillItem.setTemperature(drill, SpikeDrillItem.getTemperature(drill) + 1);
            }

            drill.set(ModDataComponentTypes.LAST_TICK, world.getTime());
            drill.damage(1, player, EquipmentSlot.MAINHAND);

            if (SpikeDrillItem.getTemperature(drill) >= SpikeDrillItem.getTemperatureMax(drill)) {
                player.damage(world, world.getDamageSources().onFire(), 2.0f);
                player.clearActiveItem();

                player.getItemCooldownManager().set(drill, OVERHEAT_COOLDOWN_TICKS);

                player.sendMessage(Text.translatable("tooltip.inhabitants.spike_drill.overheated")
                    .formatted(Formatting.RED), true);

                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH,
                    SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
    }
}
