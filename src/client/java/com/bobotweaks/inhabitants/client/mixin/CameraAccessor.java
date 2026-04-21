package com.bobotweaks.inhabitants.client.mixin;

import net.minecraft.client.render.Camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("setRotation")
    void inhabitants$setRotation(float yaw, float pitch);
}
