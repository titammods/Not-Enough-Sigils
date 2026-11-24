package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record SanctuarySigilEffect(double radius, boolean blockSpawners, int particleInterval) implements ISigilEffect {
    public static final double DEFAULT_RADIUS = 32.0D;
    public static final boolean DEFAULT_BLOCK_SPAWNERS = false;
    public static final int DEFAULT_PARTICLE_INTERVAL = 40;

    public static final SanctuarySigilEffect DEFAULT = new SanctuarySigilEffect(DEFAULT_RADIUS, DEFAULT_BLOCK_SPAWNERS, DEFAULT_PARTICLE_INTERVAL);

    public static final MapCodec<SanctuarySigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", DEFAULT_RADIUS).forGetter(SanctuarySigilEffect::radius),
            Codec.BOOL.optionalFieldOf("block_spawners", DEFAULT_BLOCK_SPAWNERS).forGetter(SanctuarySigilEffect::blockSpawners),
            Codec.INT.optionalFieldOf("particle_interval", DEFAULT_PARTICLE_INTERVAL).forGetter(SanctuarySigilEffect::particleInterval)
    ).apply(instance, SanctuarySigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.SANCTUARY, stack, this);
        if (particleInterval > 0 && level instanceof ServerLevel serverLevel && level.getGameTime() % particleInterval == 0) {
            for (int i = 0; i < 12; i++) {
                double angle = Math.PI * 2.0D * i / 12.0D;
                serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX() + Math.cos(angle) * 1.2D, player.getY() + 0.2D,
                        player.getZ() + Math.sin(angle) * 1.2D, 1, 0.0D, 0.05D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
