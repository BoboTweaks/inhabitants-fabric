package com.bobotweaks.inhabitants.client.animation;

import java.util.List;

import org.jspecify.annotations.Nullable;

public record FPVAnimationDef(
    int durationTicks,
    String easing,
    LoopMode loopMode,
    List<Keyframe> keyframes,
    @Nullable LoopTransform loopTransform,
    @Nullable VibrateConfig vibrate,
    @Nullable String continueTo,
    @Nullable String exitTo
) {

    public enum LoopMode {
        PLAY_ONCE, LOOP, HOLD;

        public static LoopMode from(String mode) {
            if (mode == null) return PLAY_ONCE;
            return switch (mode.toLowerCase()) {
                case "loop" -> LOOP;
                case "hold" -> HOLD;
                default -> PLAY_ONCE;
            };
        }
    }

    public record Keyframe(
        float time,
        float translateX, float translateY, float translateZ,
        float rotateX, float rotateY, float rotateZ,
        @Nullable String triggerKeyframe
    ) {}

    public record LoopTransform(
        float translateX, float translateY, float translateZ,
        float rotateX, float rotateY, float rotateZ
    ) {}

    public record VibrateConfig(
        float amplitudeX, float amplitudeY,
        float freqX, float freqY
    ) {}
}
