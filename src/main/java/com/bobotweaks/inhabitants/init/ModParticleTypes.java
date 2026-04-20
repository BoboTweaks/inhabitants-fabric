package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticleTypes {

    public static final SimpleParticleType ABRACADABRA = register("abracadabra", false);

    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE,
            Identifier.of(Inhabitants.MOD_ID, name), FabricParticleTypes.simple(alwaysShow));
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Particle Types for " + Inhabitants.MOD_ID);
    }
}
