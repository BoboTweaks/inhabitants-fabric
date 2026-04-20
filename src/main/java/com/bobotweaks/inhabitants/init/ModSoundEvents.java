package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    public static final SoundEvent MUSIC_DISC_BOGRE = register("item.music_disc.bogre");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of(Inhabitants.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Sound Events for " + Inhabitants.MOD_ID);
    }
}
