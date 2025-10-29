package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.components.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * Builder utility class for creating UI components with a fluent API.
 * Provides convenient factory methods for common components.
 */
public class UI {
    /**
     * Create a new VBox (vertical layout container).
     */
    public static VBox vbox() {
        return new VBox();
    }

    /**
     * Create a new HBox (horizontal layout container).
     */
    public static HBox hbox() {
        return new HBox();
    }

    /**
     * Create a new Panel container.
     */
    public static Panel panel() {
        return new Panel();
    }

    /**
     * Create a new Label with text and color.
     */
    public static Label label(IRenderContext context, String text, int color) {
        return new Label(text, color).init(context);
    }

    /**
     * Create a new Rectangle with size and color.
     */
    public static Rectangle rectangle(float width, float height, int color) {
        return new Rectangle(width, height, color);
    }

    /**
     * Create a new SpriteRectangle backed by a texture.
     */
    public static SpriteRectangle.SpriteRectangleImpl spriteRectangle(float width, float height, ResourceLocation texture) {
        return new SpriteRectangle.SpriteRectangleImpl(width, height, texture);
    }

    /**
     * Create a new SpriteRectangle backed by a solid color.
     */
    public static SpriteRectangle.SpriteRectangleImpl spriteRectangle(float width, float height, int color) {
        return new SpriteRectangle.SpriteRectangleImpl(width, height, color);
    }

    /**
     * Create a new SpriteButton backed by a texture.
     */
    public static SpriteRectangle.SpriteRectangleImpl spriteButton(float width, float height, ResourceLocation texture) {
        return new SpriteRectangle.SpriteRectangleImpl(width, height, texture);
    }

    /**
     * Create a new SpriteButton backed by a solid color.
     */
    public static SpriteButton spriteButton(float width, float height, int color) {
        return new SpriteButton(width, height, color);
    }

    /**
     * Create a new SpriteProgressBar with default dimensions.
     */
    public static SpriteProgressBar progressBar() {
        return new SpriteProgressBar();
    }

    /**
     * Create a new SpriteProgressBar with custom dimensions.
     */
    public static SpriteProgressBar progressBar(float width, float height) {
        return new SpriteProgressBar(width, height);
    }

    /**
     * Create a new ItemRenderer with default size (16x16).
     */
    public static ItemRenderer.ItemRendererImpl itemRenderer() {
        return new ItemRenderer.ItemRendererImpl();
    }

    /**
     * Create a new ItemRenderer with an ItemStack.
     */
    public static ItemRenderer.ItemRendererImpl itemRenderer(ItemStack itemStack) {
        return new ItemRenderer.ItemRendererImpl(itemStack);
    }

    /**
     * Create a new ItemRenderer with custom size.
     */
    public static ItemRenderer.ItemRendererImpl itemRenderer(float width, float height) {
        return new ItemRenderer.ItemRendererImpl(width, height);
    }

    /**
     * Create a new ItemRenderer with custom size and ItemStack.
     */
    public static ItemRenderer.ItemRendererImpl itemRenderer(float width, float height, ItemStack itemStack) {
        return new ItemRenderer.ItemRendererImpl(width, height, itemStack);
    }

    /**
     * Create a new ItemButton with default size (16x16).
     */
    public static ItemButton itemButton() {
        return new ItemButton();
    }

    /**
     * Create a new ItemButton with an ItemStack.
     */
    public static ItemButton itemButton(ItemStack itemStack) {
        return new ItemButton(itemStack);
    }

    /**
     * Create a new ItemButton with custom size.
     */
    public static ItemButton itemButton(float width, float height) {
        return new ItemButton(width, height);
    }

    /**
     * Create a new ItemButton with custom size and ItemStack.
     */
    public static ItemButton itemButton(float width, float height, ItemStack itemStack) {
        return new ItemButton(width, height, itemStack);
    }

    /**
     * Create a rotating item ring component.
     */
    public static RotatingItemRing rotatingItemRing() {
        return new RotatingItemRing();
    }

    /**
     * Create a new ItemTabs component.
     */
    public static ItemTabs itemTabs() {
        return new ItemTabs();
    }

    // Color utility methods

    /**
     * Create ARGB color from components.
     */
    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Create RGB color (fully opaque).
     */
    public static int rgb(int red, int green, int blue) {
        return argb(255, red, green, blue);
    }

    /**
     * Create color from hex string (e.g., "FFAABBCC").
     */
    public static int hex(String hex) {
        return (int) Long.parseLong(hex, 16);
    }
}
