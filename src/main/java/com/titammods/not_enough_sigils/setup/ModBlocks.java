package com.titammods.not_enough_sigils.setup;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NotEnoughSigils.MODID);

    public static final DeferredBlock<Block> RUNE_3_CAPACITY_AUGMENTED = rune("rune_3_capacity_augmented");
    public static final DeferredBlock<Block> RUNE_3_SPEED = rune("rune_3_speed");
    public static final DeferredBlock<Block> RUNE_ETERNITY = rune("rune_eternity");

    private static DeferredBlock<Block> rune(String name) {
        return BLOCKS.registerSimpleBlock(name, BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0F, 8.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> 12));
    }

    public static Block[] runes() {
        return new Block[]{RUNE_3_CAPACITY_AUGMENTED.get(), RUNE_3_SPEED.get(), RUNE_ETERNITY.get()};
    }
}
