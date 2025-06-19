package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.CrateTech;
import com.blocklogic.cratetech.component.HopperSettings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class HopperUpgradeScreen extends AbstractContainerScreen<HopperUpgradeMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CrateTech.MODID, "textures/gui/hopper_upgrade_gui.png");

    private static final int[] SIDE_BUTTON_X = {79, 79, 79, 100, 100, 58}; // UP, DOWN, NORTH, SOUTH, EAST, WEST
    private static final int[] SIDE_BUTTON_Y = {14, 56, 35, 56, 35, 35};
    private static final int SIDE_BUTTON_SIZE = 18;

    private static final int PUSH_BUTTON_X = 64;
    private static final int PUSH_BUTTON_Y = 20;
    private static final int PULL_BUTTON_X = 100;
    private static final int PULL_BUTTON_Y = 20;
    private static final int RESET_BUTTON_X = 64;
    private static final int RESET_BUTTON_Y = 56;
    private static final int MODE_BUTTON_SIZE = 12;

    private static final int SIDE_BUTTON_U = 176;
    private static final int SIDE_BUTTON_V = 0;
    private static final int SIDE_BUTTON_HOVER_V = 18;
    private static final int SIDE_BUTTON_PUSH_U = 194;
    private static final int SIDE_BUTTON_PULL_U = 212;

    private static final int PUSH_BUTTON_U = 176;
    private static final int PUSH_BUTTON_V = 36;
    private static final int PULL_BUTTON_V = 48;
    private static final int RESET_BUTTON_V = 60;

    private static final int[] DIRECTION_MAPPING = {1, 0, 2, 3, 5, 4};
    private static final Direction[] DIRECTIONS = {Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public HopperUpgradeScreen(HopperUpgradeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 172;
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

        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        for (int i = 0; i < 6; i++) {
            renderSideButton(guiGraphics, x, y, i, mouseX, mouseY);
        }

        renderPushButton(guiGraphics, x, y, mouseX, mouseY);
        renderPullButton(guiGraphics, x, y, mouseX, mouseY);
        renderResetButton(guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        for (int i = 0; i < 6; i++) {
            renderAdjacentBlockIcon(guiGraphics, i);
        }
    }

    private void renderSideButton(GuiGraphics guiGraphics, int guiX, int guiY, int sideIndex, int mouseX, int mouseY) {
        int buttonX = guiX + SIDE_BUTTON_X[sideIndex];
        int buttonY = guiY + SIDE_BUTTON_Y[sideIndex];

        boolean isHovered = isMouseOverButton(mouseX, mouseY, buttonX, buttonY, SIDE_BUTTON_SIZE, SIDE_BUTTON_SIZE);

        HopperSettings.SideMode mode = menu.getSideMode(DIRECTION_MAPPING[sideIndex]);

        int u = switch (mode) {
            case DISABLED -> SIDE_BUTTON_U;
            case PUSH -> SIDE_BUTTON_PUSH_U;
            case PULL -> SIDE_BUTTON_PULL_U;
        };

        int v = isHovered ? SIDE_BUTTON_HOVER_V : SIDE_BUTTON_V;

        guiGraphics.blit(TEXTURE, buttonX, buttonY, u, v, SIDE_BUTTON_SIZE, SIDE_BUTTON_SIZE);
    }

    private void renderAdjacentBlockIcon(GuiGraphics guiGraphics, int sideIndex) {
        BlockPos cratePos = menu.getCrateEntity().getBlockPos();
        Direction direction = DIRECTIONS[sideIndex];
        BlockPos adjacentPos = cratePos.relative(direction);

        if (minecraft.level != null) {
            BlockState adjacentState = minecraft.level.getBlockState(adjacentPos);

            if (!adjacentState.isAir()) {
                ItemStack blockItem = new ItemStack(adjacentState.getBlock().asItem());

                if (!blockItem.isEmpty()) {
                    int iconX = SIDE_BUTTON_X[sideIndex] + 2;
                    int iconY = SIDE_BUTTON_Y[sideIndex] + 2;

                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().scale(0.875f, 0.875f, 1.0f);

                    int scaledX = (int)(iconX / 0.875f);
                    int scaledY = (int)(iconY / 0.875f);

                    guiGraphics.renderItem(blockItem, scaledX, scaledY);

                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    private void renderPushButton(GuiGraphics guiGraphics, int guiX, int guiY, int mouseX, int mouseY) {
        int buttonX = guiX + PUSH_BUTTON_X;
        int buttonY = guiY + PUSH_BUTTON_Y;

        boolean isHovered = isMouseOverButton(mouseX, mouseY, buttonX, buttonY, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
        boolean isActive = menu.isPushModeActive();

        int u = PUSH_BUTTON_U;
        if (isActive) u += 24;
        if (isHovered) u += 12;

        guiGraphics.blit(TEXTURE, buttonX, buttonY, u, PUSH_BUTTON_V, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
    }

    private void renderPullButton(GuiGraphics guiGraphics, int guiX, int guiY, int mouseX, int mouseY) {
        int buttonX = guiX + PULL_BUTTON_X;
        int buttonY = guiY + PULL_BUTTON_Y;

        boolean isHovered = isMouseOverButton(mouseX, mouseY, buttonX, buttonY, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
        boolean isActive = menu.isPullModeActive();

        int u = PUSH_BUTTON_U;
        if (isActive) u += 24;
        if (isHovered) u += 12;

        guiGraphics.blit(TEXTURE, buttonX, buttonY, u, PULL_BUTTON_V, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
    }

    private void renderResetButton(GuiGraphics guiGraphics, int guiX, int guiY, int mouseX, int mouseY) {
        int buttonX = guiX + RESET_BUTTON_X;
        int buttonY = guiY + RESET_BUTTON_Y;

        boolean isHovered = isMouseOverButton(mouseX, mouseY, buttonX, buttonY, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);

        int u = PUSH_BUTTON_U;
        if (isHovered) u += 12;

        guiGraphics.blit(TEXTURE, buttonX, buttonY, u, RESET_BUTTON_V, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE);
    }

    private boolean isMouseOverButton(double mouseX, double mouseY, int buttonX, int buttonY, int width, int height) {
        return mouseX >= buttonX && mouseX < buttonX + width &&
                mouseY >= buttonY && mouseY < buttonY + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        for (int i = 0; i < 6; i++) {
            int buttonX = x + SIDE_BUTTON_X[i];
            int buttonY = y + SIDE_BUTTON_Y[i];

            if (isMouseOverButton(mouseX, mouseY, buttonX, buttonY, SIDE_BUTTON_SIZE, SIDE_BUTTON_SIZE)) {
                menu.toggleSideConfig(DIRECTION_MAPPING[i]);
                return true;
            }
        }

        if (isMouseOverButton(mouseX, mouseY, x + PUSH_BUTTON_X, y + PUSH_BUTTON_Y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE)) {
            menu.togglePushMode();
            return true;
        }

        if (isMouseOverButton(mouseX, mouseY, x + PULL_BUTTON_X, y + PULL_BUTTON_Y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE)) {
            menu.togglePullMode();
            return true;
        }

        if (isMouseOverButton(mouseX, mouseY, x + RESET_BUTTON_X, y + RESET_BUTTON_Y, MODE_BUTTON_SIZE, MODE_BUTTON_SIZE)) {
            menu.resetHopperSettings();
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