package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;
import com.bobotweaks.inhabitants.client.effect.ConcussionEffectHandler;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public abstract class EntityTurnMixin {

    @ModifyVariable(
        method = "changeLookDirection",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    ) private double inhabitants$concussionYawLag(double yaw) {
        
        if ((Object) this instanceof ClientPlayerEntity player
            && player.hasStatusEffect(ModEffects.CONCUSSION)
            && ConcussionEffectHandler.muffleLerp > 0.0F
        ) {
            return yaw * (1.0 - ConcussionEffectHandler.muffleLerp * 0.4);
        }

        return yaw;
    }

    @ModifyVariable(
        method = "changeLookDirection",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 1
    ) private double inhabitants$concussionPitchLag(double pitch) {

        if ((Object) this instanceof ClientPlayerEntity player
            && player.hasStatusEffect(ModEffects.CONCUSSION)
            && ConcussionEffectHandler.muffleLerp > 0.0F
        ) {
            return pitch * (1.0 - ConcussionEffectHandler.muffleLerp * 0.4);
        }

        return pitch;
    }
}
