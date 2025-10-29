package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.UIEvent;
import net.minecraft.resources.ResourceLocation;

/**
 * A checkbox component with a toggleable checked state.
 * Supports custom textures for checked/unchecked states or renders with simple filled rectangles.
 */
public class Checkbox extends UIElement<Checkbox> {
    private boolean checked = false;
    private int boxColor = 0xFF404040;
    private int checkColor = 0xFFFFFFFF;
    private int borderColor = 0xFF808080;
    private int hoverBorderColor = 0xFFFFFFFF;
    private boolean hovered = false;

    // Optional label next to checkbox
    private String label = null;
    private int labelColor = 0xFFFFFFFF;
    private int labelSpacing = 6;

    // Optional textures for custom appearance
    private ResourceLocation uncheckedTexture = null;
    private ResourceLocation checkedTexture = null;
    private ResourceLocation uncheckedHoverTexture = null;
    private ResourceLocation checkedHoverTexture = null;

    // Callback for state changes
    private CheckChangeListener onCheckChange = null;

    /**
     * Functional interface for check state change callbacks.
     */
    @FunctionalInterface
    public interface CheckChangeListener {
        void onCheckChange(boolean checked);
    }

    /**
     * Create a checkbox with default size (16x16).
     */
    public Checkbox() {
        this(16, 16);
    }

    /**
     * Create a checkbox with specified size.
     */
    public Checkbox(float size) {
        this(size, size);
    }

    /**
     * Create a checkbox with specified width and height.
     */
    public Checkbox(float width, float height) {
        this.size.set(width, height);
        markDirty(DirtyFlag.CONTENT);
    }

    /**
     * Set the checked state.
     */
    public Checkbox checked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            markDirty(DirtyFlag.CONTENT);
            if (onCheckChange != null) {
                onCheckChange.onCheckChange(checked);
            }
        }
        return this;
    }

    /**
     * Toggle the checked state.
     */
    public Checkbox toggle() {
        return checked(!checked);
    }

    /**
     * Get the checked state.
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Set the box background color.
     */
    public Checkbox boxColor(int color) {
        if (this.boxColor != color) {
            this.boxColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set the checkmark color.
     */
    public Checkbox checkColor(int color) {
        if (this.checkColor != color) {
            this.checkColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set the border color.
     */
    public Checkbox borderColor(int color) {
        if (this.borderColor != color) {
            this.borderColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set the hover border color.
     */
    public Checkbox hoverBorderColor(int color) {
        if (this.hoverBorderColor != color) {
            this.hoverBorderColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set a text label to display next to the checkbox.
     */
    public Checkbox label(String label) {
        if (label == null) label = "";
        if (!label.equals(this.label)) {
            this.label = label.isEmpty() ? null : label;
            markDirty(DirtyFlag.CONTENT, DirtyFlag.SIZE);
        }
        return this;
    }

    /**
     * Set the label text color.
     */
    public Checkbox labelColor(int color) {
        if (this.labelColor != color) {
            this.labelColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Set the spacing between checkbox and label.
     */
    public Checkbox labelSpacing(int spacing) {
        if (this.labelSpacing != spacing) {
            this.labelSpacing = spacing;
            markDirty(DirtyFlag.CONTENT, DirtyFlag.SIZE);
        }
        return this;
    }

    /**
     * Set custom texture for unchecked state.
     */
    public Checkbox uncheckedTexture(ResourceLocation texture) {
        this.uncheckedTexture = texture;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    /**
     * Set custom texture for checked state.
     */
    public Checkbox checkedTexture(ResourceLocation texture) {
        this.checkedTexture = texture;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    /**
     * Set custom texture for unchecked hover state.
     */
    public Checkbox uncheckedHoverTexture(ResourceLocation texture) {
        this.uncheckedHoverTexture = texture;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    /**
     * Set custom texture for checked hover state.
     */
    public Checkbox checkedHoverTexture(ResourceLocation texture) {
        this.checkedHoverTexture = texture;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    /**
     * Set check change listener.
     */
    public Checkbox onCheckChange(CheckChangeListener listener) {
        this.onCheckChange = listener;
        return this;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // No continuous updates needed for checkbox
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        int boxSize = (int) Math.min(size.x, size.y);
        int x = 0;
        int y = 0;

        // Determine which texture to use (if any)
        ResourceLocation texture;
        if (checked) {
            texture = hovered && checkedHoverTexture != null ? checkedHoverTexture : checkedTexture;
        } else {
            texture = hovered && uncheckedHoverTexture != null ? uncheckedHoverTexture : uncheckedTexture;
        }

        // Draw checkbox box
        if (texture != null) {
            // Use custom texture
            context.enableBlend();
            context.drawTexture(texture, x, y, boxSize, boxSize);
            context.disableBlend();
        } else {
            // Draw default checkbox appearance
            // Background
            context.fill(x, y, x + boxSize, y + boxSize, boxColor);

            // Border
            int currentBorderColor = hovered ? hoverBorderColor : borderColor;
            // Top
            context.fill(x, y, x + boxSize, y + 1, currentBorderColor);
            // Bottom
            context.fill(x, y + boxSize - 1, x + boxSize, y + boxSize, currentBorderColor);
            // Left
            context.fill(x, y, x + 1, y + boxSize, currentBorderColor);
            // Right
            context.fill(x + boxSize - 1, y, x + boxSize, y + boxSize, currentBorderColor);

            // Draw checkmark if checked
            if (checked) {
                int padding = boxSize / 4;
                int checkX1 = x + padding;
                int checkY1 = y + boxSize / 2;
                int checkX2 = x + boxSize / 2;
                int checkY2 = y + boxSize - padding;
                int checkX3 = x + boxSize - padding;
                int checkY3 = y + padding;

                // Draw checkmark as two lines forming a check
                // Line 1: bottom-left to middle
                drawThickLine(context, checkX1, checkY1, checkX2, checkY2, checkColor);
                // Line 2: middle to top-right
                drawThickLine(context, checkX2, checkY2, checkX3, checkY3, checkColor);
            }
        }

        // Draw label if present
        if (label != null && !label.isEmpty()) {
            int labelX = boxSize + labelSpacing;
            int labelY = (boxSize - context.getFontHeight()) / 2;
            context.drawString(label, labelX, labelY, labelColor);
        }
    }

    /**
     * Draw a thick line (2px wide) between two points.
     */
    private void drawThickLine(IRenderContext context, int x1, int y1, int x2, int y2, int color) {
        // Simple thick line implementation using rectangles
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        if (steps == 0) {
            context.fill(x1, y1, x1 + 2, y1 + 2, color);
            return;
        }

        float xStep = (float) dx / steps;
        float yStep = (float) dy / steps;

        for (int i = 0; i <= steps; i++) {
            int x = x1 + (int) (xStep * i);
            int y = y1 + (int) (yStep * i);
            context.fill(x, y, x + 2, y + 2, color);
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
                    toggle();
                    playClickBounce();
                    event.consume();
                    return true;
                }
            }
            case HOVER_ENTER -> {
                this.hovered = true;
                markDirty(DirtyFlag.CONTENT);
                return false;
            }
            case HOVER_EXIT -> {
                this.hovered = false;
                markDirty(DirtyFlag.CONTENT);
                return false;
            }
        }
        return false;
    }

    @Override
    protected String getDefaultDebugName() {
        String labelText = label != null ? " \"" + label + "\"" : "";
        return "Checkbox(" + (checked ? "checked" : "unchecked") + labelText + ")";
    }

    @Override
    protected Checkbox self() {
        return this;
    }
}
