package com.titammods.not_enough_sigils.saereth;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.Map;
import java.util.Optional;

public record SaerethSettings(int linkRange, int scanInterval, int drawTicks, int placeTicks, int castTimeout) {
    public static final String DIRECTORY = "saereth";
    public static final ResourceLocation FILE = NotEnoughSigils.rl("settings");

    public static final SaerethSettings DEFAULT = new SaerethSettings(8, 40, 40, 15, 600);

    public static final Codec<SaerethSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            field(Codec.INT, "link_range", DEFAULT.linkRange()).forGetter(SaerethSettings::linkRange),
            field(Codec.INT, "scan_interval", DEFAULT.scanInterval()).forGetter(SaerethSettings::scanInterval),
            field(Codec.INT, "draw_ticks", DEFAULT.drawTicks()).forGetter(SaerethSettings::drawTicks),
            field(Codec.INT, "place_ticks", DEFAULT.placeTicks()).forGetter(SaerethSettings::placeTicks),
            field(Codec.INT, "cast_timeout", DEFAULT.castTimeout()).forGetter(SaerethSettings::castTimeout)
    ).apply(instance, SaerethSettings::new));

    private static SaerethSettings current = DEFAULT;

    public static SaerethSettings get() {
        return current;
    }

    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new Loader());
    }

    private static <A> MapCodec<A> field(Codec<A> codec, String name, A fallback) {
        return codec.optionalFieldOf(name).xmap(value -> value.orElse(fallback), Optional::of);
    }

    private static class Loader extends SimpleJsonResourceReloadListener {
        private Loader() {
            super(new Gson(), DIRECTORY);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager resourceManager, ProfilerFiller profiler) {
            JsonElement json = entries.get(FILE);
            current = json == null ? DEFAULT : CODEC.parse(JsonOps.INSTANCE, json).result().orElse(DEFAULT);
        }
    }
}
