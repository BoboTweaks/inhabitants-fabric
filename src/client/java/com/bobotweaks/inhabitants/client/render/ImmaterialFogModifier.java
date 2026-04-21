package com.bobotweaks.inhabitants.client.render;

import com.bobotweaks.inhabitants.client.effect.ImmaterialEffectHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ImmaterialFogModifier extends FogModifier {

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return ImmaterialEffectHandler.fogIntensity > 0.0f;
    }

    @Override
    public void applyStartEndModifier(
        FogData data,
        Camera camera,
        ClientWorld clientWorld,
        float f,
        RenderTickCounter renderTickCounter
    ) {
        float intensity = ImmaterialEffectHandler.fogIntensity;
        float smoothed = intensity * intensity * (3.0f - 2.0f * intensity);
        
        data.renderDistanceStart = MathHelper.lerp(smoothed, f, 4.0F);
        data.renderDistanceEnd = MathHelper.lerp(smoothed, f, 12.0F);
        data.environmentalStart = MathHelper.lerp(smoothed, f, 4.0F);
        data.environmentalEnd = MathHelper.lerp(smoothed, f, 12.0F);
        data.skyEnd = MathHelper.lerp(smoothed, f, 12.0F);
        data.cloudEnd = MathHelper.lerp(smoothed, f, 12.0F);
    }

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        return 0x000000;
    }

    @Override
    public boolean isDarknessModifier() {
        return true; 
    }
}
