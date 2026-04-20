package com.bobotweaks.inhabitants;

import com.bobotweaks.inhabitants.init.*;
import com.bobotweaks.inhabitants.networking.ModMessages;
import com.bobotweaks.inhabitants.networking.handlers.DrillDamageHandler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inhabitants implements ModInitializer {
	public static final String MOD_ID = "inhabitants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// networking
		ModMessages.register();
		ServerTickEvents.END_SERVER_TICK.register(DrillDamageHandler::tickServer);

		// creative tabs
		ModCreativeModeTabs.register();

		// data components
		ModDataComponentTypes.register();

		ModItems.register();
		ModBlocks.register();
		ModEntities.register();
		ModBlockEntities.register();
		ModSoundEvents.register();

		LOGGER.info("Inhabitants Initialized!");
	}
}