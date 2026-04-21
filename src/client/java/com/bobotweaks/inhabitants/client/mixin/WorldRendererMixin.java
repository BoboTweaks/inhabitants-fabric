package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.client.effect.ImmaterialEffectHandler;
import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow private ClientWorld world;

    @Inject(
        method = "hasBlindnessOrDarkness",
        at = @At("RETURN"),
        cancellable = true
    )
    private void inhabitants$blockSkyWhenImmaterial(Camera camera, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {

            if (camera.getFocusedEntity() instanceof LivingEntity living &&
                living.hasStatusEffect(ModEffects.IMMATERIAL)) {
                
                if (ImmaterialEffectHandler.isInsideWall(camera, this.world)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
