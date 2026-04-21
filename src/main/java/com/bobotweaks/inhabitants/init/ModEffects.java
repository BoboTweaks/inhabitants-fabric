package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;
import com.bobotweaks.inhabitants.effects.*;

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

    public static final RegistryEntry<StatusEffect> STICKY_LEGS = register("sticky_legs", 
        new StickyLegsStatusEffect(StatusEffectCategory.BENEFICIAL, 0xf6f6f6));

    public static final RegistryEntry<StatusEffect> IMMATERIAL = register("immaterial", 
        new ImmaterialStatusEffect(StatusEffectCategory.BENEFICIAL, 0x562D4C));

    public static final RegistryEntry<StatusEffect> CONCUSSION = register("concussion", 
        new ConcussionStatusEffect(StatusEffectCategory.HARMFUL, 0x808080));

    public static final RegistryEntry<StatusEffect> UNDEAD_DISGUISE = register("undead_disguise", 
        new UndeadDisguiseStatusEffect(StatusEffectCategory.BENEFICIAL, 0x874712));

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT,
            Identifier.of(Inhabitants.MOD_ID, name), statusEffect);
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Status Effects for " + Inhabitants.MOD_ID);
    }
}
