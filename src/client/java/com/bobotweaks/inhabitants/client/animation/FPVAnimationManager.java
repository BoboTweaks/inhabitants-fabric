package com.bobotweaks.inhabitants.client.animation;

import com.bobotweaks.inhabitants.Inhabitants;

import com.google.gson.*;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.*;

public class FPVAnimationManager implements SimpleSynchronousResourceReloadListener {

    public static final FPVAnimationManager INSTANCE = new FPVAnimationManager();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String FOLDER = "item_animations";

    private Map<Identifier, FPVAnimationDef> cache = new HashMap<>();

    private FPVAnimationManager() {}

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Inhabitants.MOD_ID, "fpv_animations");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<Identifier, FPVAnimationDef> loaded = new HashMap<>();

        for (Map.Entry<Identifier, Resource> entry : manager.findResources(
            FOLDER, id -> id.getPath().endsWith(".json")).entrySet()) {
            
            Identifier location = entry.getKey();
            Resource resource = entry.getValue();

            try (BufferedReader reader = resource.getReader()) {
                
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                FPVAnimationDef def = parse(json);

                String path = location.getPath().replace(FOLDER + "/", "").replace(".json", "");
                Identifier key = Identifier.of(location.getNamespace(), path);

                loaded.put(key, def);
            } catch (Exception e) {
                Inhabitants.LOGGER.error("Failed to load hand animation: " + location, e);
            }
        }

        this.cache = loaded;
        Inhabitants.LOGGER.info("Loaded " + loaded.size() + " hand animation's");
    }

    public FPVAnimationDef get(Identifier location) {
        return cache.get(location);
    }

    public FPVAnimationDef get(String path) {
        return get(Identifier.of(Inhabitants.MOD_ID, path));
    }

    // --- [Parsing] ---
    private FPVAnimationDef parse(JsonObject json) {
        int durationTicks = json.has("duration_ticks") ? json.get("duration_ticks").getAsInt() : -1;
        String easing = json.has("easing") ? json.get("easing").getAsString() : "linear";

        FPVAnimationDef.LoopMode loopMode = FPVAnimationDef.LoopMode.from(
                json.has("loop_mode") ? json.get("loop_mode").getAsString() : null
        );
        List<FPVAnimationDef.Keyframe> keyframes = new ArrayList<>();

        if (json.has("keyframes")) {
            JsonArray kfArray = json.getAsJsonArray("keyframes");
            for (JsonElement el : kfArray) {
                keyframes.add(parseKeyframe(el.getAsJsonObject()));
            }
        }

        FPVAnimationDef.LoopTransform loopTransform = null;
        if (json.has("translate") || json.has("rotate_x")) {
            float[] t = parseVec3(json, "translate");
            loopTransform = new FPVAnimationDef.LoopTransform(
                    t[0], t[1], t[2],
                    getFloat(json, "rotate_x"),
                    getFloat(json, "rotate_y"),
                    getFloat(json, "rotate_z")
            );
        }

        FPVAnimationDef.VibrateConfig vibrate = null;
        if (json.has("vibrate")) {
            JsonObject v = json.getAsJsonObject("vibrate");
            vibrate = new FPVAnimationDef.VibrateConfig(
                    getFloat(v, "amplitude_x"),
                    getFloat(v, "amplitude_y"),
                    getFloat(v, "freq_x"),
                    getFloat(v, "freq_y")
            );
        }

        return new FPVAnimationDef(
            durationTicks,
            easing,
            loopMode,
            keyframes,
            loopTransform,
            vibrate,
            json.has("continue_to") ? json.get("continue_to").getAsString() : null,
            json.has("exit_to") ? json.get("exit_to").getAsString() : null
        );
    }

    private FPVAnimationDef.Keyframe parseKeyframe(JsonObject json) {
        float time = getFloat(json, "time");
        float[] t = parseVec3(json, "translate");
        return new FPVAnimationDef.Keyframe(
                time, t[0], t[1], t[2],
                getFloat(json, "rotate_x"),
                getFloat(json, "rotate_y"),
                getFloat(json, "rotate_z"),
                json.has("trigger_keyframe") ? json.get("trigger_keyframe").getAsString() : null
        );
    }

    private static float[] parseVec3(JsonObject json, String key) {
        if (!json.has(key)) return new float[]{0, 0, 0};
        JsonArray arr = json.getAsJsonArray(key);
        return new float[]{
                arr.get(0).getAsFloat(),
                arr.get(1).getAsFloat(),
                arr.get(2).getAsFloat()
        };
    }

    private static float getFloat(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsFloat() : 0f;
    }
}
