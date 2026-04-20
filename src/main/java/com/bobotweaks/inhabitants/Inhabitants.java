package com.bobotweaks.inhabitants;

import com.bobotweaks.inhabitants.init.ModBlocks;
import com.bobotweaks.inhabitants.init.ModCreativeModeTabs;
import com.bobotweaks.inhabitants.init.ModItems;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inhabitants implements ModInitializer {
	public static final String MOD_ID = "inhabitants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModCreativeModeTabs.registerCreativeModeTabs();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		LOGGER.info("Inhabitants Initialized!");
	}
}