package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.SkywardSoulState;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record SkywardSoulSigilEffect(int flightEvPerSecond) implements ISigilEffect {
    public static final int DEFAULT_FLIGHT_EV_PER_SECOND = 60;

    public static final SkywardSoulSigilEffect DEFAULT = new SkywardSoulSigilEffect(DEFAULT_FLIGHT_EV_PER_SECOND);

    public static final MapCodec<SkywardSoulSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("flight_ev_per_second", DEFAULT_FLIGHT_EV_PER_SECOND).forGetter(SkywardSoulSigilEffect::flightEvPerSecond)
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
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        SigilTracker.mark(player, SigilTracker.Kind.SKYWARD_SOUL, stack, this);
        SkywardSoulState.grantFlight(player, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
