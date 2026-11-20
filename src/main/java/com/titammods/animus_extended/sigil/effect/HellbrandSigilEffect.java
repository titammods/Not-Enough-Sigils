package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.animus_extended.sigil.SigilTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record HellbrandSigilEffect(int maxStacks, int stackDuration, float baseBurst, float burstHealthRatio, float maxBurst) implements ISigilEffect {
    public static final int DEFAULT_MAX_STACKS = 5;
    public static final int DEFAULT_STACK_DURATION = 200;
    public static final float DEFAULT_BASE_BURST = 5.0F;
    public static final float DEFAULT_BURST_HEALTH_RATIO = 0.15F;
    public static final float DEFAULT_MAX_BURST = 50.0F;

    public static final HellbrandSigilEffect DEFAULT = new HellbrandSigilEffect(DEFAULT_MAX_STACKS, DEFAULT_STACK_DURATION, DEFAULT_BASE_BURST, DEFAULT_BURST_HEALTH_RATIO, DEFAULT_MAX_BURST);

    public static final MapCodec<HellbrandSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("max_stacks", DEFAULT_MAX_STACKS).forGetter(HellbrandSigilEffect::maxStacks),
            Codec.INT.optionalFieldOf("stack_duration", DEFAULT_STACK_DURATION).forGetter(HellbrandSigilEffect::stackDuration),
            Codec.FLOAT.optionalFieldOf("base_burst", DEFAULT_BASE_BURST).forGetter(HellbrandSigilEffect::baseBurst),
            Codec.FLOAT.optionalFieldOf("burst_health_ratio", DEFAULT_BURST_HEALTH_RATIO).forGetter(HellbrandSigilEffect::burstHealthRatio),
            Codec.FLOAT.optionalFieldOf("max_burst", DEFAULT_MAX_BURST).forGetter(HellbrandSigilEffect::maxBurst)
    ).apply(instance, HellbrandSigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.HELLBRAND, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
