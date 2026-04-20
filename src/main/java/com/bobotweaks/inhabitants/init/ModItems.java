package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final RegistryKey<Item> CREATIVE_TAB_KEY =
        RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Inhabitants.MOD_ID, "creative_tab"));
    public static final Item CREATIVE_TAB =
        registerItem(CREATIVE_TAB_KEY, new Item(new Item.Settings().registryKey(CREATIVE_TAB_KEY)));

    private static Item registerItem(RegistryKey<Item> key, Item item) {
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Items for " + Inhabitants.MOD_ID);
    }
}
