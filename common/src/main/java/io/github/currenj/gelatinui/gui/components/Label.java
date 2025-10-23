package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import org.joml.Vector2f;

/**
 * A simple text label component.
 * Renders text with configurable color and automatically sizes to text dimensions.
 */
public class Label extends UIElement<Label> {
    private String text;
    private int color;
    private boolean centered;

    // Measured base size (unscaled) obtained via updateSize(context)
    private float baseWidth = 0f;
    private float baseHeight = 0f;
    private boolean needsSizeUpdate = true;

    public Label(String text) {
        this(text, 0xFFFFFFFF, false);
    }

    public Label(String text, int color) {
        this(text, color, false);
    }

    public Label(String text, int color, boolean centered) {
        this.text = text;
        this.color = color;
        this.centered = centered;

        markDirty(DirtyFlag.CONTENT, DirtyFlag.SIZE);
    }

    public Label text(String text) {
        if (this.text == null || !this.text.equals(text)) {
            this.text = text;
            markDirty(DirtyFlag.CONTENT, DirtyFlag.SIZE);
        }
        return this;
    }

    public Label color(int color) {
        if (this.color != color) {
            this.color = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    public Label centered(boolean centered) {
        if (this.centered != centered) {
            this.centered = centered;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    public Label init(IRenderContext renderContext)
    {
        updateSize(renderContext);
        return this;
    }

    /**
     * Delegate scale to base UIElement animation system
     */
    public Label scale(float scale) {
        super.scale(scale);
        return this;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Label-specific update is minimal; size changes are handled by updateSize from the screen before layout.
    }

    @Override
    protected void onSizeChanged() {
        // When scale changes, it triggers a SIZE dirty flag.
        // The size field should hold the BASE size (unscaled) so that calculateBounds() can apply scale correctly.
        // Do nothing here - size is already set to base dimensions by updateSize().
        needsSizeUpdate = true;
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (text == null || text.isEmpty()) {
            return;
        }

        if (needsSizeUpdate) {
            // Text size can be retrieved through font in the context, so we need to do this in the render pass when context is available.
            // Probably should be done in update pass. When we change text, we mark SIZE dirty, which triggers onSizeChanged and marks needsSizeUpdate.
            // Without this, changing text at runtime would not update size/bounds.
            updateSize(context);
            needsSizeUpdate = false;
        }

        // When UIElement.render has applied translation and scaling (Minecraft path), we should draw at origin.
        if (context instanceof MinecraftRenderContext) {
            MinecraftRenderContext mcContext = (MinecraftRenderContext) context;

            if (centered) {
                mcContext.drawCenteredString(text, (int) (baseWidth / 2f), 0, color);
            } else {
                mcContext.drawString(text, 0, 0, color);
            }
        } else {
            // Fallback: draw using global positions and scaled size
            int x = (int) position.x;
            int y = (int) position.y;
            if (centered) {
                context.drawCenteredString(text, x + (int) size.x / 2, y, color);
            } else {
                context.drawString(text, x, y, color);
            }
        }
    }

    /**
     * Measure and store base size using the provided context (unscaled size).
     */
    public void updateSize(IRenderContext context) {
        if (text != null && !text.isEmpty()) {
            this.baseWidth = context.getStringWidth(text);
            this.baseHeight = context.getFontHeight();
            // Store BASE (unscaled) size - rendering transform and bounds calculation will apply scale
            setSize(new Vector2f(baseWidth, baseHeight));
        } else {
            this.baseWidth = 0f;
            this.baseHeight = 0f;
            setSize(new Vector2f(0, 0));
        }
    }

    public String getText() {
        return text;
    }

    public int getColor() {
        return color;
    }

    public boolean isCentered() {
        return centered;
    }


    public float getCurrentScale() {
        return currentScale;
    }

    @Override
    protected String getDefaultDebugName() {
        String textContent = text != null && !text.isEmpty() ? "\"" + text.replace("\"", "\\\"") + "\"" : "empty";
        return "Label(" + textContent + ")";
    }

    @Override
    protected Label self() {
        return this;
    }
}
