package com.titammods.animus_extended.sigil.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record PunchProfile(float damageMultiplier, Optional<MobEffectSpec> effect, int freezeTicks, int fireSeconds,
                           float arcaneDamage, float arcaneHealthRatio, float arcaneMaxDamage) {
    public static final PunchProfile NEUTRAL = new PunchProfile(1.0F, Optional.empty(), 0, 0, 0.0F, 0.0F, 0.0F);

    public static final Codec<PunchProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("damage_multiplier", 1.0F).forGetter(PunchProfile::damageMultiplier),
            MobEffectSpec.CODEC.optionalFieldOf("effect").forGetter(PunchProfile::effect),
            Codec.INT.optionalFieldOf("freeze_ticks", 0).forGetter(PunchProfile::freezeTicks),
            Codec.INT.optionalFieldOf("fire_seconds", 0).forGetter(PunchProfile::fireSeconds),
            Codec.FLOAT.optionalFieldOf("arcane_damage", 0.0F).forGetter(PunchProfile::arcaneDamage),
            Codec.FLOAT.optionalFieldOf("arcane_health_ratio", 0.0F).forGetter(PunchProfile::arcaneHealthRatio),
            Codec.FLOAT.optionalFieldOf("arcane_max_damage", 0.0F).forGetter(PunchProfile::arcaneMaxDamage)
    ).apply(instance, PunchProfile::new));
}
