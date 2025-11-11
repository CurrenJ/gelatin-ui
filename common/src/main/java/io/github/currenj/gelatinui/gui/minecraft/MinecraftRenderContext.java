package io.github.currenj.gelatinui.gui.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.components.SpriteData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Minecraft-specific implementation of IRenderContext.
 * Adapts Minecraft's GuiGraphics to the GUI system's rendering interface.
 */
public class MinecraftRenderContext implements IRenderContext {
    private final GuiGraphics graphics;
    private final Font font;

    public MinecraftRenderContext(GuiGraphics graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }

    @Override
    public void fill(int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y2, color);
    }

    @Override
    public void drawString(String text, int x, int y, int color) {
        graphics.drawString(font, text, x, y, color, false);
    }

    @Override
    public void drawCenteredString(String text, int x, int y, int color) {
        graphics.drawCenteredString(font, text, x, y, color);
    }

    @Override
    public int getStringWidth(String text) {
        return font.width(text);
    }

    @Override
    public int getFontHeight() {
        return font.lineHeight;
    }

    @Override
    public void pushScissor(int x, int y, int width, int height) {
        graphics.enableScissor(x, y, x + width, y + height);
    }

    @Override
    public void popScissor() {
        graphics.disableScissor();
    }

    @Override
    public void enableBlend() {
        RenderSystem.enableBlend();
    }

    @Override
    public void disableBlend() {
        RenderSystem.disableBlend();
    }

    @Override
    public void drawTexture(ResourceLocation texture, float x, float y, int width, int height) {
        drawTexture(texture, x, y, width, height, 0, 0, width, height, width, height);
    }

    @Override
    public void drawTexture(ResourceLocation texture, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight) {
        // GuiGraphics.blit signature: blit(ResourceLocation, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight)
        // where width/height are destination size, and textureWidth/textureHeight are the total atlas dimensions
        // Note: texWidth and texHeight (source region size) are implicitly the same as width and height when using this blit overload
        // So we need to use the innerBlit method instead for proper UV mapping
        IGuiGraphicsExtension ext = (IGuiGraphicsExtension) graphics;
        ext.gelatinui$blit(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    @Override
    public void drawTextureWithZ(ResourceLocation texture, float x, float y, float width, float height, float u, float v, float regionWidth, float regionHeight, int textureWidth, int textureHeight, float z) {
        IGuiGraphicsExtension ext = (IGuiGraphicsExtension) graphics;
        ext.gelatinui$blit(texture, x, x + width, y, y + height, z, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
    }

    /**
     * Get the underlying GuiGraphics for advanced rendering operations.
     */
    public GuiGraphics getGraphics() {
        return graphics;
    }

    /**
     * Get the Font for text rendering operations.
     */
    public Font getFont() {
        return font;
    }

    @Override
    public void drawItemSprite(SpriteData sprite, float x, float y, int width, int height) {
        if (sprite == null || sprite.itemId() == null) {
            return;
        }

        // Look up the item from the registry
        Item item = BuiltInRegistries.ITEM.get(sprite.itemId());
        if (item == null) {
            return;
        }

        ItemStack itemStack = new ItemStack(item);
        if (itemStack.isEmpty()) {
            return;
        }

        graphics.pose().pushPose();
        
        // Translate to the destination position with z-offset
        float zOffset = sprite.zOffset();
        graphics.pose().translate(x, y, zOffset);

        // Get region information
        int regionW = sprite.regionW();
        int regionH = sprite.regionH();
        int textureW = sprite.textureW();
        int textureH = sprite.textureH();
        
        // Determine the source region size
        // If regionW/H are not specified (0), use full item texture (16x16)
        final int baseWorldSize = 16;
        float sourceWidthWorldSize = regionW / (float) textureW * baseWorldSize;
        float sourceHeightWorldSize = regionH / (float) textureH * baseWorldSize;
        
        // Calculate scale to fit the region (not the full item texture) into destination bounds
        // Items are rendered as 16x16, but our region might be smaller/larger
        // The region should take up the full width/height specified
        float scaleX = width / sourceWidthWorldSize;
        float scaleY = height / sourceHeightWorldSize;
        float scale = Math.min(scaleX, scaleY);
        
        graphics.pose().scale(scale, scale, 1.0f);

        // Calculate the center of the item in world units for rotation
        float itemCenterX = sourceWidthWorldSize / 2f;
        float itemCenterY = sourceHeightWorldSize / 2f;

        // Apply rotations around the center of the region using rotateAround
        // Magic number 150 is the z-offset used by GuiGraphics for items to avoid big spin
        final float ITEM_DEPTH_OFFSET = 150f;
        
        float rotationY = sprite.itemRotationY();
        float rotationZ = sprite.itemRotationZ();
        
        if (Math.abs(rotationY) > 0.001f) {
            graphics.pose().rotateAround(
                com.mojang.math.Axis.YP.rotationDegrees(rotationY),
                itemCenterX, itemCenterY, ITEM_DEPTH_OFFSET
            );
        }
        
        if (Math.abs(rotationZ) > 0.001f) {
            graphics.pose().rotateAround(
                com.mojang.math.Axis.ZP.rotationDegrees(rotationZ),
                itemCenterX, itemCenterY, ITEM_DEPTH_OFFSET
            );
        }

        // Render the item at origin
        // The item will be rendered at 16x16, and any parts outside the region
        // will be transparent/blank as per the texture design
        graphics.renderItem(itemStack, 0, 0);

        graphics.pose().popPose();
    }
}
