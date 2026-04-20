package com.bobotweaks.inhabitants.mixin;

import com.bobotweaks.inhabitants.init.*;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ReverseGrowthMixin {

    @Unique
    private boolean inhabitants$hadReverseGrowth;

    @Inject(method = "tick", at = @At("HEAD"))
    private void inhabitants$tickReverseGrowth(CallbackInfo ci) {
        
        PlayerEntity player = (PlayerEntity) (Object) this;
        World world = player.getEntityWorld();
        boolean hasEffect = player.hasStatusEffect(ModEffects.REVERSE_GROWTH);

        if (hasEffect != inhabitants$hadReverseGrowth) {
            if (inhabitants$hadReverseGrowth && !hasEffect) {

                if (!world.isClient()) {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSoundEvents.REVERSE_GROWTH, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    if (world instanceof ServerWorld serverWorld) {

                        for (int i = 0; i < 20; i++) {
                            double px = player.getX() + (world.random.nextDouble() - 0.5) * 1.0;
                            double py = player.getY() + world.random.nextDouble() * player.getHeight();
                            double pz = player.getZ() + (world.random.nextDouble() - 0.5) * 1.0;

                            serverWorld.spawnParticles(
                                ModParticleTypes.ABRACADABRA,
                                px, py, pz,
                                1, 0, 0, 0, 0.05
                            );
                        }

                    }
                }
            }

            inhabitants$hadReverseGrowth = hasEffect;
        }
    }
}
