package com.bobotweaks.inhabitants.client.items;

import com.bobotweaks.inhabitants.client.animation.FPVAnimationPlayer;
import com.bobotweaks.inhabitants.client.audio.ModTickableSounds;
import com.bobotweaks.inhabitants.init.ModSoundEvents;
import com.bobotweaks.inhabitants.items.SpikeDrillItem;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.*;

public class SpikeDrillClient {

    private static boolean wasOverheated = false;
    private static boolean initialized = false;

    public static boolean applyHandTransform(
        MatrixStack matrices,
        ClientPlayerEntity player,
        Arm arm,
        ItemStack itemInHand,
        float partialTick,
        float equipProcess,
        float swingProcess
    ) {
        init(player);

        boolean isOverheated = SpikeDrillItem.getTemperature(itemInHand) >= SpikeDrillItem
                .getTemperatureMax(itemInHand);

        if (isOverheated && !wasOverheated) {
            FPVAnimationPlayer.INSTANCE.playOverridePhase(arm, "spike_drill_overheat");
        }

        wasOverheated = isOverheated;

        float ratio = SpikeDrillItem.calculatingDrillSpeed(player, itemInHand, partialTick);
        float speedMultiplier = 0.9f;

        if (ratio >= 0.1f) {
            float alpha = (ratio - 0.1f) / 0.9f;
            speedMultiplier = MathHelper.lerp(alpha, 0.9f, 1.5f);
        }

        FPVAnimationPlayer.INSTANCE.setSpeed(arm, speedMultiplier);

        return FPVAnimationPlayer.INSTANCE.apply("spike_drill_start",
            matrices,
            player,
            arm,
            itemInHand,
            partialTick,
            equipProcess,
            true
        );
    }

    private static void init(PlayerEntity player) {
        if (initialized)
            return;
        initialized = true;

        FPVAnimationPlayer.INSTANCE.setTriggerCallback((id, p, arm, stack) -> {
            if ("inhabitants:drill_dig".equals(id)) {
                HitResult hit = MinecraftClient.getInstance().crosshairTarget;

                if (hit instanceof BlockHitResult bhr) {
                    BlockPos pos = bhr.getBlockPos();
                    BlockState state = p.getEntityWorld().getBlockState(pos);
                    BlockSoundGroup soundType = state.getSoundGroup();

                    float pitch = 1.0F;
                    float volume = soundType.getVolume() - (0.75f * p.getRandom().nextFloat());

                    p.getEntityWorld().playSound(
                        p,
                        pos.getX(), pos.getY(), pos.getZ(),
                        ModSoundEvents.DRILL_DIG,
                        SoundCategory.BLOCKS,
                        volume,
                        pitch
                    );

                    for (int i = 0; i < 5; i++) {
                        MinecraftClient.getInstance().particleManager.addParticle(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
                            pos.getX() + p.getRandom().nextDouble(),
                            pos.getY() + p.getRandom().nextDouble(),
                            pos.getZ() + p.getRandom().nextDouble(),
                            0.0, 0.0, 0.0
                        );
                    }
                }
            } else if ("inhabitants:drill_start_sound".equals(id)) {

                p.getEntityWorld().playSound(
                    p,
                    p.getX(), p.getY(), p.getZ(),
                    ModSoundEvents.DRILL_START,
                    SoundCategory.PLAYERS,
                    0.25f,
                    1.0f
                );

            } else if ("inhabitants:drill_loop_sound".equals(id)) {
                if (ModTickableSounds.DrillLoop.currentSound == null
                        || ModTickableSounds.DrillLoop.currentSound.isDone()) {
                    
                    ModTickableSounds.DrillLoop.currentSound = new
                        ModTickableSounds.DrillLoop((ClientPlayerEntity) p);
                    
                    MinecraftClient.getInstance().getSoundManager()
                        .play(ModTickableSounds.DrillLoop.currentSound);
                }
            } else if ("inhabitants:drill_stop_sound".equals(id)) {
                if (ModTickableSounds.DrillLoop.currentSound != null) {
                    ModTickableSounds.DrillLoop.currentSound.stopLoop();
                    ModTickableSounds.DrillLoop.currentSound = null;
                }

                p.getEntityWorld().playSound(
                    p,
                    p.getX(), p.getY(), p.getZ(),
                    ModSoundEvents.DRILL_STOPPED,
                    SoundCategory.PLAYERS,
                    0.25f,
                    1.0f
                );
            }
        });
    }
}
