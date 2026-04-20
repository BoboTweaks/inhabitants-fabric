package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;

import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.mojang.serialization.Codec;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<Integer> TEMPERATURE = register(
        "temperature", builder -> builder.codec(Codec.INT).packetCodec(PacketCodecs.VAR_INT));
    
    public static final ComponentType<Long> LAST_TICK = register(
        "last_tick", builder -> builder.codec(Codec.LONG).packetCodec(PacketCodecs.VAR_LONG));

    private static <T> ComponentType<T> register(
        String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE, 
            Identifier.of(Inhabitants.MOD_ID, name), 
            (builderOperator.apply(ComponentType.builder())).build());
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Data Component Types for " + Inhabitants.MOD_ID);
    }
}
