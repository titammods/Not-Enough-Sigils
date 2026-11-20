package com.titammods.not_enough_sigils.sigil.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record MissileProfile(float damageMultiplier, float velocityMultiplier, boolean homing, Optional<MobEffectSpec> effect) {
    public static final MissileProfile NEUTRAL = new MissileProfile(1.0F, 1.0F, false, Optional.empty());

    public static final Codec<MissileProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter(MissileProfile::damageMultiplier),
            Codec.FLOAT.optionalFieldOf("velocity_multiplier", 1.0F).forGetter(MissileProfile::velocityMultiplier),
            Codec.BOOL.optionalFieldOf("homing", false).forGetter(MissileProfile::homing),
            MobEffectSpec.CODEC.optionalFieldOf("effect").forGetter(MissileProfile::effect)
    ).apply(instance, MissileProfile::new));
}
