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
     * Create a new Label with text.
     */
    public static Label label(String text) {
        return new Label(text);
    }

    /**
     * Create a new Label with text and color.
     */
    public static Label label(String text, int color) {
        return new Label(text, color);
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
    public static SpriteRectangle spriteRectangle(float width, float height, ResourceLocation texture) {
        return new SpriteRectangle(width, height, texture);
    }

    /**
     * Create a new SpriteRectangle backed by a solid color.
     */
    public static SpriteRectangle spriteRectangle(float width, float height, int color) {
        return new SpriteRectangle(width, height, color);
    }

    /**
     * Create a new SpriteButton backed by a texture.
     */
    public static SpriteButton spriteButton(float width, float height, ResourceLocation texture) {
        return new SpriteButton(width, height, texture);
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
    public static ItemRenderer itemRenderer() {
        return new ItemRenderer();
    }

    /**
     * Create a new ItemRenderer with an ItemStack.
     */
    public static ItemRenderer itemRenderer(ItemStack itemStack) {
        return new ItemRenderer(itemStack);
    }

    /**
     * Create a new ItemRenderer with custom size.
     */
    public static ItemRenderer itemRenderer(float width, float height) {
        return new ItemRenderer(width, height);
    }

    /**
     * Create a new ItemRenderer with custom size and ItemStack.
     */
    public static ItemRenderer itemRenderer(float width, float height, ItemStack itemStack) {
        return new ItemRenderer(width, height, itemStack);
    }

    /**
     * Create a rotating item ring component.
     */
    public static RotatingItemRing rotatingItemRing() {
        return new RotatingItemRing();
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
