package com.bobotweaks.inhabitants.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.registry.entry.RegistryEntry;

@Mixin(LivingEntity.class)
public abstract class ConcussionMixin {

    @Inject(
        method = "getAttributeValue",
        at = @At("RETURN"),
        cancellable = true
    ) private void inhabitants$concussionSpeedAttribute(
        RegistryEntry<EntityAttribute> attribute,
        CallbackInfoReturnable<Double> cir
    ) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (attribute == EntityAttributes.MOVEMENT_SPEED &&
            self.hasStatusEffect(ModEffects.CONCUSSION)) {
            cir.setReturnValue(cir.getReturnValue() * 0.7);
        }
    }

    @Inject(
        method = "getMovementSpeed",
        at = @At("RETURN"),
        cancellable = true
    )
    private void inhabitants$concussionMovementSpeed(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        
        if (self.hasStatusEffect(ModEffects.CONCUSSION)) {
            cir.setReturnValue(cir.getReturnValue() * 0.7F);
        }
    }
}
