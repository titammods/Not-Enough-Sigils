package com.titammods.not_enough_sigils.sigil.effect;

import com.breakinblocks.animusnv.Constants;
import com.breakinblocks.animusnv.items.sigils.effects.TemporalDominanceSigilEffect;
import com.breakinblocks.animusnv.util.AnimusRitualHelper;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.not_enough_sigils.mixin.TemporalDominanceAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public record TemporalMasterySigilEffect(int startLevel, int maxLevel, int startCost, int refreshCost, int duration) implements ISigilEffect {
    public static final int DEFAULT_START_LEVEL = 1;
    public static final int DEFAULT_MAX_LEVEL = 9;
    public static final int DEFAULT_START_COST = 250;
    public static final int DEFAULT_REFRESH_COST = 20000;
    public static final int DEFAULT_DURATION = 600;
    public static final int LEVEL_CAP = 20;

    public static final TemporalMasterySigilEffect DEFAULT = new TemporalMasterySigilEffect(DEFAULT_START_LEVEL, DEFAULT_MAX_LEVEL,
            DEFAULT_START_COST, DEFAULT_REFRESH_COST, DEFAULT_DURATION);

    public static final MapCodec<TemporalMasterySigilEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("start_level", DEFAULT_START_LEVEL).forGetter(TemporalMasterySigilEffect::startLevel),
            Codec.INT.optionalFieldOf("max_level", DEFAULT_MAX_LEVEL).forGetter(TemporalMasterySigilEffect::maxLevel),
            Codec.INT.optionalFieldOf("start_cost", DEFAULT_START_COST).forGetter(TemporalMasterySigilEffect::startCost),
            Codec.INT.optionalFieldOf("refresh_cost", DEFAULT_REFRESH_COST).forGetter(TemporalMasterySigilEffect::refreshCost),
            Codec.INT.optionalFieldOf("duration", DEFAULT_DURATION).forGetter(TemporalMasterySigilEffect::duration)
    ).apply(instance, TemporalMasterySigilEffect::new));

    private static final List<String> FOREIGN_ACCELERATORS = List.of(
            "ky.someone.mods.gag.entity.TimeAcceleratorEntity",
            "org.mangorage.tiab.common.entities.TimeAcceleratorEntity",
            "com.haoict.tiab.common.entities.TimeAcceleratorEntity",
            "com.direwolf20.justdirethings.common.entities.TimeWandEntity"
    );

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnBlock(Level level, Player player, ItemStack stack, BlockPos pos, Direction side, Vec3 hitVec) {
        if (level.isClientSide() || player == null || !player.isShiftKeyDown()) {
            return false;
        }

        if (level.getBlockEntity(pos) == null) {
            player.sendOverlayMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_NO_TILE).withStyle(ChatFormatting.RED));
            return false;
        }
        if (level.getBlockState(pos).is(Constants.Tags.DISALLOW_ACCELERATION)) {
            player.sendOverlayMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_DISALLOWED).withStyle(ChatFormatting.RED));
            return false;
        }
        if (hasForeignAccelerator(level, pos)) {
            player.sendOverlayMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_GAG_ACTIVE).withStyle(ChatFormatting.RED));
            return false;
        }

        IAnima network = AnimusRitualHelper.getNetworkForBoundItem(player, stack);
        if (network == null) {
            return false;
        }

        Map<GlobalPos, TemporalDominanceSigilEffect.AccelerationState> accelerated = TemporalDominanceAccessor.not_enough_sigils$getAcceleratedBlocks();
        GlobalPos key = GlobalPos.of(level.dimension(), pos.immutable());
        TemporalDominanceSigilEffect.AccelerationState current = accelerated.get(key);

        int max = Math.max(1, Math.min(LEVEL_CAP, maxLevel));
        int start = Math.max(1, Math.min(max, startLevel));
        int newLevel;
        int evCost;
        if (current == null) {
            newLevel = start;
            evCost = startCost;
        } else if (current.level >= max) {
            newLevel = max;
            evCost = refreshCost;
        } else {
            newLevel = Math.max(start, current.level + 1);
            evCost = (int) Math.min(Integer.MAX_VALUE, (long) startCost << (newLevel - start));
        }

        if (network.getCurrentEV() < evCost) {
            player.sendOverlayMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_NO_EV, evCost).withStyle(ChatFormatting.RED));
            return false;
        }
        network.syphon(AnimaTicket.create(evCost));

        accelerated.put(key, new TemporalDominanceSigilEffect.AccelerationState(newLevel, level.getGameTime() + duration, level.dimension()));
        player.sendOverlayMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_ACTIVATED, 1 << newLevel, duration / 20)
                .withStyle(ChatFormatting.GOLD));
        return false;
    }

    private static boolean hasForeignAccelerator(Level level, BlockPos pos) {
        List<Entity> entities = level.getEntities((Entity) null, new AABB(pos), entity -> entity != null
                && FOREIGN_ACCELERATORS.contains(entity.getClass().getName()));
        return !entities.isEmpty();
    }
}
