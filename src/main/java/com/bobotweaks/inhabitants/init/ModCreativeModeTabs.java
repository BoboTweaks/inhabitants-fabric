package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModCreativeModeTabs {

    public static final RegistryKey<ItemGroup> INHABITANTS_TAB_KEY =
        RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Inhabitants.MOD_ID, "inhabitants_tab"));

    public static final ItemGroup INHABITANTS_TAB = Registry.register(Registries.ITEM_GROUP,
        INHABITANTS_TAB_KEY,
        FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.CREATIVE_TAB))
            .displayName(Text.translatable("creativetab.inhabitants_tab"))
            .entries((displayContext, entries) -> {
                
                entries.add(ModItems.IMPALER_SPIKE);
                entries.add(ModItems.GIANT_BONE);
                entries.add(ModItems.FISH_SNOT_CHOWDER);

            }).build());

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Creative Tabs for " + Inhabitants.MOD_ID);
    }
}
