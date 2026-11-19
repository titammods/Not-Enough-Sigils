package com.titammods.animus_extended.setup;

import com.titammods.animus_extended.AnimusExtended;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> ARCANE = ResourceKey.create(Registries.DAMAGE_TYPE, AnimusExtended.rl("arcane"));

    public static DamageSource arcane(Level level, @Nullable Entity directEntity, @Nullable Entity attacker) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ARCANE),
                directEntity,
                attacker
        );
    }
}
