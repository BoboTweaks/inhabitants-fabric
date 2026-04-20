package com.bobotweaks.inhabitants.client;

import com.bobotweaks.inhabitants.items.SpikeDrillItem;
import com.bobotweaks.inhabitants.networking.payloads.DrillDamagePayload;
import com.bobotweaks.inhabitants.client.animation.FPVAnimationManager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.resource.ResourceType;

public class InhabitantsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
			.registerReloadListener(FPVAnimationManager.INSTANCE);

		SpikeDrillItem.clientPacketSender = () -> ClientPlayNetworking.send(new DrillDamagePayload());
	}
}