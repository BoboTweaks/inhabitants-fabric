package com.bobotweaks.inhabitants;

import com.bobotweaks.inhabitants.init.*;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inhabitants implements ModInitializer {
	public static final String MOD_ID = "inhabitants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModCreativeModeTabs.register();
		ModItems.register();
		ModBlocks.register();
		ModEntities.register();
		ModBlockEntities.register();
		ModSoundEvents.register();

		LOGGER.info("Inhabitants Initialized!");
	}
}