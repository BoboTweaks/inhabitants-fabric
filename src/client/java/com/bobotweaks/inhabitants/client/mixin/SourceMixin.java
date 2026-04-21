package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.client.audio.ModAudio;
import com.bobotweaks.inhabitants.client.effect.ImmaterialEffectHandler;

import net.minecraft.client.sound.Source;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTEfx;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Source.class)
public abstract class SourceMixin {

    @Shadow @Final private int pointer;

    @ModifyVariable(
        method = "setPitch",
        at = @At("HEAD"),
        argsOnly = true
    )
    private float inhabitants$modifyPitch(float pitch) {
        if (ImmaterialEffectHandler.fogIntensity > 0.0F) {
            return pitch * MathHelper.lerp(ImmaterialEffectHandler.fogIntensity, 1.0F, 0.75F);
        }

        return pitch;
    }

    @Inject(
        method = {"setVolume", "setPitch", "play", "setBuffer", "setStream"},
        at = @At("HEAD")
    )
    private void inhabitants$updateMuffleFilter(CallbackInfo ci) {
        if (!ModAudio.efxSupported || ModAudio.lowpassFilterId == -1) return;
        
        if (ImmaterialEffectHandler.fogIntensity > 0.0F) {
            AL10.alSourcei(this.pointer, EXTEfx.AL_DIRECT_FILTER, ModAudio.lowpassFilterId);
        } else {
            AL10.alSourcei(this.pointer, EXTEfx.AL_DIRECT_FILTER, EXTEfx.AL_FILTER_NULL);
        }
    }
}
