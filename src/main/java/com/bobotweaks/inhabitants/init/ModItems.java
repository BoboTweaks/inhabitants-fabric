package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;
import com.bobotweaks.inhabitants.items.GiantBoneItem;
import com.bobotweaks.inhabitants.items.food.FishSnotChowderItem;
import com.bobotweaks.inhabitants.items.SpikeDrillItem;
import com.bobotweaks.inhabitants.items.food.BakedBrainsItem;

import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.component.type.*;

import java.util.List;
import java.util.function.*;

public class ModItems {

    public static final Item CREATIVE_TAB =
        registerItem("creative_tab", Item::new);
    // --- Spawn Eggs ---
    public static final Item WARPED_CLAM =
        registerItem("warped_clam", settings -> new Item(settings.maxCount(1)));
    // --- Food ---
    
    public static final Item FISH_SNOT_CHOWDER =
        registerItem("fish_snot_chowder", FishSnotChowderItem::new);

    public static final Item UNCANNY_POTTAGE =
        registerItem("uncanny_pottage", settings -> new Item(settings.food(
            new FoodComponent.Builder()
            .nutrition(5)
            .saturationModifier(0.6f)
            .build())
            .maxCount(1)));

    public static final Item MARINATED_SPIDER =
        registerItem("marinated_spider", settings -> new Item(settings.food(
            new FoodComponent.Builder()
            .nutrition(6)
            .saturationModifier(0.6f)
            .build())
            .maxCount(1)));

    public static final Item BAKED_BRAINS =
        registerItem("baked_brains", BakedBrainsItem::new);

    public static final Item DIMENSIONAL_SERVING =
        registerItem("dimensional_serving", settings -> new Item(settings.food(
            new FoodComponent.Builder()
            .nutrition(2)
            .saturationModifier(0.2f)
            .build())
            .maxCount(1)));
    // --- Tools ---
    public static final Item GIANT_BONE =
        registerItem("giant_bone", GiantBoneItem::new);

    public static final Item SPIKE_DRILL =
        registerItem("spike_drill", SpikeDrillItem::new);
    
    public static final Item IMPALER_SPIKE =
        registerItem("impaler_spike", Item::new);

    // --- Music Discs ---
    public static final Item MUSIC_DISC_BOGRE =
        registerItem("music_disc_bogre", settings -> new Item(
            settings
            .maxCount(1)
            .jukeboxPlayable(RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(Inhabitants.MOD_ID, "bogre")))));

    private static Item registerItem(String name, Function<Item.Settings, Item> itemFactory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Inhabitants.MOD_ID, name));
        Item.Settings settings = new Item.Settings().registryKey(key);
        return Registry.register(Registries.ITEM, key, itemFactory.apply(settings));
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Items for " + Inhabitants.MOD_ID);
    }
}
