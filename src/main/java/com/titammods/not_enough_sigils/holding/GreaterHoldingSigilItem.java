package com.titammods.not_enough_sigils.holding;

import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class GreaterHoldingSigilItem extends ItemSigilHolding {
    public static final int ROW_SIZE = 9;

    private final int size;
    private final String descriptionKey;

    public GreaterHoldingSigilItem(Item.Properties properties, int size, String descriptionKey) {
        super(properties);
        this.size = size;
        this.descriptionKey = descriptionKey;
    }

    public int getSize() {
        return size;
    }

    public static int sizeOf(ItemStack stack) {
        return stack.getItem() instanceof GreaterHoldingSigilItem holding ? holding.size : ItemSigilHolding.INVENTORY_SIZE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(descriptionKey)
                .withStyle(ChatFormatting.ITALIC)
                .withStyle(ChatFormatting.GRAY));

        Binding binding = getBinding(stack);
        if (binding != null) {
            tooltip.accept(Component.translatable("tooltip.neovitae.currentOwner", binding.name())
                    .withStyle(ChatFormatting.GRAY));
        }

        NonNullList<ItemStack> inventory = readInventory(stack);
        int selected = readSelected(stack);
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack inner = inventory.get(i);
            if (inner.isEmpty()) {
                continue;
            }
            Component name = i == selected
                    ? inner.getHoverName().copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.UNDERLINE)
                    : inner.getHoverName();
            tooltip.accept(Component.translatable("tooltip.neovitae.sigil.holding.sigilInSlot", i + 1, name));
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (PlayerHelper.isFakePlayer(player)) {
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().getSelectedSlot() : Inventory.SLOT_OFFHAND;
                openMenu(serverPlayer, stack, slot);
            }
            return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
        }
        return super.use(level, player, hand);
    }

    private void openMenu(ServerPlayer player, ItemStack stack, int slot) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return stack.getHoverName();
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player menuPlayer) {
                return new GreaterHoldingMenu(containerId, playerInventory, stack, slot);
            }
        }, buf -> buf.writeInt(slot));
    }

    public static NonNullList<ItemStack> readInventory(ItemStack stack) {
        int size = sizeOf(stack);
        NonNullList<ItemStack> inventory = NonNullList.withSize(size, ItemStack.EMPTY);
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents != null) {
            for (int i = 0; i < Math.min(contents.getSlots(), size); i++) {
                inventory.set(i, contents.getStackInSlot(i));
            }
        }
        return inventory;
    }

    public static void writeInventory(ItemStack stack, NonNullList<ItemStack> inventory) {
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory));
    }

    public static int readSelected(ItemStack stack) {
        int selected = stack.getOrDefault(NVDataComponents.READER_STATE.get(), 0);
        return Math.max(0, Math.min(selected, sizeOf(stack) - 1));
    }

    public static void writeSelected(ItemStack stack, int slot) {
        if (slot >= 0 && slot < sizeOf(stack)) {
            stack.set(NVDataComponents.READER_STATE.get(), slot);
        }
    }

    public static void cycle(ItemStack stack, int direction) {
        NonNullList<ItemStack> inventory = readInventory(stack);
        int size = inventory.size();
        int index = readSelected(stack);
        int attempts = 0;
        do {
            index = Math.floorMod(index + direction, size);
            attempts++;
        } while (inventory.get(index).isEmpty() && attempts < size);
        writeSelected(stack, index);
    }
}
