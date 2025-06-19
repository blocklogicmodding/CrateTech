package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.item.CTItems;
import com.blocklogic.cratetech.network.CTNetworkHandler;
import com.blocklogic.cratetech.network.OpenCollectorUpgradePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MediumCrateScreen extends AbstractContainerScreen<MediumCrateMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CrateTech.MODID, "textures/gui/medium_crate_gui.png");
    private static final ResourceLocation GUI_ELEMENTS =
            ResourceLocation.fromNamespaceAndPath(CrateTech.MODID, "textures/gui/gui_elements.png");

    private final BlockPos cratePos;

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    private static final int COLLECTOR_BUTTON_X = 176;
    private static final int COLLECTOR_BUTTON_Y = 0;
    private static final int HOPPER_BUTTON_X = 176;
    private static final int HOPPER_BUTTON_Y = 15;
    private static final int COMPACTING_BUTTON_X = 176;
    private static final int COMPACTING_BUTTON_Y = 30;
    private static final int BUTTON_SIZE = 15;

    public MediumCrateScreen(MediumCrateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 8;
        this.cratePos = menu.getCratePos();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        guiGraphics.blit(GUI_ELEMENTS, x - 34, y, 62, 0, 32, 34);
        guiGraphics.blit(GUI_ELEMENTS, x - 34, y + 36, 0, 0, 32, 89);

        if (hasCollectorUpgrade()) {
            renderButton(guiGraphics, x, y, COLLECTOR_BUTTON_X, COLLECTOR_BUTTON_Y, 32, 0, 47, 0, mouseX, mouseY);
        }

        if (hasHopperUpgrade()) {
            renderButton(guiGraphics, x, y, HOPPER_BUTTON_X, HOPPER_BUTTON_Y, 32, 15, 47, 15, mouseX, mouseY);
        }

        if (hasCompactingUpgrade()) {
            renderButton(guiGraphics, x, y, COMPACTING_BUTTON_X, COMPACTING_BUTTON_Y, 32, 30, 47, 30, mouseX, mouseY);
        }
    }

    private boolean hasCollectorUpgrade() {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = menu.getSlot(28 + i).getItem(); // UPGRADE_SLOTS_START = 28
            if (!stack.isEmpty() && stack.getItem() == CTItems.COLLECTOR_UPGRADE.get()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasHopperUpgrade() {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = menu.getSlot(28 + i).getItem(); // UPGRADE_SLOTS_START = 28
            if (!stack.isEmpty() && stack.getItem() == CTItems.HOPPER_UPGRADE.get()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCompactingUpgrade() {
        for (int i = 0; i < 4; i++) {
            ItemStack stack = menu.getSlot(28 + i).getItem(); // UPGRADE_SLOTS_START = 28
            if (!stack.isEmpty() && stack.getItem() == CTItems.COMPACTING_UPGRADE.get()) {
                return true;
            }
        }
        return false;
    }

    private void renderButton(GuiGraphics guiGraphics, int guiX, int guiY, int buttonX, int buttonY,
                              int normalU, int normalV, int hoverU, int hoverV, int mouseX, int mouseY) {
        boolean isHovered = mouseX >= guiX + buttonX && mouseX < guiX + buttonX + BUTTON_SIZE &&
                mouseY >= guiY + buttonY && mouseY < guiY + buttonY + BUTTON_SIZE;

        if (isHovered) {
            guiGraphics.blit(GUI_ELEMENTS, guiX + buttonX, guiY + buttonY, hoverU, hoverV, BUTTON_SIZE, BUTTON_SIZE);
        } else {
            guiGraphics.blit(GUI_ELEMENTS, guiX + buttonX, guiY + buttonY, normalU, normalV, BUTTON_SIZE, BUTTON_SIZE);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (hasCollectorUpgrade() && isMouseOverButton(mouseX, mouseY, x + COLLECTOR_BUTTON_X, y + COLLECTOR_BUTTON_Y)) {
            CTNetworkHandler.sendToServer(new OpenCollectorUpgradePacket(cratePos));
            return true;
        }

        if (hasHopperUpgrade() && isMouseOverButton(mouseX, mouseY, x + HOPPER_BUTTON_X, y + HOPPER_BUTTON_Y)) {
            // TODO: Open hopper upgrade GUI
            return true;
        }

        if (hasCompactingUpgrade() && isMouseOverButton(mouseX, mouseY, x + COMPACTING_BUTTON_X, y + COMPACTING_BUTTON_Y)) {
            // TODO: Open compacting upgrade GUI
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverButton(double mouseX, double mouseY, int buttonX, int buttonY) {
        return mouseX >= buttonX && mouseX < buttonX + BUTTON_SIZE &&
                mouseY >= buttonY && mouseY < buttonY + BUTTON_SIZE;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}