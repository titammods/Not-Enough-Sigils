package com.titammods.not_enough_sigils.holding;

import com.breakinblocks.neovitae.common.item.sigil.ISigil;
import com.breakinblocks.neovitae.common.item.sigil.ItemSigilHolding;
import com.titammods.not_enough_sigils.setup.ModMenus;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class GreaterHoldingMenu extends AbstractContainerMenu {
    public static final int SLOT_X = 8;
    public static final int SLOT_Y = 18;
    public static final int SLOT_SPACING = 18;

    private final ItemStack holdingStack;
    private final int holdingSlot;
    private final int size;
    private final int rows;
    private final ItemStackHandler sigilInventory;
    private int selectedSlot;

    public GreaterHoldingMenu(int containerId, Inventory playerInventory, ItemStack holdingStack, int holdingSlot) {
        super(ModMenus.GREATER_HOLDING.get(), containerId);
        this.holdingStack = holdingStack;
        this.holdingSlot = holdingSlot;
        this.size = GreaterHoldingSigilItem.sizeOf(holdingStack);
        this.rows = Math.max(1, (size + GreaterHoldingSigilItem.ROW_SIZE - 1) / GreaterHoldingSigilItem.ROW_SIZE);
        this.selectedSlot = GreaterHoldingSigilItem.readSelected(holdingStack);

        this.sigilInventory = new ItemStackHandler(size) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return isAcceptedSigil(stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                saveToHoldingStack();
            }
        };

        NonNullList<ItemStack> stored = GreaterHoldingSigilItem.readInventory(holdingStack);
        for (int i = 0; i < size; i++) {
            sigilInventory.setStackInSlot(i, stored.get(i).copy());
        }

        for (int i = 0; i < size; i++) {
            final int index = i;
            int x = SLOT_X + (i % GreaterHoldingSigilItem.ROW_SIZE) * SLOT_SPACING;
            int y = SLOT_Y + (i / GreaterHoldingSigilItem.ROW_SIZE) * SLOT_SPACING;
            this.addSlot(new SlotItemHandler(sigilInventory, i, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return sigilInventory.isItemValid(index, stack);
                }
            });
        }

        int inventoryY = getPlayerInventoryY();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, inventoryY + row * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            final int hotbarSlot = i;
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, inventoryY + 58) {
                @Override
                public boolean mayPickup(Player player) {
                    return hotbarSlot != holdingSlot;
                }
            });
        }
    }

    public GreaterHoldingMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readInt());
    }

    private GreaterHoldingMenu(int containerId, Inventory playerInventory, int slot) {
        this(containerId, playerInventory, playerInventory.getItem(slot), slot);
    }

    public static boolean isAcceptedSigil(ItemStack stack) {
        return stack.getItem() instanceof ISigil && !(stack.getItem() instanceof ItemSigilHolding);
    }

    public int getSize() {
        return size;
    }

    public int getRows() {
        return rows;
    }

    public int getPlayerInventoryY() {
        return 32 + rows * SLOT_SPACING;
    }

    private void saveToHoldingStack() {
        NonNullList<ItemStack> inventory = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            inventory.set(i, sigilInventory.getStackInSlot(i).copy());
        }
        GreaterHoldingSigilItem.writeInventory(holdingStack, inventory);
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < size) {
            this.selectedSlot = slot;
            GreaterHoldingSigilItem.writeSelected(holdingStack, slot);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return result;
        }

        ItemStack slotStack = slot.getItem();
        result = slotStack.copy();

        if (index < size) {
            if (!this.moveItemStackTo(slotStack, size, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (isAcceptedSigil(slotStack)) {
            if (!this.moveItemStackTo(slotStack, 0, size, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < this.slots.size()) {
            Slot slot = this.slots.get(slotId);
            if (slot.getItem().getItem() instanceof ItemSigilHolding) {
                return;
            }
        }
        if (clickType == ClickType.SWAP && button == holdingSlot) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().getItem(holdingSlot).getItem() instanceof GreaterHoldingSigilItem;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        saveToHoldingStack();
    }
}
