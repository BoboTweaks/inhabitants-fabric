package com.bobotweaks.inhabitants.client.effect;

import com.bobotweaks.inhabitants.init.ModEffects;
import com.bobotweaks.inhabitants.client.audio.ModTickableSounds;
import com.bobotweaks.inhabitants.client.audio.ModAudio;
import com.bobotweaks.inhabitants.client.mixin.GameRendererAccessor;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ConcussionEffectHandler {

    private static final Identifier CONCUSSION_SHADER_ID = Identifier.of("inhabitants", "concussion");

    public static volatile float muffleLerp = 0.0F;
    public static float lastCameraYaw = 0.0F;
    public static float lastCameraPitch = 0.0F;

    private static ModTickableSounds.ConcussionBuzz concussionBuzz = null;
    private static boolean wasConcussed = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(ConcussionEffectHandler::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;

        if (player == null) {
            if (muffleLerp > 0.0F) {
                muffleLerp = Math.max(0.0F, muffleLerp - 0.1F);
                ModAudio.updateFilter(muffleLerp);
            }
            return;
        }

        boolean isConcussed = player.hasStatusEffect(ModEffects.CONCUSSION);

        updateMuffleLerp(isConcussed);
        updateAudio(client, player, isConcussed);
        updateVisuals(client, player, isConcussed);

        wasConcussed = isConcussed;
    }

    private static void updateMuffleLerp(boolean isConcussed) {
        if (isConcussed) {
            muffleLerp = Math.min(1.0F, muffleLerp + 0.1F);
        } else {
            muffleLerp = Math.max(0.0F, muffleLerp - 0.1F);
        }
        if (muffleLerp < 0.05F) muffleLerp = 0.0F;

        ModAudio.updateFilter(muffleLerp);
    }

    private static void updateAudio(MinecraftClient client, ClientPlayerEntity player, boolean isConcussed) {
        if (isConcussed) {
            if (concussionBuzz == null) {

                concussionBuzz = new ModTickableSounds.ConcussionBuzz(player);
                client.getSoundManager().play(concussionBuzz);
            }
        } else if (concussionBuzz != null) {
            if (concussionBuzz.isDone()) {
                
                concussionBuzz = null;
            }
        }
    }

    private static void updateVisuals(MinecraftClient client, ClientPlayerEntity player, boolean isConcussed) {
        if (isConcussed && !wasConcussed) {
            lastCameraYaw = player.getYaw();
            lastCameraPitch = player.getPitch();

            ((GameRendererAccessor) client.gameRenderer).inhabitants$setPostProcessor(CONCUSSION_SHADER_ID);
        } else if (!isConcussed && wasConcussed) {

            if (CONCUSSION_SHADER_ID.equals(client.gameRenderer.getPostProcessorId())) {
                ((GameRendererAccessor) client.gameRenderer).inhabitants$clearPostProcessor();
            }
        }
    }

    public static void onCameraAngles(
        float partialTick,
        long gameTime,
        float[] yawPitchRoll
    ) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;
        if (!client.player.hasStatusEffect(ModEffects.CONCUSSION)) return;
        if (muffleLerp <= 0.1F) return;

        float ticks = (float) gameTime + partialTick;
        float targetYaw = yawPitchRoll[0];
        float targetPitch = yawPitchRoll[1];

        float lagFactor = MathHelper.lerp(muffleLerp, 1.0F, 0.1F);
        lastCameraYaw = MathHelper.lerpAngleDegrees(lagFactor, lastCameraYaw, targetYaw);
        lastCameraPitch = MathHelper.lerp(lagFactor, lastCameraPitch, targetPitch);

        float roll = MathHelper.sin(ticks * 0.05F) * 5.0F * muffleLerp;
        float yawSway = MathHelper.cos(ticks * 0.03F) * 2.0F * muffleLerp;
        float pitchSway = MathHelper.sin(ticks * 0.04F) * 2.0F * muffleLerp;

        yawPitchRoll[0] = lastCameraYaw + yawSway;
        yawPitchRoll[1] = lastCameraPitch + pitchSway;
        yawPitchRoll[2] = roll;
    }

    public static double modifyFov(double fov, float partialTick, long gameTime) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return fov;
        if (!client.player.hasStatusEffect(ModEffects.CONCUSSION)) return fov;
        if (muffleLerp <= 0.1F) return fov;

        float ticks = (float) gameTime + partialTick;
        float fovOffset = MathHelper.sin(ticks * 0.08F) * 3.0F * muffleLerp;
        
        return fov + fovOffset;
    }
}
