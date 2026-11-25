package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record QuicksilverSigilEffect(Holder<MobEffect> effect, int amplifier, int duration, int refreshBelow) implements ISigilEffect {
    public static final int DEFAULT_AMPLIFIER = 1;
    public static final int DEFAULT_DURATION = 100;
    public static final int DEFAULT_REFRESH_BELOW = 40;

    public static final QuicksilverSigilEffect DEFAULT = new QuicksilverSigilEffect(MobEffects.MOVEMENT_SPEED, DEFAULT_AMPLIFIER, DEFAULT_DURATION, DEFAULT_REFRESH_BELOW);

    public static final MapCodec<QuicksilverSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().optionalFieldOf("effect", MobEffects.MOVEMENT_SPEED).forGetter(QuicksilverSigilEffect::effect),
            Codec.INT.optionalFieldOf("amplifier", DEFAULT_AMPLIFIER).forGetter(QuicksilverSigilEffect::amplifier),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(QuicksilverSigilEffect::duration),
            Codec.INT.optionalFieldOf("refresh_below", DEFAULT_REFRESH_BELOW).forGetter(QuicksilverSigilEffect::refreshBelow)
    ).apply(instance, QuicksilverSigilEffect::new));

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
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && (current.getAmplifier() > amplifier || current.getDuration() > refreshBelow)) {
            return;
        }
        player.addEffect(new MobEffectInstance(effect, Math.max(duration, refreshBelow + 1), amplifier, true, false, false));
    }
}
