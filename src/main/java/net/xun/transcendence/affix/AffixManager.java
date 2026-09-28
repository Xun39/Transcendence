package net.xun.transcendence.affix;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.xun.transcendence.Transcendence;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AffixManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();

    public static final AffixManager INSTANCE = new AffixManager();

    private volatile Map<ResourceLocation, Affix> affixes = Map.of();

    private AffixManager() {
        super(GSON, "affixes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Affix> loaded = new LinkedHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            ResourceLocation id = entry.getKey();

            Affix.CODEC.parse(JsonOps.INSTANCE, entry.getValue())
                    .resultOrPartial(error -> Transcendence.LOGGER.error("Failed to parse affix {}: {}", id, error))
                    .ifPresent(affix -> loaded.put(id, affix));
        }
        affixes = Collections.unmodifiableMap(loaded);

        Transcendence.LOGGER.info("Loaded {} Transcendence affixes", loaded.size());
    }

    public Map<ResourceLocation, Affix> all() {
        return affixes;
    }

    public List<Map.Entry<ResourceLocation, Affix>> entries() {
        return List.copyOf(affixes.entrySet());
    }
}
