package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.sigil.SigilTracker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record CurseOfGluttonySigilEffect(int bloodCost, int hungerThreshold, float saturation) implements ISigilEffect {
    public static final int MAX_FOOD = 20;
    public static final int DEFAULT_BLOOD_COST = 150;
    public static final int DEFAULT_HUNGER_THRESHOLD = 2;
    public static final float DEFAULT_SATURATION = 20.0F;

    public static final CurseOfGluttonySigilEffect DEFAULT = new CurseOfGluttonySigilEffect(DEFAULT_BLOOD_COST, DEFAULT_HUNGER_THRESHOLD, DEFAULT_SATURATION);

    public static final MapCodec<CurseOfGluttonySigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("blood_cost", DEFAULT_BLOOD_COST).forGetter(CurseOfGluttonySigilEffect::bloodCost),
            Codec.INT.optionalFieldOf("hunger_threshold", DEFAULT_HUNGER_THRESHOLD).forGetter(CurseOfGluttonySigilEffect::hungerThreshold),
            Codec.FLOAT.optionalFieldOf("saturation", DEFAULT_SATURATION).forGetter(CurseOfGluttonySigilEffect::saturation)
    ).apply(instance, CurseOfGluttonySigilEffect::new));

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
        if (level.isClientSide() || player.getAbilities().invulnerable) {
            return;
        }
        FoodData food = player.getFoodData();
        if (food.getSaturationLevel() > 0.0F || MAX_FOOD - food.getFoodLevel() < Math.max(1, hungerThreshold)) {
            return;
        }
        if (!SigilTracker.drainEV(player, stack, bloodCost)) {
            return;
        }
        food.setFoodLevel(MAX_FOOD);
        food.setSaturation(Math.min(saturation, MAX_FOOD));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, 0.8F);
    }
}
