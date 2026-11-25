package com.titammods.not_enough_sigils.saereth;

import com.titammods.not_enough_sigils.setup.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;
import java.util.UUID;

public class SoulCaptureSigilItem extends Item {
    private static final String CHEST = "SaerethChest";
    private static final String ARRAY = "SaerethArray";
    private static final String DIMENSION = "SaerethDimension";
    private static final String CAPTURED = "SaerethCaptured";

    public SoulCaptureSigilItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant());
    }

    public static void storeCaptured(ItemStack stack, CompoundTag entityTag) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(CAPTURED, entityTag));
    }

    private static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static boolean hasCaptured(ItemStack stack) {
        return data(stack).contains(CAPTURED);
    }

    private static BlockPos getPos(CompoundTag tag, String key) {
        return tag.contains(key) ? BlockPos.of(tag.getLong(key)) : null;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasCaptured(stack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof SaerethEntity saereth)) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (!saereth.isOwner(player)) {
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.not_owner").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            if (hasCaptured(held)) {
                player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.already_holding").withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }
            if (player.level() instanceof ServerLevel serverLevel) {
                SaerethFx.capture(serverLevel, saereth, player);
            }
            saereth.captureInto(held);
            player.level().playSound(null, saereth.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6F, 1.4F);
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.captured").withStyle(ChatFormatting.LIGHT_PURPLE), true);
            return InteractionResult.SUCCESS;
        }
        CompoundTag tag = data(held);
        BlockPos chest = getPos(tag, CHEST);
        BlockPos array = getPos(tag, ARRAY);
        if (chest == null || array == null) {
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.missing_links").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.FAIL;
        }
        if (!player.level().dimension().location().toString().equals(tag.getString(DIMENSION))) {
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.wrong_dimension").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        if (saereth.isBusy()) {
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.busy").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.FAIL;
        }
        if (!saereth.canReach(chest) || !saereth.canReach(array)) {
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.too_far", SaerethSettings.get().linkRange()).withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        saereth.link(chest, array);
        if (player.level() instanceof ServerLevel serverLevel) {
            SaerethFx.link(serverLevel, saereth, chest, array);
        }
        player.level().playSound(null, saereth.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.8F, 1.2F);
        player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.linked").withStyle(ChatFormatting.GREEN), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (level.isClientSide() || player == null) {
            return InteractionResult.SUCCESS;
        }
        BlockPos clicked = context.getClickedPos();
        CompoundTag tag = data(stack);

        if (tag.contains(CAPTURED)) {
            return release(level, player, stack, tag, clicked.relative(context.getClickedFace()));
        }

        String dimension = level.dimension().location().toString();
        if (!dimension.equals(tag.getString(DIMENSION))) {
            tag.remove(CHEST);
            tag.remove(ARRAY);
            tag.putString(DIMENSION, dimension);
        }

        if (level.getCapability(Capabilities.ItemHandler.BLOCK, clicked, null) != null) {
            tag.putLong(CHEST, clicked.asLong());
            CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.chest_set", clicked.toShortString()).withStyle(ChatFormatting.AQUA), true);
            return InteractionResult.SUCCESS;
        }
        if (context.getClickedFace() != Direction.UP) {
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.top_face").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.FAIL;
        }
        BlockPos array = clicked.above();
        tag.putLong(ARRAY, array.asLong());
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.array_set", array.toShortString()).withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult release(Level level, Player player, ItemStack stack, CompoundTag tag, BlockPos pos) {
        SaerethEntity saereth = ModEntities.SAERETH.get().create(level);
        if (saereth == null) {
            return InteractionResult.FAIL;
        }
        saereth.load(tag.getCompound(CAPTURED));
        saereth.setUUID(UUID.randomUUID());
        saereth.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot() + 180.0F, 0.0F);
        saereth.setYHeadRot(saereth.getYRot());
        saereth.yBodyRot = saereth.getYRot();
        saereth.setDeltaMovement(0.0D, 0.0D, 0.0D);
        if (!level.noCollision(saereth)) {
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.no_space").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        level.addFreshEntity(saereth);
        tag.remove(CAPTURED);
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6F, 0.9F);
        if (level instanceof ServerLevel serverLevel) {
            SaerethFx.release(serverLevel, player, saereth);
        }
        player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.released").withStyle(ChatFormatting.LIGHT_PURPLE), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() || hasCaptured(stack)) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide()) {
            CompoundTag tag = data(stack);
            tag.remove(CHEST);
            tag.remove(ARRAY);
            tag.remove(DIMENSION);
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
            }
            player.displayClientMessage(Component.translatable("chat.not_enough_sigils.saereth.cleared").withStyle(ChatFormatting.GRAY), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide() || !selected || entity.tickCount % 5 != 0) {
            return;
        }
        CompoundTag tag = data(stack);
        if (!level.dimension().location().toString().equals(tag.getString(DIMENSION))) {
            return;
        }
        BlockPos chest = getPos(tag, CHEST);
        BlockPos array = getPos(tag, ARRAY);
        if (chest != null) {
            SaerethFx.marker(level, chest, SaerethFx.CHEST_MARK, 1.1D);
        }
        if (array != null) {
            SaerethFx.marker(level, array, SaerethFx.ARRAY_MARK, 0.1D);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = data(stack);
        if (tag.contains(CAPTURED)) {
            tooltip.add(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.captured").withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.release").withStyle(ChatFormatting.GRAY));
            return;
        }
        BlockPos chest = getPos(tag, CHEST);
        BlockPos array = getPos(tag, ARRAY);
        tooltip.add(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.chest",
                chest == null ? "-" : chest.toShortString()).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.array",
                array == null ? "-" : array.toShortString()).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.usage").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.capture").withStyle(ChatFormatting.GRAY));
    }
}
