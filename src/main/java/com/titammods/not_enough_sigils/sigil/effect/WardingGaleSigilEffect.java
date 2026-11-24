package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.WardingGaleAura;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record WardingGaleSigilEffect(double radius, double pushStrength, double pushLift, double deflectFactor, int pulseInterval, boolean reflect) implements ISigilEffect {
    public static final double DEFAULT_RADIUS = 4.0D;
    public static final double DEFAULT_PUSH_STRENGTH = 0.45D;
    public static final double DEFAULT_PUSH_LIFT = 0.15D;
    public static final double DEFAULT_DEFLECT_FACTOR = -0.6D;
    public static final int DEFAULT_PULSE_INTERVAL = 2;

    public static final WardingGaleSigilEffect DEFAULT = new WardingGaleSigilEffect(DEFAULT_RADIUS, DEFAULT_PUSH_STRENGTH, DEFAULT_PUSH_LIFT, DEFAULT_DEFLECT_FACTOR, DEFAULT_PULSE_INTERVAL, false);

    public static final MapCodec<WardingGaleSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("radius", DEFAULT_RADIUS).forGetter(WardingGaleSigilEffect::radius),
            Codec.DOUBLE.optionalFieldOf("push_strength", DEFAULT_PUSH_STRENGTH).forGetter(WardingGaleSigilEffect::pushStrength),
            Codec.DOUBLE.optionalFieldOf("push_lift", DEFAULT_PUSH_LIFT).forGetter(WardingGaleSigilEffect::pushLift),
            Codec.DOUBLE.optionalFieldOf("deflect_factor", DEFAULT_DEFLECT_FACTOR).forGetter(WardingGaleSigilEffect::deflectFactor),
            Codec.INT.optionalFieldOf("pulse_interval", DEFAULT_PULSE_INTERVAL).forGetter(WardingGaleSigilEffect::pulseInterval),
            Codec.BOOL.optionalFieldOf("reflect", false).forGetter(WardingGaleSigilEffect::reflect)
    ).apply(instance, WardingGaleSigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.WARDING_GALE, stack, this);
        WardingGaleAura.pulse(level, player, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
