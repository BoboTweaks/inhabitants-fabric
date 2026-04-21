package com.bobotweaks.inhabitants.client.effect;

import com.bobotweaks.inhabitants.Inhabitants;
import com.bobotweaks.inhabitants.init.ModEffects;
import com.bobotweaks.inhabitants.init.ModSoundEvents;
import com.bobotweaks.inhabitants.client.audio.ModTickableSounds;
import com.bobotweaks.inhabitants.client.mixin.GameRendererAccessor;
import com.bobotweaks.inhabitants.client.audio.ModAudio;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ImmaterialEffectHandler {
    private static boolean wasInsideBlock = false;
    private static ModTickableSounds.ImmaterialInsideLoop loopSound = null;
    private static final Identifier IMMATERIAL_SHADER_ID = Identifier.of(Inhabitants.MOD_ID, "immaterial");

    public static volatile float fogIntensity = 0.0f;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ImmaterialEffectHandler::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        boolean hasEffect = player.hasStatusEffect(ModEffects.IMMATERIAL);
        boolean isInside = isInsideBlock(player);

        if (hasEffect) {
            handleAudio(client, player, isInside);
            handleVisuals(client, player, isInside);
        } else {
            
            if (wasInsideBlock) {
                stopLoopSound();
                clearShader(client);
                wasInsideBlock = false;
            }
        }
    }

    private static void handleAudio(
        MinecraftClient client,
        ClientPlayerEntity player,
        boolean isInside
    ) {
        if (isInside && !wasInsideBlock) {
            player.playSound(ModSoundEvents.IMMATERIAL_ENTER_WALL, 1.0F, 1.0F);
            startLoopSound(client, player);
        } else if (!isInside && wasInsideBlock) {
            player.playSound(ModSoundEvents.IMMATERIAL_EXIT_WALL, 1.0F, 1.0F);
            stopLoopSound();
        }
        
        if (loopSound != null) {
            loopSound.setVolume(5.0f);
        }
    }

    private static void handleVisuals(
        MinecraftClient client,
        ClientPlayerEntity player,
        boolean isInside
    ) {
        if (isInside && !wasInsideBlock) {
            applyShader(client);
        } else if (!isInside && wasInsideBlock) {
            clearShader(client);
        }
        
        if (isInside) {
            fogIntensity = Math.min(1.0f, fogIntensity + 0.1f);
        } else {
            fogIntensity = Math.max(0.0f, fogIntensity - 0.1f);
        }
        
        ModAudio.updateFilter(fogIntensity);
        
        wasInsideBlock = isInside;
    }

    private static void startLoopSound(MinecraftClient client, ClientPlayerEntity player) {
        if (loopSound == null) {
            loopSound = new ModTickableSounds.ImmaterialInsideLoop(player);
            client.getSoundManager().play(loopSound);
        }
    }

    private static void stopLoopSound() {
        if (loopSound != null) {
            loopSound.stopSound();
            loopSound = null;
        }
    }

    private static void applyShader(MinecraftClient client) {
        ((GameRendererAccessor) client.gameRenderer)
            .inhabitants$setPostProcessor(IMMATERIAL_SHADER_ID);
    }

    private static void clearShader(MinecraftClient client) {
        if (IMMATERIAL_SHADER_ID.equals(client.gameRenderer.getPostProcessorId())) {
            ((GameRendererAccessor) client.gameRenderer).inhabitants$clearPostProcessor();
        }
    }

    public static boolean isInsideWall(Camera camera, ClientWorld world) {
        BlockPos pos = camera.getBlockPos();
        return !world.getBlockState(pos).getCollisionShape(world, pos, ShapeContext.absent()).isEmpty();
    }

    private static boolean isInsideBlock(ClientPlayerEntity player) {
        double px = player.getX();
        for (double dy : new double[]{0.1, 0.8, 1.62}) {

            BlockPos pos = BlockPos.ofFloored(px, player.getY() + dy, player.getZ());

            if (!player.getEntityWorld().getBlockState(pos)
                .getCollisionShape(player.getEntityWorld(), pos, ShapeContext.absent())
                .isEmpty())
            {
                return true;
            }
        }
        
        return false;
    }
}
