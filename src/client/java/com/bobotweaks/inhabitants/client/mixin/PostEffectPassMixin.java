package com.bobotweaks.inhabitants.client.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;

import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import org.lwjgl.system.MemoryStack;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;
import java.util.Map;

@Mixin(PostEffectPass.class)
public abstract class PostEffectPassMixin {

    @Shadow @Final private String id;
    @Shadow @Final private Map<String, GpuBuffer> uniformBuffers;

    @ModifyArg(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/GpuDevice;createBuffer(Ljava/util/function/Supplier;ILjava/nio/ByteBuffer;)Lcom/mojang/blaze3d/buffers/GpuBuffer;"
        ),
        index = 1
    )
    private int inhabitants$addCopyUsage(int usage) {
        return usage | GpuBuffer.USAGE_COPY_DST;
    }

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void inhabitants$animateImmaterial(
        FrameGraphBuilder builder,
        Map<Identifier, Object> handles,
        GpuBufferSlice slice,
        CallbackInfo ci
    ) {
        if (this.id.contains("inhabitants")) {
            GpuBuffer timeBuffer = this.uniformBuffers.get("Time");

            if (timeBuffer != null) {
                float time = (float) (Util.getMeasuringTimeMs() / 1000.0);
                
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    ByteBuffer buf = stack.malloc(4);
                    buf.putFloat(0, time);
                    RenderSystem.getDevice().createCommandEncoder().writeToBuffer(timeBuffer.slice(), buf);
                }
            }
        }
    }
}
