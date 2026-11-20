package com.titammods.not_enough_sigils.sigil.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record MobEffectSpec(Holder<MobEffect> effect, int duration, int amplifier) {
    public static final Codec<MobEffectSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("id").forGetter(MobEffectSpec::effect),
            Codec.INT.optionalFieldOf("duration", 100).forGetter(MobEffectSpec::duration),
            Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MobEffectSpec::amplifier)
    ).apply(instance, MobEffectSpec::new));

    public void apply(LivingEntity target, Entity source) {
        target.addEffect(new MobEffectInstance(effect, duration, amplifier), source);
    }
}
