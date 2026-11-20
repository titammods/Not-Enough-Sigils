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

import java.util.UUID;

public record BrunimneetsSigilEffect(double heightReductionPixels, double blockRangeBonus, double entityRangeBonus) implements ISigilEffect {
    public static final double DEFAULT_HEIGHT_REDUCTION_PIXELS = 4.0D;
    public static final double DEFAULT_BLOCK_RANGE_BONUS = 10.0D;
    public static final double DEFAULT_ENTITY_RANGE_BONUS = 10.0D;

    public static final BrunimneetsSigilEffect DEFAULT = new BrunimneetsSigilEffect(DEFAULT_HEIGHT_REDUCTION_PIXELS, DEFAULT_BLOCK_RANGE_BONUS, DEFAULT_ENTITY_RANGE_BONUS);

    public static final MapCodec<BrunimneetsSigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("height_reduction_pixels", DEFAULT_HEIGHT_REDUCTION_PIXELS).forGetter(BrunimneetsSigilEffect::heightReductionPixels),
            Codec.DOUBLE.optionalFieldOf("block_range_bonus", DEFAULT_BLOCK_RANGE_BONUS).forGetter(BrunimneetsSigilEffect::blockRangeBonus),
            Codec.DOUBLE.optionalFieldOf("entity_range_bonus", DEFAULT_ENTITY_RANGE_BONUS).forGetter(BrunimneetsSigilEffect::entityRangeBonus)
    ).apply(instance, BrunimneetsSigilEffect::new));

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
        SigilTracker.mark(player, SigilTracker.Kind.BRUNIMNEETS, stack, this);
    }

    @Override
    public void onPlayerLogout(UUID playerId, MinecraftServer server) {
        SigilTracker.clear(playerId);
    }
}
