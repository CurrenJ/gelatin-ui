package io.github.currenj.gelatinui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientItemStacksTooltip implements ClientTooltipComponent {
    private static final int MARGIN_Y = 4;
    private static final int BORDER_WIDTH = 1;
    private static final int SLOT_SIZE_X = 18;
    private static final int SLOT_SIZE_Y = 20;
    private final List<ItemStack> items;
    private final boolean renderItemDecorations;

    public ClientItemStacksTooltip(List<ItemStack> items, boolean renderItemDecorations) {
        this.items = items;
        this.renderItemDecorations = renderItemDecorations;
    }

    @Override
    public int getHeight() {
        return isShowing() ? this.backgroundHeight() : 0;
    }

    @Override
    public int getWidth(Font font) {
        return isShowing() ? this.backgroundWidth() : 0;
    }

    private int backgroundWidth() {
        return isShowing() ? this.gridSizeX() * SLOT_SIZE_X + 2 : 0;
    }

    private int backgroundHeight() {
        return isShowing() ? this.gridSizeY() * SLOT_SIZE_Y + 2 : 0;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        if(isShowing()) {
            int xMax = this.gridSizeX();
            int yMax = this.gridSizeY();
            int k = 0;

            for (int yIndex = 0; yIndex < yMax; yIndex++) {
                for (int xIndex = 0; xIndex < xMax; xIndex++) {
                    int j1 = x + xIndex * SLOT_SIZE_X + BORDER_WIDTH;
                    int k1 = y + yIndex * SLOT_SIZE_Y + BORDER_WIDTH;
                    this.renderSlot(j1, k1, k++, guiGraphics, font);
                }
            }
        }
    }

    private boolean isShowing() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null;
    }

    private void renderSlot(int x, int y, int itemIndex, GuiGraphics guiGraphics, Font font) {
        if (itemIndex < this.items.size()) {
            ItemStack itemstack = this.items.get(itemIndex);
            if (itemstack.isEmpty()) {
                return;
            }

            guiGraphics.renderItem(itemstack, x + 1, y + 1, itemIndex);
            if (this.renderItemDecorations) {
                guiGraphics.renderItemDecorations(font, itemstack, x + 1, y + 1);
            }
        }
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, Texture texture) {
        guiGraphics.blitSprite(texture.sprite, x, y, texture.w, texture.h);
    }

    private int gridSizeX() {
        return isShowing() ? Math.clamp(this.items.size(), 1, 9) : 0;
    }

    private int gridSizeY() {
        return isShowing() ? (int) (Math.ceil(this.items.size() / (double) gridSizeX())) : 0;
    }

    enum Texture {
        SLOT(ResourceLocation.withDefaultNamespace("container/slot"), 18, 20);

        public final ResourceLocation sprite;
        public final int w;
        public final int h;

        Texture(ResourceLocation sprite, int w, int h) {
            this.sprite = sprite;
            this.w = w;
            this.h = h;
        }
    }
}
