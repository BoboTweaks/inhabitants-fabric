package com.bobotweaks.inhabitants.items;

import com.bobotweaks.inhabitants.init.ModDataComponentTypes;
import com.bobotweaks.inhabitants.networking.handlers.DrillDamageHandler;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.*;
import net.minecraft.world.*;

import java.util.*;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

public class SpikeDrillItem extends Item {
    public static final int BASE_TEMPERATURE_MAX = 120;
    public static final int OVERHEAT_COOLDOWN_TICKS = 40;

    private static final int USE_DURATION_TICKS = 60 * 60 * 20;
    private static final int DRILL_PACKET_INTERVAL = 2;
    
    public static Runnable clientPacketSender = null;

    // client-side only
    private static final Map<UUID, Long> clientStartTicks = new HashMap<>();
    private static final Map<UUID, Long> clientLastUseTicks = new HashMap<>();

    public SpikeDrillItem(Settings settings) {
        super(settings.maxCount(1)
            .component(ModDataComponentTypes.TEMPERATURE, 0)
            .component(ModDataComponentTypes.LAST_TICK, 0L));
    }

    public static int getTemperatureMax(ItemStack stack) {
        return BASE_TEMPERATURE_MAX;
    }

    public static int getTemperature(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.TEMPERATURE, 0);
    }

    public static void setTemperature(ItemStack stack, int temperature) {
        stack.set(
            ModDataComponentTypes.TEMPERATURE,
            Math.max(0, Math.min(getTemperatureMax(stack), temperature))
        );
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack drill = user.getStackInHand(hand);

        if (getTemperature(drill) >= getTemperatureMax(drill)) {
            user.getItemCooldownManager().set(drill, OVERHEAT_COOLDOWN_TICKS);

            if (world instanceof ServerWorld serverWorld) {
                user.damage(serverWorld, world.getDamageSources().onFire(), 2.0f);
                
                world.playSound(null,
                    user.getBlockPos(),
                    SoundEvents.BLOCK_LAVA_EXTINGUISH,
                    SoundCategory.PLAYERS,
                    1.0f, 1.0f
                );
            }

            user.sendMessage(Text.translatable("tooltip.inhabitants.spike_drill.overheated")
                .formatted(Formatting.RED), true);
            
            return ActionResult.FAIL;
        }

        BlockHitResult hit = raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return ActionResult.FAIL;
        }

        user.setCurrentHand(hand);
        drill.set(ModDataComponentTypes.LAST_TICK, world.getTime());
        return ActionResult.CONSUME;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return USE_DURATION_TICKS;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        if (!world.isClient()) {
            stack.set(ModDataComponentTypes.LAST_TICK, world.getTime());
            return;
        }

        // client side
        BlockHitResult hit = raycast(world, player, RaycastContext.FluidHandling.NONE);
        boolean overBlock = hit.getType() == HitResult.Type.BLOCK;
        double reach = player.getBlockInteractionRange();
        boolean inReach = overBlock && hit.getPos().distanceTo(player.getEyePos()) <= reach;

        clientStartTicks.putIfAbsent(player.getUuid(), world.getTime());
        clientLastUseTicks.put(player.getUuid(), world.getTime());

        int elapsed = USE_DURATION_TICKS - remainingUseTicks;
        if (elapsed % DRILL_PACKET_INTERVAL == 0 && overBlock && inReach && clientPacketSender != null) {
            clientPacketSender.run();
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient() && user instanceof PlayerEntity) {
            DrillDamageHandler.clearMomentum(user);
        }

        if (world.isClient() && user instanceof PlayerEntity player) {
            clientStartTicks.remove(player.getUuid());
            clientLastUseTicks.remove(player.getUuid());
        }

        return true;
    }

    public static float calculatingDrillSpeed(PlayerEntity player, ItemStack drill, float partialTick) {
        if (player == null) return 0f;

        Long start = clientStartTicks.get(player.getUuid());
        Long last = clientLastUseTicks.get(player.getUuid());

        if (start == null || last == null) return 0f;

        if (player.getEntityWorld().getTime() - last > 40) {
            clientStartTicks.remove(player.getUuid());
            return 0f;
        }

        float duration = (player.getEntityWorld().getTime() - start) + partialTick;
        return Math.min(1.0f, duration / (float) DrillDamageHandler.RAMP_UP_TICKS);
    }

    @Override
    public void inventoryTick(
        ItemStack stack,
        ServerWorld world,
        Entity entity,
        @Nullable EquipmentSlot slot
    ) {
        if (world.isClient()) return;

        int temperature = getTemperature(stack);
        if (temperature > 0) {

            long lastUsed = stack.getOrDefault(ModDataComponentTypes.LAST_TICK, 0L);
            long currentTime = world.getTime();
            long elapsed = currentTime - lastUsed;

            if (elapsed >= 60 && (elapsed - 60) % 20 == 0) {
                setTemperature(stack, temperature - 1);
            }
        }
    }

    @Override
    public boolean onClicked(
        ItemStack stack,
        ItemStack otherStack,
        Slot slot,
        ClickType clickType,
        PlayerEntity player,
        StackReference cursorStackReference
    ) {
        if (otherStack.isOf(Items.SNOWBALL) && getTemperature(stack) > 0) {
            World world = player.getEntityWorld();

            if (!player.getItemCooldownManager().isCoolingDown(stack)) {
                setTemperature(stack, getTemperature(stack) - 30);

                if (!player.getAbilities().creativeMode) {
                    otherStack.decrement(1);
                }

                world.playSound(null,
                    player.getBlockPos(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.PLAYERS,
                    1.0f, 1.0f
                );

                player.getItemCooldownManager().set(stack, 20);
                return true;
            }
        }

        return super.onClicked(
            stack,
            otherStack,
            slot,
            clickType,
            player,
            cursorStackReference
        );
    }

    @Override
    public void appendTooltip(
        ItemStack stack,
        TooltipContext context,
        TooltipDisplayComponent displayComponent,
        Consumer<Text> textConsumer,
        TooltipType type
    ) {
        int temp = getTemperature(stack);
        int max = getTemperatureMax(stack);
        Formatting color = temp >= max ? Formatting.RED : Formatting.GOLD;

        textConsumer.accept(Text.translatable("tooltip.inhabitants.spike_drill.temperature", temp, max)
            .formatted(color));

        textConsumer.accept(Text.translatable("tooltip.inhabitants.spike_drill")
            .formatted(Formatting.GRAY));
    }
}
