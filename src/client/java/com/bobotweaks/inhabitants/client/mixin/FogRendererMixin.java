package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.client.render.ImmaterialFogModifier;

import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Shadow @Final private static List<FogModifier> FOG_MODIFIERS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void inhabitants$addImmaterialFog(CallbackInfo ci) {
        FOG_MODIFIERS.add(0, new ImmaterialFogModifier());
    }
}
