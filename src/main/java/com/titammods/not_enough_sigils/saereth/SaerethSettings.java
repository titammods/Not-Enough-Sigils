package com.titammods.not_enough_sigils.saereth;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.io.Reader;
import java.util.Optional;

public record SaerethSettings(int linkRange, int scanInterval, int drawTicks, int placeTicks, int castTimeout) {
    public static final String DIRECTORY = "saereth";
    public static final Identifier FILE = NotEnoughSigils.rl("settings");

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

    public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(NotEnoughSigils.rl(DIRECTORY), new Loader());
    }

    private static <A> MapCodec<A> field(Codec<A> codec, String name, A fallback) {
        return codec.optionalFieldOf(name).xmap(value -> value.orElse(fallback), Optional::of);
    }

    private static class Loader implements ResourceManagerReloadListener {
        private static final Identifier PATH = FILE.withPath(DIRECTORY + "/" + FILE.getPath() + ".json");

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            current = read(resourceManager.getResource(PATH));
        }

        private static SaerethSettings read(Optional<Resource> resource) {
            if (resource.isEmpty()) {
                return DEFAULT;
            }
            try (Reader reader = resource.get().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                return CODEC.parse(JsonOps.INSTANCE, json).result().orElse(DEFAULT);
            } catch (Exception exception) {
                return DEFAULT;
            }
        }
    }
}
