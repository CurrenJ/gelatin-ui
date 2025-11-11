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
        
        // Translate to the destination position
        graphics.pose().translate(x, y, 0);

        // Apply rotation if specified
        float rotationY = sprite.itemRotationY();
        float rotationZ = sprite.itemRotationZ();
        
        // Scale the item to fit the destination size
        // Standard item size is 16x16, so scale accordingly
        float scale = Math.min(width / 16.0f, height / 16.0f);
        graphics.pose().scale(scale, scale, 1.0f);

        // Center the item in the destination area if it doesn't fill it completely
        float centerOffsetX = (width / scale - 16) / 2;
        float centerOffsetY = (height / scale - 16) / 2;
        graphics.pose().translate(centerOffsetX, centerOffsetY, 0);

        // Apply Y-axis rotation (spin effect)
        if (rotationY != 0) {
            graphics.pose().translate(8, 8, 0); // Center of item
            graphics.pose().mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotationY));
            graphics.pose().translate(-8, -8, 0);
        }

        // Apply Z-axis rotation (coin spin effect)
        if (rotationZ != 0) {
            graphics.pose().translate(8, 8, 0); // Center of item
            graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(rotationZ));
            graphics.pose().translate(-8, -8, 0);
        }

        // Handle UV bounds if specified
        // If regionW and regionH are set, we need to scissor the rendered item
        int regionW = sprite.regionW();
        int regionH = sprite.regionH();
        int u = sprite.u();
        int v = sprite.v();
        
        if (regionW > 0 && regionH > 0) {
            // Enable scissor to clip to the UV region
            // Note: This is a simplification - proper UV clipping for 3D items is complex
            // For now, we'll just render the full item within the bounds
            pushScissor((int)x, (int)y, width, height);
            graphics.renderItem(itemStack, 0, 0);
            popScissor();
        } else {
            // Render the full item
            graphics.renderItem(itemStack, 0, 0);
        }

        graphics.pose().popPose();
    }
}
