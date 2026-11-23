package com.titammods.not_enough_sigils.setup;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> PONYMEAL_EXCLUDED = TagKey.create(Registries.BLOCK, NotEnoughSigils.rl("ponymeal_excluded"));
        public static final TagKey<Block> T7_CAPSTONES_RUINA = tag("altar/t7_capstones_ruina");
        public static final TagKey<Block> T7_CAPSTONES_NIHILUM = tag("altar/t7_capstones_nihilum");
        public static final TagKey<Block> T7_CAPSTONES_INVICTUS = tag("altar/t7_capstones_invictus");
        public static final TagKey<Block> T7_CAPSTONES_VINDICTA = tag("altar/t7_capstones_vindicta");
        public static final TagKey<Block> T8_CAPSTONES = tag("altar/t8_capstones");
        public static final TagKey<Block> T8_CORNERSTONES = tag("altar/t8_cornerstones");

        private static TagKey<Block> tag(String path) {
            return TagKey.create(Registries.BLOCK, NotEnoughSigils.rl(path));
        }
    }
}
