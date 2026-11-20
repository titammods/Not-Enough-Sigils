package com.titammods.not_enough_sigils.setup;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> PONYMEAL_EXCLUDED = TagKey.create(Registries.BLOCK, NotEnoughSigils.rl("ponymeal_excluded"));
    }
}
