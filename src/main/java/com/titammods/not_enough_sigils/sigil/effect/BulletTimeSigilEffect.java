package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.BulletTimeState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record BulletTimeSigilEffect(int duration, int cooldown, double radius, double projectileSpeedFactor, int slowAmplifier) implements ISigilEffect {
    public static final int DEFAULT_DURATION = 200;
    public static final int DEFAULT_COOLDOWN = 600;
    public static final double DEFAULT_RADIUS = 16.0D;
    public static final double DEFAULT_PROJECTILE_SPEED_FACTOR = 0.2D;
    public static final int DEFAULT_SLOW_AMPLIFIER = 4;

    public static final BulletTimeSigilEffect DEFAULT = new BulletTimeSigilEffect(DEFAULT_DURATION, DEFAULT_COOLDOWN, DEFAULT_RADIUS,
            DEFAULT_PROJECTILE_SPEED_FACTOR, DEFAULT_SLOW_AMPLIFIER);

    public static final MapCodec<BulletTimeSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(BulletTimeSigilEffect::duration),
            Codec.INT.optionalFieldOf("cooldown", DEFAULT_COOLDOWN).forGetter(BulletTimeSigilEffect::cooldown),
            Codec.DOUBLE.optionalFieldOf("radius", DEFAULT_RADIUS).forGetter(BulletTimeSigilEffect::radius),
            Codec.DOUBLE.optionalFieldOf("projectile_speed_factor", DEFAULT_PROJECTILE_SPEED_FACTOR).forGetter(BulletTimeSigilEffect::projectileSpeedFactor),
            Codec.INT.optionalFieldOf("slow_amplifier", DEFAULT_SLOW_AMPLIFIER).forGetter(BulletTimeSigilEffect::slowAmplifier)
    ).apply(instance, BulletTimeSigilEffect::new));

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack)) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        BulletTimeState.start(player, this);
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(stack, cooldown);
        }
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
