package com.bobotweaks.inhabitants.networking;

import com.bobotweaks.inhabitants.Inhabitants;
import com.bobotweaks.inhabitants.networking.handlers.DrillDamageHandler;
import com.bobotweaks.inhabitants.networking.payloads.DrillDamagePayload;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModMessages {
    public static void register() {
        Inhabitants.LOGGER.info("Registering Networking for " + Inhabitants.MOD_ID);

        // Payloads
        PayloadTypeRegistry.playC2S().register(DrillDamagePayload.ID, DrillDamagePayload.CODEC);

        // Server Receivers
        ServerPlayNetworking.registerGlobalReceiver(DrillDamagePayload.ID, DrillDamageHandler::receive);
    }
}
