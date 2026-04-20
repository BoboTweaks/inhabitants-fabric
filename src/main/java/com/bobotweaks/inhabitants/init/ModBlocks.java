package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModBlocks {

    private static Block registerBlock(String name, Block block) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Inhabitants.MOD_ID, name));

        registerBlockItem(name, block);

        return Registry.register(Registries.BLOCK, key, block);
    }

    private static Item registerBlockItem(String name, Block block) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Inhabitants.MOD_ID, name));

        return Registry.register(Registries.ITEM, key,
            new BlockItem(block, new Item.Settings().registryKey(key)));
    }

    public static void registerModBlocks() {
        
    }
}
