package com.titammods.not_enough_sigils.client;

import com.titammods.not_enough_sigils.NotEnoughSigils;
import com.titammods.not_enough_sigils.holding.GreaterHoldingMenu;
import com.titammods.not_enough_sigils.holding.GreaterHoldingSigilItem;
import com.titammods.not_enough_sigils.network.GreaterHoldingSelectPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class GreaterHoldingScreen extends AbstractContainerScreen<GreaterHoldingMenu> {
    private static final Identifier BACKGROUND_ONE_ROW = NotEnoughSigils.rl("textures/gui/sigil_holding_greater.png");
    private static final Identifier BACKGROUND_TWO_ROWS = NotEnoughSigils.rl("textures/gui/sigil_holding_supreme.png");
    private static final int TEXTURE_SIZE = 256;
    private static final int IMAGE_WIDTH = 176;
    private static final int SLOT_SIZE = 16;
    private static final int SELECTION_COLOR = 0xFFE0B040;

    private final Identifier background;

    public GreaterHoldingScreen(GreaterHoldingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, IMAGE_WIDTH, menu.getPlayerInventoryY() + 82);
        this.background = menu.getRows() > 1 ? BACKGROUND_TWO_ROWS : BACKGROUND_ONE_ROW;
        this.inventoryLabelY = menu.getPlayerInventoryY() - 11;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, TEXTURE_SIZE, TEXTURE_SIZE);

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
    public boolean mouseClicked(MouseButtonEvent event, boolean dragging) {
        if (event.button() == 0 && (minecraft == null || !minecraft.hasShiftDown())) {
            for (int i = 0; i < menu.getSize(); i++) {
                int slotX = leftPos + slotX(i);
                int slotY = topPos + slotY(i);
                if (event.x() >= slotX && event.x() < slotX + SLOT_SIZE && event.y() >= slotY && event.y() < slotY + SLOT_SIZE) {
                    menu.setSelectedSlot(i);
                    ClientPacketDistributor.sendToServer(new GreaterHoldingSelectPayload(i));
                    return true;
                }
            }
        }
        return super.mouseClicked(event, dragging);
    }

    private static int slotX(int index) {
        return GreaterHoldingMenu.SLOT_X + (index % GreaterHoldingSigilItem.ROW_SIZE) * GreaterHoldingMenu.SLOT_SPACING;
    }

    private static int slotY(int index) {
        return GreaterHoldingMenu.SLOT_Y + (index / GreaterHoldingSigilItem.ROW_SIZE) * GreaterHoldingMenu.SLOT_SPACING;
    }
}
