package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.NotEnoughSigils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record HarvestSigilEffect(int radius, int height, int interval, int maxPerPulse, TagKey<Block> excluded) implements ISigilEffect {
    public static final int DEFAULT_RADIUS = 5;
    public static final int DEFAULT_HEIGHT = 2;
    public static final int DEFAULT_INTERVAL = 60;
    public static final int DEFAULT_MAX_PER_PULSE = 64;
    public static final TagKey<Block> DEFAULT_EXCLUDED = TagKey.create(Registries.BLOCK, NotEnoughSigils.rl("harvest_excluded"));

    public static final HarvestSigilEffect DEFAULT = new HarvestSigilEffect(DEFAULT_RADIUS, DEFAULT_HEIGHT, DEFAULT_INTERVAL,
            DEFAULT_MAX_PER_PULSE, DEFAULT_EXCLUDED);

    public static final MapCodec<HarvestSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("radius", DEFAULT_RADIUS).forGetter(HarvestSigilEffect::radius),
            Codec.INT.optionalFieldOf("height", DEFAULT_HEIGHT).forGetter(HarvestSigilEffect::height),
            Codec.INT.optionalFieldOf("interval", DEFAULT_INTERVAL).forGetter(HarvestSigilEffect::interval),
            Codec.INT.optionalFieldOf("max_per_pulse", DEFAULT_MAX_PER_PULSE).forGetter(HarvestSigilEffect::maxPerPulse),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("excluded", DEFAULT_EXCLUDED).forGetter(HarvestSigilEffect::excluded)
    ).apply(instance, HarvestSigilEffect::new));

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        if (!(level instanceof ServerLevel serverLevel) || player.isSpectator()) {
            return;
        }
        if (interval > 0 && level.getGameTime() % interval != 0) {
            return;
        }
        BlockPos center = player.blockPosition();
        int harvested = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -height, -radius), center.offset(radius, height, radius))) {
            if (harvested >= maxPerPulse) {
                break;
            }
            BlockState state = serverLevel.getBlockState(pos);
            if (state.isAir() || state.is(excluded) || !serverLevel.mayInteract(player, pos)) {
                continue;
            }
            if (harvest(serverLevel, player, pos.immutable(), state)) {
                harvested++;
            }
        }
        if (harvested > 0) {
            serverLevel.playSound(null, center, SoundEvents.CROP_BREAK, SoundSource.PLAYERS, 0.6F, 1.2F);
        }
    }

    private static boolean harvest(ServerLevel level, Player player, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            if (!crop.isMaxAge(state)) {
                return false;
            }
            Item seed = block.getCloneItemStack(level, pos, state).getItem();
            return replant(level, player, pos, state, crop.getStateForAge(0), seed);
        }
        if (block instanceof NetherWartBlock) {
            if (state.getValue(NetherWartBlock.AGE) < 3) {
                return false;
            }
            return replant(level, player, pos, state, state.setValue(NetherWartBlock.AGE, 0), Items.NETHER_WART);
        }
        if (block instanceof CocoaBlock) {
            if (state.getValue(CocoaBlock.AGE) < 2) {
                return false;
            }
            return replant(level, player, pos, state, state.setValue(CocoaBlock.AGE, 0), Items.COCOA_BEANS);
        }
        if (block instanceof SweetBerryBushBlock) {
            if (state.getValue(SweetBerryBushBlock.AGE) < 3) {
                return false;
            }
            int count = 2 + level.getRandom().nextInt(2);
            give(level, player, pos, new ItemStack(Items.SWEET_BERRIES, count));
            level.setBlock(pos, state.setValue(SweetBerryBushBlock.AGE, 1), Block.UPDATE_CLIENTS);
            sparkle(level, pos);
            return true;
        }
        return false;
    }

    private static boolean replant(ServerLevel level, Player player, BlockPos pos, BlockState state, BlockState replanted, Item seed) {
        List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, player.getMainHandItem());
        boolean consumed = false;
        for (ItemStack drop : drops) {
            if (!consumed && drop.is(seed)) {
                drop.shrink(1);
                consumed = true;
            }
            if (!drop.isEmpty()) {
                give(level, player, pos, drop);
            }
        }
        level.setBlock(pos, replanted, Block.UPDATE_CLIENTS);
        sparkle(level, pos);
        return true;
    }

    private static void give(ServerLevel level, Player player, BlockPos pos, ItemStack stack) {
        if (!player.getInventory().add(stack) && !stack.isEmpty()) {
            Block.popResource(level, pos, stack);
        }
    }

    private static void sparkle(ServerLevel level, BlockPos pos) {
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 3, 0.25D, 0.25D, 0.25D, 0.0D);
    }
}
