package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public record PonymealSigilEffect() implements ISigilEffect {
    public static final MapCodec<PonymealSigilEffect> CODEC = MapCodec.unit(PonymealSigilEffect::new);

    private static final int BLOOM_ATTEMPTS = 64;
    private static final int PLANT_GROWTH_PARTICLES = 15;

    private static List<Block> flowers;

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos pos, Direction side, Vec3 hitVec) {
        BlockState state = level.getBlockState(pos);

        if (isFlower(state)) {
            if (!level.isClientSide()) {
                duplicate(level, pos, state);
            }
            return true;
        }

        if (state.is(Blocks.GRASS_BLOCK) && level.getBlockState(pos.above()).isAir()) {
            if (!level.isClientSide()) {
                bloom(level, pos);
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos.above(), PLANT_GROWTH_PARTICLES);
            }
            return true;
        }

        ItemStack boneMeal = new ItemStack(Items.BONE_MEAL);
        if (BoneMealItem.applyBonemeal(boneMeal, level, pos, player)) {
            if (!level.isClientSide()) {
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, PLANT_GROWTH_PARTICLES);
            }
            return true;
        }

        BlockPos relative = pos.relative(side);
        if (level.getBlockState(pos).isFaceSturdy(level, pos, side) && BoneMealItem.growWaterPlant(boneMeal, level, relative, side)) {
            if (!level.isClientSide()) {
                level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, relative, PLANT_GROWTH_PARTICLES);
            }
            return true;
        }
        return false;
    }

    public static boolean isFlower(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) {
            return false;
        }
        return block instanceof FlowerBlock || state.is(BlockTags.FLOWERS) || state.is(BlockTags.SMALL_FLOWERS);
    }

    private static void duplicate(Level level, BlockPos pos, BlockState state) {
        Item item = state.getBlock().asItem();
        if (item == Items.AIR) {
            return;
        }
        Block.popResource(level, pos, new ItemStack(item));
        level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, PLANT_GROWTH_PARTICLES);
    }

    private static void bloom(Level level, BlockPos grass) {
        List<Block> candidates = flowers();
        if (candidates.isEmpty()) {
            return;
        }
        RandomSource random = level.getRandom();
        BlockPos start = grass.above();

        for (int attempt = 0; attempt < BLOOM_ATTEMPTS; attempt++) {
            BlockPos target = start;
            boolean valid = true;
            for (int step = 0; step < attempt / 16; step++) {
                target = target.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!level.getBlockState(target.below()).is(Blocks.GRASS_BLOCK) || level.getBlockState(target).isCollisionShapeFullBlock(level, target)) {
                    valid = false;
                    break;
                }
            }
            if (!valid || !level.getBlockState(target).isAir() || !level.getBlockState(target.below()).is(Blocks.GRASS_BLOCK)) {
                continue;
            }

            Block flower = candidates.get(random.nextInt(candidates.size()));
            BlockState flowerState = flower.defaultBlockState();
            if (!flowerState.canSurvive(level, target)) {
                continue;
            }
            if (flower instanceof DoublePlantBlock) {
                if (level.getBlockState(target.above()).isAir()) {
                    DoublePlantBlock.placeAt(level, flowerState, target, Block.UPDATE_ALL);
                }
            } else {
                level.setBlock(target, flowerState, Block.UPDATE_ALL);
            }
        }
    }

    private static List<Block> flowers() {
        if (flowers == null) {
            List<Block> found = new ArrayList<>();
            for (Block block : BuiltInRegistries.BLOCK) {
                if (block == Blocks.WITHER_ROSE) {
                    continue;
                }
                BlockState state = block.defaultBlockState();
                boolean smallFlower = block instanceof FlowerBlock;
                boolean tallFlower = block instanceof DoublePlantBlock && state.is(BlockTags.FLOWERS);
                if (smallFlower || tallFlower) {
                    found.add(block);
                }
            }
            flowers = List.copyOf(found);
        }
        return flowers;
    }
}
