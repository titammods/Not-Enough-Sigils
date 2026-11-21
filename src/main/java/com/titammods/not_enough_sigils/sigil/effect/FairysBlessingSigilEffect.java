package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record FairysBlessingSigilEffect(int amplifier, int duration, int refreshBelow) implements ISigilEffect {
    public static final int DEFAULT_AMPLIFIER = 2;
    public static final int DEFAULT_DURATION = 100;
    public static final int DEFAULT_REFRESH_BELOW = 40;

    public static final FairysBlessingSigilEffect DEFAULT = new FairysBlessingSigilEffect(DEFAULT_AMPLIFIER, DEFAULT_DURATION, DEFAULT_REFRESH_BELOW);

    public static final MapCodec<FairysBlessingSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("amplifier", DEFAULT_AMPLIFIER).forGetter(FairysBlessingSigilEffect::amplifier),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(FairysBlessingSigilEffect::duration),
            Codec.INT.optionalFieldOf("refresh_below", DEFAULT_REFRESH_BELOW).forGetter(FairysBlessingSigilEffect::refreshBelow)
    ).apply(instance, FairysBlessingSigilEffect::new));

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
        if (level.isClientSide()) {
            return;
        }
        MobEffectInstance current = player.getEffect(MobEffects.REGENERATION);
        if (current != null && (current.getAmplifier() > amplifier || current.getDuration() > refreshBelow)) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Math.max(duration, refreshBelow + 1), amplifier, true, false, false));
    }
}
