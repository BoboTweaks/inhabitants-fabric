package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.client.audio.ModAudio;

import net.minecraft.client.sound.SoundEngine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Inject(
        method = "init",
        at = @At("RETURN")
    )
    private void inhabitants$initOpenALefx(String deviceSpecifier, boolean directionalAudio, CallbackInfo ci) {
        ModAudio.init();
    }

    @Inject(
        method = "close",
        at = @At("HEAD")
    )
    private void inhabitants$cleanupOpenALefx(CallbackInfo ci) {
        ModAudio.cleanup();
    }
}
