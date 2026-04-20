package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;
import com.bobotweaks.inhabitants.items.GiantBoneItem;
import com.bobotweaks.inhabitants.items.food.FishSnotChowderItem;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

    public static final Item CREATIVE_TAB =
        registerItem("creative_tab", Item::new);

    public static final Item IMPALER_SPIKE =
        registerItem("impaler_spike", Item::new);

    public static final Item GIANT_BONE =
        registerItem("giant_bone", GiantBoneItem::new);

    public static final Item FISH_SNOT_CHOWDER =
        registerItem("fish_snot_chowder", FishSnotChowderItem::new);

    private static Item registerItem(String name, Function<Item.Settings, Item> itemFactory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Inhabitants.MOD_ID, name));
        Item.Settings settings = new Item.Settings().registryKey(key);
        return Registry.register(Registries.ITEM, key, itemFactory.apply(settings));
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Items for " + Inhabitants.MOD_ID);
    }
}
