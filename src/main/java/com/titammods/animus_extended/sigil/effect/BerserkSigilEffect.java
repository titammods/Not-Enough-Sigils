package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record BerserkSigilEffect(int hitsRequired, int comboWindow, int rageDuration, int strengthAmplifier, int speedAmplifier, int comboSpeedDuration) implements ISigilEffect {
    public static final int DEFAULT_HITS_REQUIRED = 10;
    public static final int DEFAULT_COMBO_WINDOW = 300;
    public static final int DEFAULT_RAGE_DURATION = 400;
    public static final int DEFAULT_STRENGTH_AMPLIFIER = 2;
    public static final int DEFAULT_SPEED_AMPLIFIER = 1;
    public static final int DEFAULT_COMBO_SPEED_DURATION = 100;

    public static final BerserkSigilEffect DEFAULT = new BerserkSigilEffect(DEFAULT_HITS_REQUIRED, DEFAULT_COMBO_WINDOW, DEFAULT_RAGE_DURATION, DEFAULT_STRENGTH_AMPLIFIER, DEFAULT_SPEED_AMPLIFIER, DEFAULT_COMBO_SPEED_DURATION);

    public static final MapCodec<BerserkSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("hits_required", DEFAULT_HITS_REQUIRED).forGetter(BerserkSigilEffect::hitsRequired),
            Codec.INT.optionalFieldOf("combo_window", DEFAULT_COMBO_WINDOW).forGetter(BerserkSigilEffect::comboWindow),
            Codec.INT.optionalFieldOf("rage_duration", DEFAULT_RAGE_DURATION).forGetter(BerserkSigilEffect::rageDuration),
            Codec.INT.optionalFieldOf("strength_amplifier", DEFAULT_STRENGTH_AMPLIFIER).forGetter(BerserkSigilEffect::strengthAmplifier),
            Codec.INT.optionalFieldOf("speed_amplifier", DEFAULT_SPEED_AMPLIFIER).forGetter(BerserkSigilEffect::speedAmplifier),
            Codec.INT.optionalFieldOf("combo_speed_duration", DEFAULT_COMBO_SPEED_DURATION).forGetter(BerserkSigilEffect::comboSpeedDuration)
    ).apply(instance, BerserkSigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.BERSERK, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
