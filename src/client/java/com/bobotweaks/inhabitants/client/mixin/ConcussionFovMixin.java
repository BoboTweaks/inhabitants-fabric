package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.client.effect.ConcussionEffectHandler;
import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class ConcussionFovMixin {

    @Inject(
        method = "getFov",
        at = @At("RETURN"),
        cancellable = true
    ) private void inhabitants$concussionFov(
        Camera camera,
        float tickDelta,
        boolean changingFov,
        CallbackInfoReturnable<Float> cir
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;
        if (!client.player.hasStatusEffect(ModEffects.CONCUSSION)) return;
        if (ConcussionEffectHandler.muffleLerp <= 0.1F) return;

        long gameTime = client.world != null ? client.world.getTime() : 0L;
        float ticks = (float) gameTime + tickDelta;
        float fovOffset = MathHelper.sin(ticks * 0.08F) * 3.0F * ConcussionEffectHandler.muffleLerp;
        
        cir.setReturnValue(cir.getReturnValue() + fovOffset);
    }
}
