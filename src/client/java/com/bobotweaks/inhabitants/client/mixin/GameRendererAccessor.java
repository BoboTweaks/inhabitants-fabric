package com.bobotweaks.inhabitants.client.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker("setPostProcessor")
    void inhabitants$setPostProcessor(Identifier id);

    @Invoker("clearPostProcessor")
    void inhabitants$clearPostProcessor();
}
