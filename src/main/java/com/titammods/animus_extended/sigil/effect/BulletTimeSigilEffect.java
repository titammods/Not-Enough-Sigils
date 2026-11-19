package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.MapCodec;
import com.titammods.animus_extended.sigil.BulletTimeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record BulletTimeSigilEffect() implements ISigilEffect {
    public static final MapCodec<BulletTimeSigilEffect> CODEC = MapCodec.unit(BulletTimeSigilEffect::new);

    private static final int DURATION_TICKS = 200;
    private static final int COOLDOWN_TICKS = 600;

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        BulletTimeState.start(player, DURATION_TICKS);
        player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 0.6F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1.0D, player.getZ(), 60, 2.0D, 1.0D, 2.0D, 0.0D);
        }
        return true;
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        BulletTimeState.clear(playerId);
    }
}
