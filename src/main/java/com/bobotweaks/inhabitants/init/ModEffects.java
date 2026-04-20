package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;
import com.bobotweaks.inhabitants.effects.ReverseGrowthStatusEffect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static final RegistryEntry<StatusEffect> REVERSE_GROWTH = register("reverse_growth", 
        new ReverseGrowthStatusEffect(StatusEffectCategory.HARMFUL, 0x808080)
            .addAttributeModifier(EntityAttributes.SCALE, 
                Identifier.of(Inhabitants.MOD_ID, "effect.reverse_growth"), 
                -0.5, 
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT,
            Identifier.of(Inhabitants.MOD_ID, name), statusEffect);
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Status Effects for " + Inhabitants.MOD_ID);
    }
}
