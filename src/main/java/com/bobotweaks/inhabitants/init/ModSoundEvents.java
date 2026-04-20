package com.bobotweaks.inhabitants.init;

import com.bobotweaks.inhabitants.Inhabitants;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSoundEvents {

    public static final SoundEvent MUSIC_DISC_BOGRE = register("item.music_disc.bogre");
    public static final SoundEvent DRILL_LOOP = register("drill_loop");
    public static final SoundEvent CONCUSSION_BUZZ = register("concussion_buzz");
    public static final SoundEvent IMMATERIAL_INSIDE = register("immaterial_inside");
    public static final SoundEvent DRILL_START = register("drill_start");
    public static final SoundEvent DRILL_STOPPED = register("drill_stopped");
    public static final SoundEvent DRILL_DIG = register("drill_dig");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.of(Inhabitants.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {
        Inhabitants.LOGGER.info("Registering Mod Sound Events for " + Inhabitants.MOD_ID);
    }
}
