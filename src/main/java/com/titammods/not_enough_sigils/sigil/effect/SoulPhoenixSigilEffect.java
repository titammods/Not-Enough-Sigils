package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public record SoulPhoenixSigilEffect(boolean greater, Optional<Integer> cooldownOverride, Optional<Integer> evCostOverride,
                                     Optional<Float> reviveHealthOverride, Optional<Float> reviveHealthRatioOverride,
                                     Optional<Integer> invulnerabilityOverride, Optional<Integer> graceTicksOverride,
                                     Optional<Boolean> voidEscapeOverride) implements ISigilEffect {
    public static final int DEFAULT_EV_COST = 1000;
    public static final int DEFAULT_INVULNERABILITY = 20;
    public static final int LESSER_COOLDOWN = 300;
    public static final float LESSER_REVIVE_HEALTH = 4.0F;
    public static final int GREATER_COOLDOWN = 160;
    public static final float GREATER_REVIVE_RATIO = 0.5F;
    public static final int GREATER_GRACE = 20;

    public static final SoulPhoenixSigilEffect LESSER = of(false);
    public static final SoulPhoenixSigilEffect GREATER = of(true);

    public static final MapCodec<SoulPhoenixSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("greater", false).forGetter(SoulPhoenixSigilEffect::greater),
            Codec.INT.optionalFieldOf("cooldown").forGetter(SoulPhoenixSigilEffect::cooldownOverride),
            Codec.INT.optionalFieldOf("ev_cost").forGetter(SoulPhoenixSigilEffect::evCostOverride),
            Codec.FLOAT.optionalFieldOf("revive_health").forGetter(SoulPhoenixSigilEffect::reviveHealthOverride),
            Codec.FLOAT.optionalFieldOf("revive_health_ratio").forGetter(SoulPhoenixSigilEffect::reviveHealthRatioOverride),
            Codec.INT.optionalFieldOf("invulnerability").forGetter(SoulPhoenixSigilEffect::invulnerabilityOverride),
            Codec.INT.optionalFieldOf("grace_ticks").forGetter(SoulPhoenixSigilEffect::graceTicksOverride),
            Codec.BOOL.optionalFieldOf("void_escape").forGetter(SoulPhoenixSigilEffect::voidEscapeOverride)
    ).apply(instance, SoulPhoenixSigilEffect::new));

    public static SoulPhoenixSigilEffect of(boolean greater) {
        return new SoulPhoenixSigilEffect(greater, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());
    }

    public int cooldown() {
        return cooldownOverride.orElse(greater ? GREATER_COOLDOWN : LESSER_COOLDOWN);
    }

    public int evCost() {
        return evCostOverride.orElse(DEFAULT_EV_COST);
    }

    public float reviveHealth() {
        return reviveHealthOverride.orElse(greater ? 0.0F : LESSER_REVIVE_HEALTH);
    }

    public float reviveHealthRatio() {
        return reviveHealthRatioOverride.orElse(greater ? GREATER_REVIVE_RATIO : 0.0F);
    }

    public int invulnerability() {
        return invulnerabilityOverride.orElse(DEFAULT_INVULNERABILITY);
    }

    public int graceTicks() {
        return graceTicksOverride.orElse(greater ? GREATER_GRACE : 0);
    }

    public boolean voidEscape() {
        return voidEscapeOverride.orElse(greater);
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
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        SigilTracker.mark(player, greater ? SigilTracker.Kind.SOUL_PHOENIX_GREATER : SigilTracker.Kind.SOUL_PHOENIX, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
