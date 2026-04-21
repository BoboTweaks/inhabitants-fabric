package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.client.effect.ConcussionEffectHandler;
import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.client.render.Camera;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class ConcussionCameraMixin {

    @Inject(method = "update", at = @At("TAIL"))
    private void inhabitants$concussionCameraAngles(
        World area,
        Entity focusedEntity,
        boolean thirdPerson,
        boolean inverseView,
        float tickDelta,
        CallbackInfo ci
    ) {
        
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;
        if (!client.player.hasStatusEffect(ModEffects.CONCUSSION)) return;
        if (ConcussionEffectHandler.muffleLerp <= 0.1F) return;

        Camera camera = (Camera) (Object) this;
        long gameTime = client.world != null ? client.world.getTime() : 0L;
        float ticks = (float) gameTime + tickDelta;

        float targetYaw = camera.getYaw();
        float targetPitch = camera.getPitch();
        float lagFactor = MathHelper.lerp(ConcussionEffectHandler.muffleLerp, 1.0F, 0.1F);

        ConcussionEffectHandler.lastCameraYaw = MathHelper.lerpAngleDegrees(
            lagFactor,
            ConcussionEffectHandler.lastCameraYaw,
            targetYaw
        );

        ConcussionEffectHandler.lastCameraPitch = MathHelper.lerp(
            lagFactor,
            ConcussionEffectHandler.lastCameraPitch,
            targetPitch
        );

        float yawSway = MathHelper.cos(ticks * 0.03F) * 2.0F * ConcussionEffectHandler.muffleLerp;
        float pitchSway = MathHelper.sin(ticks * 0.04F) * 2.0F * ConcussionEffectHandler.muffleLerp;

        ((CameraAccessor) camera).inhabitants$setRotation(
            ConcussionEffectHandler.lastCameraYaw + yawSway,
            ConcussionEffectHandler.lastCameraPitch + pitchSway
        );
    }
}
