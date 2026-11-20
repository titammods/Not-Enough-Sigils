package com.titammods.animus_extended.sigil.effect;

import com.breakinblocks.animusnv.Constants;
import com.breakinblocks.animusnv.items.sigils.effects.TemporalDominanceSigilEffect;
import com.breakinblocks.animusnv.util.AnimusRitualHelper;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.mojang.serialization.MapCodec;
import com.titammods.animus_extended.mixin.TemporalDominanceAccessor;
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

public record TemporalMasterySigilEffect() implements ISigilEffect {
    public static final MapCodec<TemporalMasterySigilEffect> CODEC = MapCodec.unit(TemporalMasterySigilEffect::new);

    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 9;
    private static final int START_EV_COST = 250;
    private static final int REFRESH_COST = 20000;
    private static final int DURATION_TICKS = 600;

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
            player.displayClientMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_NO_TILE).withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (level.getBlockState(pos).is(Constants.Tags.DISALLOW_ACCELERATION)) {
            player.displayClientMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_DISALLOWED).withStyle(ChatFormatting.RED), true);
            return false;
        }
        if (hasForeignAccelerator(level, pos)) {
            player.displayClientMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_GAG_ACTIVE).withStyle(ChatFormatting.RED), true);
            return false;
        }

        IAnima network = AnimusRitualHelper.getNetworkForBoundItem(player, stack);
        if (network == null) {
            return false;
        }

        Map<GlobalPos, TemporalDominanceSigilEffect.AccelerationState> accelerated = TemporalDominanceAccessor.animus_extended$getAcceleratedBlocks();
        GlobalPos key = GlobalPos.of(level.dimension(), pos.immutable());
        TemporalDominanceSigilEffect.AccelerationState current = accelerated.get(key);

        int newLevel;
        int evCost;
        if (current == null) {
            newLevel = START_LEVEL;
            evCost = START_EV_COST;
        } else if (current.level >= MAX_LEVEL) {
            newLevel = MAX_LEVEL;
            evCost = REFRESH_COST;
        } else {
            newLevel = current.level + 1;
            evCost = START_EV_COST << (newLevel - START_LEVEL);
        }

        if (network.getCurrentEV() < evCost) {
            player.displayClientMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_NO_EV, evCost).withStyle(ChatFormatting.RED), true);
            return false;
        }
        network.syphon(AnimaTicket.create(evCost));

        accelerated.put(key, new TemporalDominanceSigilEffect.AccelerationState(newLevel, level.getGameTime() + DURATION_TICKS, level.dimension()));
        player.displayClientMessage(Component.translatable(Constants.Localizations.Text.TEMPORAL_ACTIVATED, 1 << newLevel, DURATION_TICKS / 20)
                .withStyle(ChatFormatting.GOLD), true);
        return false;
    }

    private static boolean hasForeignAccelerator(Level level, BlockPos pos) {
        List<Entity> entities = level.getEntities((Entity) null, new AABB(pos), entity -> entity != null
                && FOREIGN_ACCELERATORS.contains(entity.getClass().getName()));
        return !entities.isEmpty();
    }
}
