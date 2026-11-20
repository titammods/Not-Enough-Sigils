package com.titammods.animus_extended.setup;

import com.titammods.animus_extended.AnimusExtended;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> PONYMEAL_EXCLUDED = TagKey.create(Registries.BLOCK, AnimusExtended.rl("ponymeal_excluded"));
    }
}
