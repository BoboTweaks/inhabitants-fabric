package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {

    @Inject(
        method = "renderOverlays",
        at = @At("HEAD")
    )
    private void inhabitants$disableInWallOverlay(
        boolean sleeping,
        float tickProgress,
        OrderedRenderCommandQueue queue,
        CallbackInfo ci
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null &&
            client.player.hasStatusEffect(ModEffects.IMMATERIAL)) {
        }
    }

    @Inject(
        method = "renderOverlays",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameOverlayRenderer;renderInWallOverlay(Lnet/minecraft/client/texture/Sprite;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"
        ),
        cancellable = true
    )
    private void inhabitants$cancelInWallOverlay(
        boolean sleeping,
        float tickProgress,
        OrderedRenderCommandQueue queue,
        CallbackInfo ci
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null &&
            client.player.hasStatusEffect(ModEffects.IMMATERIAL)) {
                
            ci.cancel();
        }
    }
}
