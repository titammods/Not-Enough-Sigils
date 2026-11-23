package com.titammods.not_enough_sigils.saereth;

import com.titammods.not_enough_sigils.setup.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.util.ProblemReporter;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.function.Consumer;
import java.util.UUID;

public class SaerethSigilItem extends Item {
    private static final String CHEST = "SaerethChest";
    private static final String ARRAY = "SaerethArray";
    private static final String DIMENSION = "SaerethDimension";
    private static final String CAPTURED = "SaerethCaptured";

    public SaerethSigilItem(Item.Properties properties) {
        super(properties);
    }

    public static void storeCaptured(ItemStack stack, CompoundTag entityTag) {
        CompoundTag tag = data(stack);
        tag.put(CAPTURED, entityTag);
        write(stack, tag);
    }

    private static void write(ItemStack stack, CompoundTag tag) {
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static boolean isLinkedTo(ItemStack stack, Level level) {
        return level.dimension().identifier().toString().equals(data(stack).getStringOr(DIMENSION, ""));
    }

    public static BlockPos chestOf(ItemStack stack) {
        return getPos(data(stack), CHEST);
    }

    public static BlockPos arrayOf(ItemStack stack) {
        return getPos(data(stack), ARRAY);
    }

    private static CompoundTag data(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static boolean hasCaptured(ItemStack stack) {
        return data(stack).contains(CAPTURED);
    }

    private static BlockPos getPos(CompoundTag tag, String key) {
        return tag.getLong(key).map(BlockPos::of).orElse(null);
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
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.not_owner").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            if (hasCaptured(held)) {
                player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.already_holding").withStyle(ChatFormatting.RED));
                return InteractionResult.FAIL;
            }
            saereth.captureInto(held);
            player.level().playSound(null, saereth.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6F, 1.4F);
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.captured").withStyle(ChatFormatting.LIGHT_PURPLE));
            return InteractionResult.SUCCESS;
        }
        CompoundTag tag = data(held);
        BlockPos chest = getPos(tag, CHEST);
        BlockPos array = getPos(tag, ARRAY);
        if (chest == null || array == null) {
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.missing_links").withStyle(ChatFormatting.YELLOW));
            return InteractionResult.FAIL;
        }
        if (!player.level().dimension().identifier().toString().equals(tag.getStringOr(DIMENSION, ""))) {
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.wrong_dimension").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        if (saereth.isBusy()) {
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.busy").withStyle(ChatFormatting.YELLOW));
            return InteractionResult.FAIL;
        }
        if (!saereth.canReach(chest) || !saereth.canReach(array)) {
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.too_far", SaerethSettings.get().linkRange()).withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        saereth.link(chest, array);
        player.level().playSound(null, saereth.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.8F, 1.2F);
        player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.linked").withStyle(ChatFormatting.GREEN));
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

        String dimension = level.dimension().identifier().toString();
        if (!dimension.equals(tag.getStringOr(DIMENSION, ""))) {
            tag.remove(CHEST);
            tag.remove(ARRAY);
            tag.putString(DIMENSION, dimension);
        }

        if (level.getCapability(Capabilities.Item.BLOCK, clicked, null) != null) {
            tag.putLong(CHEST, clicked.asLong());
            write(stack, tag);
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.chest_set", clicked.toShortString()).withStyle(ChatFormatting.AQUA));
            return InteractionResult.SUCCESS;
        }
        if (context.getClickedFace() != Direction.UP) {
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.top_face").withStyle(ChatFormatting.YELLOW));
            return InteractionResult.FAIL;
        }
        BlockPos array = clicked.above();
        tag.putLong(ARRAY, array.asLong());
        write(stack, tag);
        player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.array_set", array.toShortString()).withStyle(ChatFormatting.LIGHT_PURPLE));
        return InteractionResult.SUCCESS;
    }

    private InteractionResult release(Level level, Player player, ItemStack stack, CompoundTag tag, BlockPos pos) {
        SaerethEntity saereth = ModEntities.SAERETH.get().create(level, EntitySpawnReason.TRIGGERED);
        if (saereth == null) {
            return InteractionResult.FAIL;
        }
        saereth.load(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag.getCompoundOrEmpty(CAPTURED)));
        saereth.setUUID(UUID.randomUUID());
        saereth.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot() + 180.0F, 0.0F);
        saereth.setYHeadRot(saereth.getYRot());
        saereth.yBodyRot = saereth.getYRot();
        saereth.setDeltaMovement(0.0D, 0.0D, 0.0D);
        if (!level.noCollision(saereth)) {
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.no_space").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        level.addFreshEntity(saereth);
        tag.remove(CAPTURED);
        write(stack, tag);
        level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6F, 0.9F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL, saereth.getX(), saereth.getY() + 1.0D, saereth.getZ(), 40, 0.3D, 0.8D, 0.3D, 0.2D);
        }
        player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.released").withStyle(ChatFormatting.LIGHT_PURPLE));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() || hasCaptured(stack)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            CompoundTag tag = data(stack);
            tag.remove(CHEST);
            tag.remove(ARRAY);
            tag.remove(DIMENSION);
            write(stack, tag);
            player.sendOverlayMessage(Component.translatable("chat.not_enough_sigils.saereth.cleared").withStyle(ChatFormatting.GRAY));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = data(stack);
        if (tag.contains(CAPTURED)) {
            tooltip.accept(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.captured").withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.accept(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.release").withStyle(ChatFormatting.GRAY));
            return;
        }
        BlockPos chest = getPos(tag, CHEST);
        BlockPos array = getPos(tag, ARRAY);
        tooltip.accept(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.chest",
                chest == null ? "-" : chest.toShortString()).withStyle(ChatFormatting.AQUA));
        tooltip.accept(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.array",
                array == null ? "-" : array.toShortString()).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.accept(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.usage").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.not_enough_sigils.saereth_sigil.capture").withStyle(ChatFormatting.GRAY));
    }
}
