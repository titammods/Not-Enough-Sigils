package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.MapCodec;
import com.titammods.animus_extended.network.VeinSeekerPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record VeinSeekerSigilEffect() implements ISigilEffect {
    public static final MapCodec<VeinSeekerSigilEffect> CODEC = MapCodec.unit(VeinSeekerSigilEffect::new);

    private static final int RADIUS = 12;
    private static final int MAX_RESULTS = 512;
    private static final int DURATION_TICKS = 200;
    private static final int COOLDOWN_TICKS = 100;

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

        BlockPos center = player.blockPosition();
        List<BlockPos> found = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -RADIUS, -RADIUS), center.offset(RADIUS, RADIUS, RADIUS))) {
            if (level.getBlockState(pos).is(Tags.Blocks.ORES)) {
                found.add(pos.immutable());
            }
        }
        found.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
        if (found.size() > MAX_RESULTS) {
            found = new ArrayList<>(found.subList(0, MAX_RESULTS));
        }

        PacketDistributor.sendToPlayer(serverPlayer, new VeinSeekerPayload(found, DURATION_TICKS));
        player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }
}
