package com.blocklogic.cratetech.screen.custom;

import com.blocklogic.cratetech.CrateTech;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ItemFilterScreen extends AbstractContainerScreen<ItemFilterMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CrateTech.MODID, "textures/gui/collector_filter_gui.png");

    private static final int WHITELIST_BUTTON_X = 136;
    private static final int WHITELIST_BUTTON_Y = 9;
    private static final int MATCH_TAG_BUTTON_X = 154;
    private static final int MATCH_TAG_BUTTON_Y = 9;
    private static final int BUTTON_SIZE = 12;

    // Button texture coordinates
    private static final int WHITELIST_ACTIVE_U = 176;
    private static final int WHITELIST_ACTIVE_V = 0;
    private static final int WHITELIST_ACTIVE_HOVER_U = 188;
    private static final int WHITELIST_ACTIVE_HOVER_V = 0;

    private static final int BLACKLIST_INACTIVE_U = 176;
    private static final int BLACKLIST_INACTIVE_V = 12;
    private static final int BLACKLIST_INACTIVE_HOVER_U = 188;
    private static final int BLACKLIST_INACTIVE_HOVER_V = 12;

    private static final int MATCH_TAG_ACTIVE_U = 176;
    private static final int MATCH_TAG_ACTIVE_V = 24;
    private static final int MATCH_TAG_ACTIVE_HOVER_U = 188;
    private static final int MATCH_TAG_ACTIVE_HOVER_V = 24;

    private static final int MATCH_TAG_INACTIVE_U = 176;
    private static final int MATCH_TAG_INACTIVE_V = 36;
    private static final int MATCH_TAG_INACTIVE_HOVER_U = 188;
    private static final int MATCH_TAG_INACTIVE_HOVER_V = 36;

    public ItemFilterScreen(ItemFilterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 181;
        this.inventoryLabelY = this.imageHeight - 96;
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

        renderWhitelistButton(guiGraphics, x, y, mouseX, mouseY);
        renderMatchTagButton(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderWhitelistButton(GuiGraphics guiGraphics, int guiX, int guiY, int mouseX, int mouseY) {
        int buttonX = guiX + WHITELIST_BUTTON_X;
        int buttonY = guiY + WHITELIST_BUTTON_Y;

        boolean isHovered = isMouseOverButton(mouseX, mouseY, buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE);
        boolean isWhitelist = menu.isWhitelistMode();

        int u, v;
        if (isWhitelist) {
            u = isHovered ? WHITELIST_ACTIVE_HOVER_U : WHITELIST_ACTIVE_U;
            v = isHovered ? WHITELIST_ACTIVE_HOVER_V : WHITELIST_ACTIVE_V;
        } else {
            u = isHovered ? BLACKLIST_INACTIVE_HOVER_U : BLACKLIST_INACTIVE_U;
            v = isHovered ? BLACKLIST_INACTIVE_HOVER_V : BLACKLIST_INACTIVE_V;
        }

        guiGraphics.blit(TEXTURE, buttonX, buttonY, u, v, BUTTON_SIZE, BUTTON_SIZE);
    }

    private void renderMatchTagButton(GuiGraphics guiGraphics, int guiX, int guiY, int mouseX, int mouseY) {
        int buttonX = guiX + MATCH_TAG_BUTTON_X;
        int buttonY = guiY + MATCH_TAG_BUTTON_Y;

        boolean isHovered = isMouseOverButton(mouseX, mouseY, buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE);
        boolean isMatchTags = menu.isMatchTags();

        int u, v;
        if (isMatchTags) {
            u = isHovered ? MATCH_TAG_ACTIVE_HOVER_U : MATCH_TAG_ACTIVE_U;
            v = isHovered ? MATCH_TAG_ACTIVE_HOVER_V : MATCH_TAG_ACTIVE_V;
        } else {
            u = isHovered ? MATCH_TAG_INACTIVE_HOVER_U : MATCH_TAG_INACTIVE_U;
            v = isHovered ? MATCH_TAG_INACTIVE_HOVER_V : MATCH_TAG_INACTIVE_V;
        }

        guiGraphics.blit(TEXTURE, buttonX, buttonY, u, v, BUTTON_SIZE, BUTTON_SIZE);
    }

    private boolean isMouseOverButton(double mouseX, double mouseY, int buttonX, int buttonY, int width, int height) {
        return mouseX >= buttonX && mouseX < buttonX + width &&
                mouseY >= buttonY && mouseY < buttonY + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (isMouseOverButton(mouseX, mouseY,
                x + WHITELIST_BUTTON_X, y + WHITELIST_BUTTON_Y,
                BUTTON_SIZE, BUTTON_SIZE)) {
            menu.toggleWhitelistMode();
            return true;
        }

        if (isMouseOverButton(mouseX, mouseY,
                x + MATCH_TAG_BUTTON_X, y + MATCH_TAG_BUTTON_Y,
                BUTTON_SIZE, BUTTON_SIZE)) {
            menu.toggleMatchTags();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (isMouseOverButton(mouseX, mouseY,
                x + WHITELIST_BUTTON_X, y + WHITELIST_BUTTON_Y,
                BUTTON_SIZE, BUTTON_SIZE)) {
            Component tooltip = menu.isWhitelistMode()
                    ? Component.translatable("gui.cratetech.item_filter.whitelist_mode")
                    : Component.translatable("gui.cratetech.item_filter.blacklist_mode");
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if (isMouseOverButton(mouseX, mouseY,
                x + MATCH_TAG_BUTTON_X, y + MATCH_TAG_BUTTON_Y,
                BUTTON_SIZE, BUTTON_SIZE)) {
            Component tooltip = menu.isMatchTags()
                    ? Component.translatable("gui.cratetech.item_filter.match_tags_on")
                    : Component.translatable("gui.cratetech.item_filter.match_tags_off");
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}