package com.titammods.not_enough_sigils.sigil.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record KnifeProfile(float damage, float explosionPower, float lightningDamage, Optional<MobEffectSpec> effect,
                           int freezeTicks, int fireSeconds) {
    public static final Codec<KnifeProfile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("damage", 5.0F).forGetter(KnifeProfile::damage),
            Codec.FLOAT.optionalFieldOf("explosion_power", 0.0F).forGetter(KnifeProfile::explosionPower),
            Codec.FLOAT.optionalFieldOf("lightning_damage", 0.0F).forGetter(KnifeProfile::lightningDamage),
            MobEffectSpec.CODEC.optionalFieldOf("effect").forGetter(KnifeProfile::effect),
            Codec.INT.optionalFieldOf("freeze_ticks", 0).forGetter(KnifeProfile::freezeTicks),
            Codec.INT.optionalFieldOf("fire_seconds", 0).forGetter(KnifeProfile::fireSeconds)
    ).apply(instance, KnifeProfile::new));

    public static KnifeProfile damage(float damage) {
        return new KnifeProfile(damage, 0.0F, 0.0F, Optional.empty(), 0, 0);
    }
}
