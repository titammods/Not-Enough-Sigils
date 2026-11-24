package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.holding.GreaterHoldingMenu;
import com.titammods.not_enough_sigils.holding.GreaterHoldingSigilItem;
import com.titammods.not_enough_sigils.network.GreaterHoldingSelectPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class GreaterHoldingScreen extends AbstractContainerScreen<GreaterHoldingMenu> {
    private static final ResourceLocation BACKGROUND_ONE_ROW = NotEnoughSigils.rl("textures/gui/sigil_holding_greater.png");
    private static final ResourceLocation BACKGROUND_TWO_ROWS = NotEnoughSigils.rl("textures/gui/sigil_holding_supreme.png");
    private static final ResourceLocation BACKGROUND_THREE_ROWS = NotEnoughSigils.rl("textures/gui/sigil_holding_divine.png");
    private static final int SLOT_SIZE = 16;
    private static final int SELECTION_COLOR = 0xFFE0B040;

    private final ResourceLocation background;

    public GreaterHoldingScreen(GreaterHoldingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.background = menu.getRows() > 2 ? BACKGROUND_THREE_ROWS : menu.getRows() > 1 ? BACKGROUND_TWO_ROWS : BACKGROUND_ONE_ROW;
        this.imageWidth = 176;
        this.imageHeight = menu.getPlayerInventoryY() + 82;
        this.inventoryLabelY = menu.getPlayerInventoryY() - 11;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(background, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int selected = menu.getSelectedSlot();
        int x = leftPos + slotX(selected) - 1;
        int y = topPos + slotY(selected) - 1;
        int size = SLOT_SIZE + 2;
        guiGraphics.fill(x - 1, y - 1, x + size + 1, y, SELECTION_COLOR);
        guiGraphics.fill(x - 1, y + size, x + size + 1, y + size + 1, SELECTION_COLOR);
        guiGraphics.fill(x - 1, y, x, y + size, SELECTION_COLOR);
        guiGraphics.fill(x + size, y, x + size + 1, y + size, SELECTION_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !hasShiftDown()) {
            for (int i = 0; i < menu.getSize(); i++) {
                int slotX = leftPos + slotX(i);
                int slotY = topPos + slotY(i);
                if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE && mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {
                    menu.setSelectedSlot(i);
                    PacketDistributor.sendToServer(new GreaterHoldingSelectPayload(i));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static int slotX(int index) {
        return GreaterHoldingMenu.SLOT_X + (index % GreaterHoldingSigilItem.ROW_SIZE) * GreaterHoldingMenu.SLOT_SPACING;
    }

    private static int slotY(int index) {
        return GreaterHoldingMenu.SLOT_Y + (index / GreaterHoldingSigilItem.ROW_SIZE) * GreaterHoldingMenu.SLOT_SPACING;
    }
}
