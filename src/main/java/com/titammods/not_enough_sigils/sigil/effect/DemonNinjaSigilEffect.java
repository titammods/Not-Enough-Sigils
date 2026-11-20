package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.entity.NinjaKnifeEntity;
import com.titammods.not_enough_sigils.sigil.DemonNinjaState;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import com.titammods.not_enough_sigils.sigil.SpiritusAffinity;
import com.titammods.not_enough_sigils.sigil.config.KnifeProfile;
import com.titammods.not_enough_sigils.sigil.config.MobEffectSpec;
import com.titammods.not_enough_sigils.sigil.config.SpiritusProfiles;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record DemonNinjaSigilEffect(int revealTicks, int knifeCooldown, float knifeVelocity, float knifeInaccuracy,
                                    Map<SpiritusType, KnifeProfile> knives) implements ISigilEffect {
    public static final int DEFAULT_REVEAL_TICKS = 600;
    public static final int DEFAULT_KNIFE_COOLDOWN = 10;
    public static final float DEFAULT_KNIFE_VELOCITY = 2.2F;
    public static final float DEFAULT_KNIFE_INACCURACY = 0.5F;
    public static final float DEFAULT_RAW_DAMAGE = 8.0F;
    public static final Map<SpiritusType, KnifeProfile> DEFAULT_KNIVES = Map.of(
            SpiritusType.RAW, KnifeProfile.damage(DEFAULT_RAW_DAMAGE),
            SpiritusType.NIHILUM, new KnifeProfile(5.0F, 1.5F, 0.0F, Optional.empty(), 0, 0),
            SpiritusType.RUINA, new KnifeProfile(5.0F, 0.0F, 5.0F, Optional.empty(), 0, 0),
            SpiritusType.INVICTUS, new KnifeProfile(5.0F, 0.0F, 0.0F, Optional.of(new MobEffectSpec(MobEffects.MOVEMENT_SLOWDOWN, 100, 2)), 100, 0),
            SpiritusType.VINDICTA, new KnifeProfile(5.0F, 0.0F, 0.0F, Optional.empty(), 0, 5)
    );

    public static final DemonNinjaSigilEffect DEFAULT = new DemonNinjaSigilEffect(DEFAULT_REVEAL_TICKS, DEFAULT_KNIFE_COOLDOWN,
            DEFAULT_KNIFE_VELOCITY, DEFAULT_KNIFE_INACCURACY, Map.of());

    public static final MapCodec<DemonNinjaSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("reveal_ticks", DEFAULT_REVEAL_TICKS).forGetter(DemonNinjaSigilEffect::revealTicks),
            Codec.INT.optionalFieldOf("knife_cooldown", DEFAULT_KNIFE_COOLDOWN).forGetter(DemonNinjaSigilEffect::knifeCooldown),
            Codec.FLOAT.optionalFieldOf("knife_velocity", DEFAULT_KNIFE_VELOCITY).forGetter(DemonNinjaSigilEffect::knifeVelocity),
            Codec.FLOAT.optionalFieldOf("knife_inaccuracy", DEFAULT_KNIFE_INACCURACY).forGetter(DemonNinjaSigilEffect::knifeInaccuracy),
            SpiritusProfiles.codec(KnifeProfile.CODEC).optionalFieldOf("knives", Map.of()).forGetter(DemonNinjaSigilEffect::knives)
    ).apply(instance, DemonNinjaSigilEffect::new));

    public KnifeProfile knife(SpiritusType type) {
        return SpiritusProfiles.resolve(knives, DEFAULT_KNIVES, type, KnifeProfile.damage(DEFAULT_RAW_DAMAGE));
    }

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        SpiritusType type = SpiritusAffinity.of(player);
        NinjaKnifeEntity knife = new NinjaKnifeEntity(level, player, type, knife(type));
        knife.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, knifeVelocity, knifeInaccuracy);
        level.addFreshEntity(knife);
        if (knifeCooldown > 0) {
            player.getCooldowns().addCooldown(stack.getItem(), knifeCooldown);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.6F, 1.8F);
        return true;
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        SigilTracker.mark(player, SigilTracker.Kind.DEMON_NINJA, stack, this);
        DemonNinjaState.applyEffects(player);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
