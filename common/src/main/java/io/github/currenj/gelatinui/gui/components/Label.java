package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import org.joml.Vector2f;

import java.util.List;

/**
 * A simple text label component.
 * Renders text with configurable color and automatically sizes to text dimensions.
 */
public class Label extends UIElement<Label> {
    // Vanilla's bitmap font glyphs sit flush to the top of the 9px line-height box (ascent 7,
    // glyph height 8), leaving ~1px empty above the ink and ~2px below within that box. Since
    // Label reports the full 9px box as its size, HBox/VBox center that box exactly — but the
    // ink inside it isn't centered, so text reads as shifted slightly toward the top next to
    // siblings whose reported size matches their visual bounds (e.g. status pip icons). Nudge
    // rendering down by this many local pixels to compensate.
    private static final int VERTICAL_INK_OFFSET = 1;

    private String text;
    private int color;
    private boolean centered;

    // Wrapping support — when maxWidth > 0, text word-wraps to fit within this pixel width
    private float maxWidth = 0f;
    // Extra pixels between wrapped lines (added to font height per line gap)
    private float lineSpacing = 0f;
    // Cached wrapped lines populated by updateSize when wrapping is active; null otherwise
    private List<String> wrappedLines;

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

    /**
     * Set a maximum pixel width for this label. When the text exceeds this width,
     * it will word-wrap across multiple lines. Set to 0 or negative to disable
     * wrapping (default).
     */
    public Label maxWidth(float maxWidth) {
        if (this.maxWidth != maxWidth) {
            this.maxWidth = maxWidth;
            markDirty(DirtyFlag.CONTENT, DirtyFlag.SIZE);
        }
        return this;
    }

    /**
     * Set extra pixels between wrapped lines. Only meaningful when maxWidth is set.
     * Default is 0 (standard font line height with no extra gap).
     */
    public Label lineSpacing(float lineSpacing) {
        if (this.lineSpacing != lineSpacing) {
            this.lineSpacing = lineSpacing;
            markDirty(DirtyFlag.CONTENT, DirtyFlag.SIZE);
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
        if (context instanceof MinecraftRenderContext mcContext) {
            if (wrappedLines != null && !wrappedLines.isEmpty()) {
                // Multi-line wrapped rendering
                int fontHeight = context.getFontHeight();
                for (int i = 0; i < wrappedLines.size(); i++) {
                    String line = wrappedLines.get(i);
                    int yOffset = (int) (i * (fontHeight + lineSpacing)) + VERTICAL_INK_OFFSET;
                    if (centered) {
                        mcContext.drawCenteredString(line, (int) (baseWidth / 2f), yOffset, color);
                    } else {
                        mcContext.drawString(line, 0, yOffset, color);
                    }
                }
            } else {
                // Single-line rendering
                if (centered) {
                    mcContext.drawCenteredString(text, (int) (baseWidth / 2f), VERTICAL_INK_OFFSET, color);
                } else {
                    mcContext.drawString(text, 0, VERTICAL_INK_OFFSET, color);
                }
            }
        } else {
            // Fallback: draw using global positions and scaled size
            int x = (int) position.x;
            int y = (int) position.y;
            if (wrappedLines != null && !wrappedLines.isEmpty()) {
                // Multi-line wrapped rendering (fallback)
                int fontHeight = context.getFontHeight();
                for (int i = 0; i < wrappedLines.size(); i++) {
                    String line = wrappedLines.get(i);
                    int yOffset = (int) (i * (fontHeight + lineSpacing));
                    if (centered) {
                        context.drawCenteredString(line, x + (int) size.x / 2, y + yOffset, color);
                    } else {
                        context.drawString(line, x, y + yOffset, color);
                    }
                }
            } else {
                // Single-line rendering (fallback)
                if (centered) {
                    context.drawCenteredString(text, x + (int) size.x / 2, y, color);
                } else {
                    context.drawString(text, x, y, color);
                }
            }
        }
    }

    /**
     * Measure and store base size using the provided context (unscaled size).
     * When maxWidth is set, text is word-wrapped and the label's height expands
     * to accommodate all lines.
     */
    public void updateSize(IRenderContext context) {
        if (text != null && !text.isEmpty()) {
            if (maxWidth > 0) {
                // Word-wrap mode: split text into lines that fit within maxWidth
                this.wrappedLines = context.wrapText(text, (int) maxWidth);
                this.baseWidth = maxWidth;
                int fontHeight = context.getFontHeight();
                float totalHeight = wrappedLines.size() * fontHeight
                    + Math.max(0, wrappedLines.size() - 1) * lineSpacing;
                this.baseHeight = totalHeight;
                setSize(new Vector2f(baseWidth, baseHeight));
            } else {
                // Single-line mode (default)
                this.wrappedLines = null;
                this.baseWidth = context.getStringWidth(text);
                this.baseHeight = context.getFontHeight();
                setSize(new Vector2f(baseWidth, baseHeight));
            }
        } else {
            this.wrappedLines = null;
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

    public float getMaxWidth() {
        return maxWidth;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    /**
     * Get the wrapped lines if wrapping is active, or null if not wrapping.
     */
    public List<String> getWrappedLines() {
        return wrappedLines;
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
