package com.bobotweaks.inhabitants.client.audio;

import com.bobotweaks.inhabitants.Inhabitants;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.openal.AL10;

/**
 * Manages OpenAL audio effects for the Inhabitants mod.
 * Handles the initialization and configuration of the Lowpass filter.
 */
public class ModAudio {

    public static int lowpassFilterId = -1;
    public static boolean efxSupported = false;
    
    public static void init() {
        long context = ALC10.alcGetCurrentContext();
        if (context == 0L) {
            Inhabitants.LOGGER.warn("[Inhabitants] Audio: No OpenAL context available for EFX init");
            return;
        }

        long device = ALC10.alcGetContextsDevice(context);
        if (device == 0L) {
            Inhabitants.LOGGER.warn("[Inhabitants] Audio: No OpenAL device available for EFX init");
            return;
        }

        efxSupported = ALC10.alcIsExtensionPresent(device, "ALC_EXT_EFX");

        if (efxSupported) {
            lowpassFilterId = EXTEfx.alGenFilters();
            if (lowpassFilterId != -1) {
                EXTEfx.alFilteri(lowpassFilterId, EXTEfx.AL_FILTER_TYPE, EXTEfx.AL_FILTER_LOWPASS);
                
                EXTEfx.alFilterf(lowpassFilterId, EXTEfx.AL_LOWPASS_GAIN, 1.0F);
                EXTEfx.alFilterf(lowpassFilterId, EXTEfx.AL_LOWPASS_GAINHF, 1.0F);
                
                Inhabitants.LOGGER.info("[Inhabitants] Audio: OpenAL EFX Lowpass initialized (ID: {})", lowpassFilterId);
            }
        } else {
            Inhabitants.LOGGER.warn("[Inhabitants] Audio: OpenAL EXT_EFX is not supported on this hardware");
        }
    }

    /**
     * Updates the lowpass filter parameters based on intensity.
     * @param intensity 0.0 (normal) to 1.0 (fully muffled)
     */
    public static void updateFilter(float intensity) {
        if (efxSupported && lowpassFilterId != -1) {

            float gain = MathHelper.lerp(intensity, 1.0F, 0.4F);
            float gainHF = MathHelper.lerp(intensity, 1.0F, 0.005F);

            EXTEfx.alFilterf(lowpassFilterId, EXTEfx.AL_LOWPASS_GAIN, gain);
            EXTEfx.alFilterf(lowpassFilterId, EXTEfx.AL_LOWPASS_GAINHF, gainHF);
        }
    }

    /**
     * Deletes the lowpass filter and cleans up OpenAL resources.
     */
    public static void cleanup() {
        if (efxSupported && lowpassFilterId != -1) {
            EXTEfx.alDeleteFilters(lowpassFilterId);
            lowpassFilterId = -1;
            
            Inhabitants.LOGGER.info("[Inhabitants] Audio: OpenAL EFX cleaned up");
        }
    }
}
