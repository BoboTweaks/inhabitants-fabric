package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {

    @Inject(
        method = "shouldDrawFace",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void inhabitants$immaterialForceDraw(
        BlockRenderView world,
        BlockState state,
        boolean cull,
        Direction side,
        BlockPos pos,
        CallbackInfoReturnable<Boolean> cir
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null &&
            mc.player.hasStatusEffect(ModEffects.IMMATERIAL)) {
            
            double distSq = mc.player.squaredDistanceTo(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
            );
            
            if (distSq < 10.5) {
                cir.setReturnValue(true);
            }
        }
    }
}
