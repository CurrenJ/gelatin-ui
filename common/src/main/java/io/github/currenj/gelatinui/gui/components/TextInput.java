package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.UIEvent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

/**
 * A text input field component for user text entry.
 * Supports placeholder text, character limits, focus states, and validation.
 */
public class TextInput extends UIElement<TextInput> {
    private String text = "";
    private String placeholder = "";
    private int maxLength = 32;
    private int textColor = 0xFFFFFFFF;
    private int placeholderColor = 0xFF888888;
    private int backgroundColor = 0xFF000000;
    private int borderColor = 0xFF404040;
    private int focusedBorderColor = 0xFFFFFFFF;
    private boolean focused = false;
    private float cursorBlinkTimer = 0f;
    private static final float CURSOR_BLINK_INTERVAL = 0.53f;
    private int cursorPosition = 0;

    // Padding for text inside the box
    private int paddingX = 4;
    private int paddingY = 4;

    // Callback for text changes
    private TextChangeListener onTextChange = null;

    /**
     * Functional interface for text change callbacks.
     */
    @FunctionalInterface
    public interface TextChangeListener {
        void onTextChange(String newText);
    }

    /**
     * Text alignment options for TextInput.
     */
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    private TextAlignment textAlignment = TextAlignment.LEFT;

    public TextInput(float width, float height) {
        this.size.set(width, height);
        markDirty(DirtyFlag.CONTENT);
    }

    public TextInput(Vector2f size) {
        this.size.set(size);
        markDirty(DirtyFlag.CONTENT);
    }

    /**
     * Set the current text value.
     */
    public TextInput text(String text) {
        if (text == null) text = "";
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength);
        }
        if (!this.text.equals(text)) {
            this.text = text;
            this.cursorPosition = Math.min(cursorPosition, text.length());
            markDirty(DirtyFlag.CONTENT);
            if (onTextChange != null) {
                onTextChange.onTextChange(text);
            }
        }
        return this;
    }

    /**
     * Set placeholder text shown when input is empty.
     */
    public TextInput placeholder(String placeholder) {
        if (placeholder == null) placeholder = "";
        if (!this.placeholder.equals(placeholder)) {
            this.placeholder = placeholder;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set maximum character limit.
     */
    public TextInput maxLength(int maxLength) {
        if (this.maxLength != maxLength) {
            this.maxLength = maxLength;
            if (text.length() > maxLength) {
                text(text.substring(0, maxLength));
            }
        }
        return this;
    }

    /**
     * Set text color.
     */
    public TextInput textColor(int color) {
        if (this.textColor != color) {
            this.textColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set placeholder text color.
     */
    public TextInput placeholderColor(int color) {
        if (this.placeholderColor != color) {
            this.placeholderColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set background color.
     */
    public TextInput backgroundColor(int color) {
        if (this.backgroundColor != color) {
            this.backgroundColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set border color.
     */
    public TextInput borderColor(int color) {
        if (this.borderColor != color) {
            this.borderColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set focused border color.
     */
    public TextInput focusedBorderColor(int color) {
        if (this.focusedBorderColor != color) {
            this.focusedBorderColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set text change listener.
     */
    public TextInput onTextChange(TextChangeListener listener) {
        this.onTextChange = listener;
        return this;
    }

    /**
     * Set text alignment.
     */
    public TextInput alignment(TextAlignment alignment) {
        if (this.textAlignment != alignment) {
            this.textAlignment = alignment;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Get current text value.
     */
    public String getText() {
        return text;
    }

    /**
     * Get focused state.
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Set focused state.
     */
    public TextInput setFocused(boolean focused) {
        if (this.focused != focused) {
            this.focused = focused;
            if (focused) {
                cursorBlinkTimer = 0f;
            }
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Register a global click listener with the given screen to handle focus management.
     * This allows the TextInput to respond to clicks anywhere on the screen.
     */
    public TextInput registerGlobalClickListener(GelatinUIScreen screen) {
        screen.addGlobalClickListener((mouseX, mouseY, button) -> {
            java.awt.geom.Rectangle2D bounds = getBounds();
            if (bounds != null && bounds.contains(mouseX, mouseY)) {
                setFocused(true);
            } else {
                setFocused(false);
            }
        });
        return this;
    }

    /**
     * Handle character input.
     */
    public void charTyped(char character) {
        if (!focused) return;

        // Filter out control characters
        if (character < 32 || character == 127) return;

        if (text.length() < maxLength && cursorPosition >= 0 && cursorPosition <= text.length()) {
            String newText = text.substring(0, cursorPosition) + character + text.substring(cursorPosition);
            text(newText);
            cursorPosition++;
        }
    }

    /**
     * Handle key press (for backspace, delete, arrow keys, etc.).
     */
    public void keyPressed(int keyCode) {
        if (!focused) return;

        // Minecraft key codes (these are GLFW constants in actual Minecraft)
        // 259 = BACKSPACE, 261 = DELETE, 263 = LEFT, 262 = RIGHT, 268 = HOME, 269 = END
        switch (keyCode) {
            case 259 -> { // BACKSPACE
                if (cursorPosition > 0) {
                    cursorPosition--;
                    String newText = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                    text(newText);
                }
            }
            case 261 -> { // DELETE
                if (cursorPosition < text.length()) {
                    String newText = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                    text(newText);
                }
            }
            case 263 -> { // LEFT
                if (cursorPosition > 0) {
                    cursorPosition--;
                    cursorBlinkTimer = 0f;
                    markDirty(DirtyFlag.CONTENT);
                }
            }
            case 262 -> { // RIGHT
                if (cursorPosition < text.length()) {
                    cursorPosition++;
                    cursorBlinkTimer = 0f;
                    markDirty(DirtyFlag.CONTENT);
                }
            }
            case 268 -> { // HOME
                cursorPosition = 0;
                cursorBlinkTimer = 0f;
                markDirty(DirtyFlag.CONTENT);
            }
            case 269 -> { // END
                cursorPosition = text.length();
                cursorBlinkTimer = 0f;
                markDirty(DirtyFlag.CONTENT);
            }
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (focused) {
            cursorBlinkTimer += deltaTime;
            if (cursorBlinkTimer >= CURSOR_BLINK_INTERVAL * 2) {
                cursorBlinkTimer = 0f;
            }
            // Keep animating to update cursor blink
            markDirty(DirtyFlag.CONTENT);
        }
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        int x = 0;
        int y = 0;
        int w = (int) size.x;
        int h = (int) size.y;

        // Draw background
        context.fill(x, y, x + w, y + h, backgroundColor);

        // Draw border
        int currentBorderColor = focused ? focusedBorderColor : borderColor;
        // Top
        context.fill(x, y, x + w, y + 1, currentBorderColor);
        // Bottom
        context.fill(x, y + h - 1, x + w, y + h, currentBorderColor);
        // Left
        context.fill(x, y, x + 1, y + h, currentBorderColor);
        // Right
        context.fill(x + w - 1, y, x + w, y + h, currentBorderColor);

        // Calculate text positioning
        int fontHeight = context.getFontHeight();
        int availableWidth = w - 2 * paddingX;
        int availableHeight = h - 2 * paddingY;
        int centerX = x + w / 2;
        int textY = y + paddingY + (availableHeight - fontHeight) / 2;

        // Draw text or placeholder
        if (text.isEmpty() && !placeholder.isEmpty()) {
            // Draw placeholder
            int placeholderWidth = context.getStringWidth(placeholder);
            int placeholderX = switch (textAlignment) {
                case LEFT -> x + paddingX;
                case CENTER -> centerX - placeholderWidth / 2;
                case RIGHT -> x + w - paddingX - placeholderWidth;
            };
            context.drawString(placeholder, placeholderX, textY, placeholderColor);
        } else if (!text.isEmpty()) {
            // Draw actual text
            int textWidth = context.getStringWidth(text);
            int textX = switch (textAlignment) {
                case LEFT -> x + paddingX;
                case CENTER -> centerX - textWidth / 2;
                case RIGHT -> x + w - paddingX - textWidth;
            };
            context.drawString(text, textX, textY, textColor);

            // Draw cursor if focused and in visible blink phase
            if (focused && cursorBlinkTimer < CURSOR_BLINK_INTERVAL) {
                String textBeforeCursor = text.substring(0, cursorPosition);
                int cursorX = textX + context.getStringWidth(textBeforeCursor);
                int cursorY = textY;
                context.fill(cursorX, cursorY, cursorX + 1, cursorY + fontHeight, textColor);
            }
        } else if (focused && cursorBlinkTimer < CURSOR_BLINK_INTERVAL) {
            // Draw cursor when text is empty, positioned based on alignment
            int cursorX = switch (textAlignment) {
                case LEFT -> x + paddingX;
                case CENTER -> centerX;
                case RIGHT -> x + w - paddingX;
            };
            context.fill(cursorX, textY, cursorX + 1, textY + fontHeight, textColor);
        }
    }

    @Override
    protected boolean onEvent(UIEvent event) {
        switch (event.getType()) {
            case CLICK -> {
                int mx = event.getMouseX();
                int my = event.getMouseY();
                java.awt.geom.Rectangle2D bounds = getBounds();
                if (bounds != null && bounds.contains(mx, my)) {
                    event.consume();
                    return true;
                }
                // Focus management is now handled by global click listener
            }
            case HOVER_ENTER -> {
                markDirty(DirtyFlag.CONTENT);
            }
            case HOVER_EXIT -> {
                markDirty(DirtyFlag.CONTENT);
            }
        }
        return false;
    }

    @Override
    protected String getDefaultDebugName() {
        return "TextInput(\"" + text + "\")";
    }

    @Override
    protected TextInput self() {
        return this;
    }
}
