package com.titammods.animus_extended.sigil.config;

import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.mojang.serialization.Codec;

import java.util.Map;

public class SpiritusProfiles {

    public static <T> Codec<Map<SpiritusType, T>> codec(Codec<T> profileCodec) {
        return Codec.unboundedMap(SpiritusType.CODEC, profileCodec);
    }

    public static <T> T resolve(Map<SpiritusType, T> overrides, Map<SpiritusType, T> defaults, SpiritusType type, T fallback) {
        T profile = overrides.get(type);
        if (profile == null) {
            profile = defaults.get(type);
        }
        if (profile == null) {
            profile = overrides.get(SpiritusType.RAW);
        }
        if (profile == null) {
            profile = defaults.get(SpiritusType.RAW);
        }
        return profile != null ? profile : fallback;
    }
}
