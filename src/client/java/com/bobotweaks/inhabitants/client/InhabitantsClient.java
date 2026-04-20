package com.bobotweaks.inhabitants.client;

import com.bobotweaks.inhabitants.items.SpikeDrillItem;
import com.bobotweaks.inhabitants.networking.payloads.DrillDamagePayload;
import com.bobotweaks.inhabitants.client.animation.FPVAnimationManager;
import com.bobotweaks.inhabitants.init.ModParticleTypes;
import com.bobotweaks.inhabitants.client.particle.AbracadabraParticle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import net.minecraft.resource.ResourceType;

public class InhabitantsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// FPV Animations
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
			.registerReloadListener(FPVAnimationManager.INSTANCE);

		// Particles
		ParticleFactoryRegistry.getInstance().register(ModParticleTypes.ABRACADABRA, AbracadabraParticle.Factory::new);

		// Networking
		SpikeDrillItem.clientPacketSender = () -> ClientPlayNetworking.send(new DrillDamagePayload());
	}
}