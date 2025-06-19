package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.CrateTech;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CollectorUpgradeScreen extends AbstractContainerScreen<CollectorUpgradeMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CrateTech.MODID, "textures/gui/collector_upgrade_gui.png");

    // Direction constants
    private static final int DOWN = 0;
    private static final int UP = 1;
    private static final int NORTH = 2;
    private static final int SOUTH = 3;
    private static final int WEST = 4;
    private static final int EAST = 5;

    // Direction labels
    private static final String[] DIRECTION_LABELS = {"D", "U", "N", "S", "W", "E"};

    // Button positions
    private static final int[] INCREASE_BUTTON_X = {7, 26, 45, 64, 83, 102};
    private static final int[] INCREASE_BUTTON_Y = {15, 15, 15, 15, 15, 15};
    private static final int[] DECREASE_BUTTON_X = {7, 26, 45, 64, 83, 102};
    private static final int[] DECREASE_BUTTON_Y = {39, 39, 39, 39, 39, 39};

    // Label positions
    private static final int[] VALUE_LABEL_X = {8, 28, 46, 65, 84, 103};
    private static final int[] VALUE_LABEL_Y = {28, 28, 28, 28, 28, 28};
    private static final int[] DIR_LABEL_X = {11, 30, 49, 68, 87, 106};
    private static final int[] DIR_LABEL_Y = {51, 51, 51, 51, 51, 51};

    // Button sizes
    private static final int ADJUST_BUTTON_WIDTH = 16;
    private static final int ADJUST_BUTTON_HEIGHT = 10;
    private static final int RESET_BUTTON_WIDTH = 34;
    private static final int RESET_BUTTON_HEIGHT = 10;
    private static final int WIREFRAME_BUTTON_SIZE = 10;

    // Reset and wireframe button positions
    private static final int RESET_BUTTON_X = 135;
    private static final int RESET_BUTTON_Y = 27;
    private static final int WIREFRAME_BUTTON_X = 159;
    private static final int WIREFRAME_BUTTON_Y = 39;

    public CollectorUpgradeScreen(CollectorUpgradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 158;
        this.inventoryLabelY = this.imageHeight - 94;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Render main GUI background
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Render direction adjustment buttons
        for (int dir = 0; dir < 6; dir++) {
            // Increase buttons
            boolean increaseHover = isMouseOverButton(mouseX, mouseY,
                    x + INCREASE_BUTTON_X[dir], y + INCREASE_BUTTON_Y[dir],
                    ADJUST_BUTTON_WIDTH, ADJUST_BUTTON_HEIGHT);
            renderIncreaseButton(guiGraphics, x + INCREASE_BUTTON_X[dir], y + INCREASE_BUTTON_Y[dir], increaseHover);

            // Decrease buttons
            boolean decreaseHover = isMouseOverButton(mouseX, mouseY,
                    x + DECREASE_BUTTON_X[dir], y + DECREASE_BUTTON_Y[dir],
                    ADJUST_BUTTON_WIDTH, ADJUST_BUTTON_HEIGHT);
            renderDecreaseButton(guiGraphics, x + DECREASE_BUTTON_X[dir], y + DECREASE_BUTTON_Y[dir], decreaseHover);
        }

        // Render reset button
        boolean resetHover = isMouseOverButton(mouseX, mouseY,
                x + RESET_BUTTON_X, y + RESET_BUTTON_Y,
                RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT);
        renderResetButton(guiGraphics, x + RESET_BUTTON_X, y + RESET_BUTTON_Y, resetHover);

        // Render wireframe toggle button
        boolean wireframeHover = isMouseOverButton(mouseX, mouseY,
                x + WIREFRAME_BUTTON_X, y + WIREFRAME_BUTTON_Y,
                WIREFRAME_BUTTON_SIZE, WIREFRAME_BUTTON_SIZE);
        renderWireframeButton(guiGraphics, x + WIREFRAME_BUTTON_X, y + WIREFRAME_BUTTON_Y, wireframeHover);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        // Render direction labels and values
        for (int dir = 0; dir < 6; dir++) {
            // Direction letter (D, U, N, S, W, E)
            String dirLabel = DIRECTION_LABELS[dir];
            int dirLabelWidth = this.font.width(dirLabel);
            guiGraphics.drawString(this.font, dirLabel,
                    DIR_LABEL_X[dir] - dirLabelWidth / 2, DIR_LABEL_Y[dir],
                    0x404040, false);

            // Current adjustment value (+/-number)
            int adjustment = menu.getCollectionAdjustment(dir);
            String valueLabel = (adjustment >= 0 ? "+" : "") + adjustment;
            int valueLabelWidth = this.font.width(valueLabel);
            guiGraphics.drawString(this.font, valueLabel,
                    VALUE_LABEL_X[dir] - valueLabelWidth / 2, VALUE_LABEL_Y[dir],
                    0x404040, false);
        }

        // Reset button label
        String resetLabel = "RESET";
        int resetLabelWidth = this.font.width(resetLabel);
        guiGraphics.drawString(this.font, resetLabel,
                RESET_BUTTON_X + 2 + (30 - resetLabelWidth) / 2, RESET_BUTTON_Y + 2,
                0x404040, false);
    }

    private void renderIncreaseButton(GuiGraphics guiGraphics, int x, int y, boolean hover) {
        int u = hover ? 192 : 176;
        int v = 0;
        guiGraphics.blit(TEXTURE, x, y, u, v, ADJUST_BUTTON_WIDTH, ADJUST_BUTTON_HEIGHT);
    }

    private void renderDecreaseButton(GuiGraphics guiGraphics, int x, int y, boolean hover) {
        int u = 176;
        int v = hover ? 30 : 20;
        guiGraphics.blit(TEXTURE, x, y, u, v, ADJUST_BUTTON_WIDTH, ADJUST_BUTTON_HEIGHT);
    }

    private void renderResetButton(GuiGraphics guiGraphics, int x, int y, boolean hover) {
        int u = hover ? 192 : 172;
        int v = 10;
        guiGraphics.blit(TEXTURE, x, y, u, v, RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT);
    }

    private void renderWireframeButton(GuiGraphics guiGraphics, int x, int y, boolean hover) {
        int u = hover ? 186 : 176;
        int v = 40;
        guiGraphics.blit(TEXTURE, x, y, u, v, WIREFRAME_BUTTON_SIZE, WIREFRAME_BUTTON_SIZE);
    }

    private boolean isMouseOverButton(double mouseX, double mouseY, int buttonX, int buttonY, int width, int height) {
        return mouseX >= buttonX && mouseX < buttonX + width &&
                mouseY >= buttonY && mouseY < buttonY + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Check direction adjustment buttons
        for (int dir = 0; dir < 6; dir++) {
            // Increase button
            if (isMouseOverButton(mouseX, mouseY,
                    x + INCREASE_BUTTON_X[dir], y + INCREASE_BUTTON_Y[dir],
                    ADJUST_BUTTON_WIDTH, ADJUST_BUTTON_HEIGHT)) {
                menu.adjustCollectionZone(dir, 1);
                return true;
            }

            // Decrease button
            if (isMouseOverButton(mouseX, mouseY,
                    x + DECREASE_BUTTON_X[dir], y + DECREASE_BUTTON_Y[dir],
                    ADJUST_BUTTON_WIDTH, ADJUST_BUTTON_HEIGHT)) {
                menu.adjustCollectionZone(dir, -1);
                return true;
            }
        }

        // Reset button
        if (isMouseOverButton(mouseX, mouseY,
                x + RESET_BUTTON_X, y + RESET_BUTTON_Y,
                RESET_BUTTON_WIDTH, RESET_BUTTON_HEIGHT)) {
            menu.resetCollectionZone();
            return true;
        }

        // Wireframe toggle button
        if (isMouseOverButton(mouseX, mouseY,
                x + WIREFRAME_BUTTON_X, y + WIREFRAME_BUTTON_Y,
                WIREFRAME_BUTTON_SIZE, WIREFRAME_BUTTON_SIZE)) {
            menu.toggleWireframe();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}