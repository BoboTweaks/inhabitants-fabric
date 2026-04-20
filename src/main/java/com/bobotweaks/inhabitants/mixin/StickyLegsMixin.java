package com.bobotweaks.inhabitants.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class StickyLegsMixin {

    @Inject(
        method = "isClimbing", 
        at = @At("HEAD"), 
        cancellable = true
    )
    private void inhabitants$stickyLegsClimb(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof PlayerEntity player &&
            player.hasStatusEffect(ModEffects.STICKY_LEGS) &&
            player.horizontalCollision) {
            
            cir.setReturnValue(true);
        }
    }
}
