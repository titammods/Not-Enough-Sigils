package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.SkywardSoulState;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record SkywardSoulSigilEffect(int flightEvPerSecond, double boostStrength, int boostCost, int boostCooldown) implements ISigilEffect {
    public static final int DEFAULT_FLIGHT_EV_PER_SECOND = 60;

    public static final SkywardSoulSigilEffect DEFAULT = new SkywardSoulSigilEffect(DEFAULT_FLIGHT_EV_PER_SECOND, 0.0D, 50, 20);

    public static final MapCodec<SkywardSoulSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("flight_ev_per_second", DEFAULT_FLIGHT_EV_PER_SECOND).forGetter(SkywardSoulSigilEffect::flightEvPerSecond),
            Codec.DOUBLE.optionalFieldOf("boost_strength", 0.0D).forGetter(SkywardSoulSigilEffect::boostStrength),
            Codec.INT.optionalFieldOf("boost_cost", 50).forGetter(SkywardSoulSigilEffect::boostCost),
            Codec.INT.optionalFieldOf("boost_cooldown", 20).forGetter(SkywardSoulSigilEffect::boostCooldown)
    ).apply(instance, SkywardSoulSigilEffect::new));

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (boostStrength <= 0.0D || player == null || !player.getAbilities().flying || player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        if (!SigilTracker.isActive(player, SigilTracker.Kind.SKYWARD_SOUL) || !SigilTracker.drainEV(player, stack, boostCost)) {
            return false;
        }
        player.setDeltaMovement(player.getDeltaMovement().add(player.getLookAngle().scale(boostStrength)));
        player.hurtMarked = true;
        if (boostCooldown > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), boostCooldown);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 0.5D, player.getZ(), 12, 0.2D, 0.2D, 0.2D, 0.05D);
        }
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        SigilTracker.mark(player, SigilTracker.Kind.SKYWARD_SOUL, stack, this);
        SkywardSoulState.grantFlight(player, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
