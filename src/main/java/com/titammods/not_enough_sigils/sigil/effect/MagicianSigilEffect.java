package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.spiritus.IPlayerSpiritusHandler;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.entity.MagicMissileEntity;
import com.titammods.not_enough_sigils.sigil.SpiritusAffinity;
import com.titammods.not_enough_sigils.sigil.config.MissileProfile;
import com.titammods.not_enough_sigils.sigil.config.MobEffectSpec;
import com.titammods.not_enough_sigils.sigil.config.SpiritusProfiles;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;

public record MagicianSigilEffect(int missiles, boolean homing, int cooldown, float baseDamage, float bonusDamage,
                                  double maxSpiritus, float velocity, float spreadDegrees,
                                  Map<SpiritusType, MissileProfile> profiles) implements ISigilEffect {
    public static final int DEFAULT_MISSILES = 1;
    public static final int DEFAULT_COOLDOWN = 20;
    public static final float DEFAULT_BASE_DAMAGE = 20.0F;
    public static final float DEFAULT_BONUS_DAMAGE = 20.0F;
    public static final double DEFAULT_MAX_SPIRITUS = 4096.0D;
    public static final float DEFAULT_VELOCITY = 2.5F;
    public static final float DEFAULT_SPREAD = 10.0F;
    public static final Map<SpiritusType, MissileProfile> DEFAULT_PROFILES = Map.of(
            SpiritusType.RAW, MissileProfile.NEUTRAL,
            SpiritusType.RUINA, new MissileProfile(0.75F, 1.0F, true, Optional.empty()),
            SpiritusType.VINDICTA, new MissileProfile(1.4F, 0.6F, false, Optional.empty()),
            SpiritusType.INVICTUS, new MissileProfile(0.75F, 1.6F, false, Optional.empty()),
            SpiritusType.NIHILUM, new MissileProfile(0.7F, 1.0F, false, Optional.of(new MobEffectSpec(MobEffects.SLOWNESS, 80, 2)))
    );

    public static final MagicianSigilEffect DEFAULT = basic(DEFAULT_MISSILES, false, DEFAULT_COOLDOWN);

    public static final MapCodec<MagicianSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("missiles", DEFAULT_MISSILES).forGetter(MagicianSigilEffect::missiles),
            Codec.BOOL.optionalFieldOf("homing", false).forGetter(MagicianSigilEffect::homing),
            Codec.INT.optionalFieldOf("cooldown", DEFAULT_COOLDOWN).forGetter(MagicianSigilEffect::cooldown),
            Codec.FLOAT.optionalFieldOf("base_damage", DEFAULT_BASE_DAMAGE).forGetter(MagicianSigilEffect::baseDamage),
            Codec.FLOAT.optionalFieldOf("bonus_damage", DEFAULT_BONUS_DAMAGE).forGetter(MagicianSigilEffect::bonusDamage),
            Codec.DOUBLE.optionalFieldOf("max_spiritus", DEFAULT_MAX_SPIRITUS).forGetter(MagicianSigilEffect::maxSpiritus),
            Codec.FLOAT.optionalFieldOf("velocity", DEFAULT_VELOCITY).forGetter(MagicianSigilEffect::velocity),
            Codec.FLOAT.optionalFieldOf("spread_degrees", DEFAULT_SPREAD).forGetter(MagicianSigilEffect::spreadDegrees),
            SpiritusProfiles.codec(MissileProfile.CODEC).optionalFieldOf("profiles", Map.of()).forGetter(MagicianSigilEffect::profiles)
    ).apply(instance, MagicianSigilEffect::new));

    public static MagicianSigilEffect basic(int missiles, boolean homing, int cooldown) {
        return new MagicianSigilEffect(missiles, homing, cooldown, DEFAULT_BASE_DAMAGE, DEFAULT_BONUS_DAMAGE, DEFAULT_MAX_SPIRITUS,
                DEFAULT_VELOCITY, DEFAULT_SPREAD, Map.of());
    }

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack)) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }

        MissileProfile profile = SpiritusProfiles.resolve(profiles, DEFAULT_PROFILES, SpiritusAffinity.of(player), MissileProfile.NEUTRAL);
        float damage = computeDamage(player) * profile.damageMultiplier();
        float speed = velocity * profile.velocityMultiplier();
        boolean seeking = homing || profile.homing();
        int count = Math.max(1, missiles);
        for (int i = 0; i < count; i++) {
            float yawOffset = (i - (count - 1) / 2.0F) * spreadDegrees;
            MagicMissileEntity missile = new MagicMissileEntity(level, player, damage, seeking, profile.effect());
            missile.shootFromRotation(player, player.getXRot(), player.getYRot() + yawOffset, 0.0F, speed, 0.0F);
            level.addFreshEntity(missile);
        }
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(stack, cooldown);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.8F, 1.4F);
        return true;
    }

    public float computeDamage(Player player) {
        IPlayerSpiritusHandler handler = NeoVitaeAPI.getInstance().getPlayerSpiritusHandler();
        double total = 0.0D;
        for (SpiritusType type : SpiritusType.values()) {
            total += handler.getTotalSpiritus(type, player);
        }
        double ratio = maxSpiritus <= 0.0D ? 1.0D : Math.min(total, maxSpiritus) / maxSpiritus;
        return baseDamage + (float) (ratio * bonusDamage);
    }
}
