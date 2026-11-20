package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.network.VeinSeekerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record VeinSeekerSigilEffect(int radius, int maxResults, int duration, int cooldown, TagKey<Block> targets) implements ISigilEffect {
    public static final int DEFAULT_RADIUS = 12;
    public static final int DEFAULT_MAX_RESULTS = 512;
    public static final int DEFAULT_DURATION = 200;
    public static final int DEFAULT_COOLDOWN = 100;
    public static final int MAX_RADIUS = 32;

    public static final VeinSeekerSigilEffect DEFAULT = new VeinSeekerSigilEffect(DEFAULT_RADIUS, DEFAULT_MAX_RESULTS, DEFAULT_DURATION,
            DEFAULT_COOLDOWN, Tags.Blocks.ORES);

    public static final MapCodec<VeinSeekerSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("radius", DEFAULT_RADIUS).forGetter(VeinSeekerSigilEffect::radius),
            Codec.INT.optionalFieldOf("max_results", DEFAULT_MAX_RESULTS).forGetter(VeinSeekerSigilEffect::maxResults),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(VeinSeekerSigilEffect::duration),
            Codec.INT.optionalFieldOf("cooldown", DEFAULT_COOLDOWN).forGetter(VeinSeekerSigilEffect::cooldown),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("targets", Tags.Blocks.ORES).forGetter(VeinSeekerSigilEffect::targets)
    ).apply(instance, VeinSeekerSigilEffect::new));

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        return seek(level, player, stack);
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos blockPos, Direction side, Vec3 hitVec) {
        return seek(level, player, stack);
    }

    private boolean seek(Level level, Player player, ItemStack stack) {
        if (player == null || player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }

        int r = Math.max(1, Math.min(MAX_RADIUS, radius));
        int limit = Math.max(1, Math.min(VeinSeekerPayload.MAX_POSITIONS, maxResults));
        BlockPos center = player.blockPosition();
        List<BlockPos> found = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r))) {
            if (level.getBlockState(pos).is(targets)) {
                found.add(pos.immutable());
            }
        }
        found.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
        if (found.size() > limit) {
            found = new ArrayList<>(found.subList(0, limit));
        }

        PacketDistributor.sendToPlayer(serverPlayer, new VeinSeekerPayload(found, duration));
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), cooldown);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
