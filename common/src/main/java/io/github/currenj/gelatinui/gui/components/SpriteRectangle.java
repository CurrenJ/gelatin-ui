package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

/**
 * A rectangle that can draw a texture (sprite) or fallback to a solid color.
 * Also supports optional centered text and simple hover/pressed textures.
 */
public abstract class SpriteRectangle<T extends SpriteRectangle<T>> extends UIElement<T> {
    private int color = 0xFFFFFFFF;
    // Primary sprite data (may be null)
    private SpriteData sprite = null;
    private SpriteData hoverSprite = null;
    private SpriteData pressedSprite = null;

    private String text = null;
    private int textColor = 0xFFFFFFFF;

    private boolean autoSize = false;
    private float paddingX = 0f;
    private float paddingY = 0f;
    private boolean textChanged = false;

    private boolean outline = false;
    private int outlineColor1 = -1;
    private int outlineColor2 = -1;

    protected boolean hovered = false;
    protected boolean pressed = false;

    public SpriteRectangle(float width, float height, int color) {
        this.size.set(width, height);
        this.color = color;
    }

    public SpriteRectangle(float width, float height, ResourceLocation texture) {
        this.size.set(width, height);
        this.sprite = new SpriteData(texture);
    }

    public SpriteRectangle(Vector2f size, int color) {
        this.size.set(size);
        this.color = color;
    }

    /**
     * Primary sprite setter using the SpriteData record.
     */
    public T texture(SpriteData data) {
        this.sprite = data;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T hoverTexture(SpriteData data) {
        this.hoverSprite = data;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T pressedTexture(SpriteData data) {
        this.pressedSprite = data;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T uv(int u, int v) {
        if (this.sprite != null) {
            this.sprite = this.sprite.uv(u, v);
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T region(int regionW, int regionH) {
        if (this.sprite != null) {
            this.sprite = this.sprite.uv(sprite.u(), sprite.v(), regionW, regionH);
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T actualSize(int actualW, int actualH) {
        if (this.sprite != null) {
            this.sprite = this.sprite.actualSize(actualW, actualH);
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T atlasSize(int atlasW, int atlasH) {
        if (this.sprite != null) {
            this.sprite = this.sprite.textureSize(atlasW, atlasH);
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T color(int color) {
        if (this.color != color) {
            this.color = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T text(String text, int color) {
        if (this.text == null || !this.text.equals(text) || this.textColor != color) {
            this.text = text;
            this.textColor = color;
            this.textChanged = true;
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    /**
     * Get the current text content.
     */
    public String getText() {
        return text;
    }

    /**
     * Enable or disable auto-sizing to fit the text.
     */
    public T autoSize(boolean autoSize) {
        this.autoSize = autoSize;
        if (autoSize && text != null) {
            textChanged = true;
        }
        return self();
    }

    /**
     * Set padding for auto-sizing.
     */
    public T padding(float x, float y) {
        this.paddingX = x;
        this.paddingY = y;
        if (autoSize && text != null) {
            textChanged = true;
        }
        return self();
    }

    public T outline(boolean enable) {
        this.outline = enable;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T outlineColors(int color1, int color2) {
        this.outlineColor1 = color1;
        this.outlineColor2 = color2;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    private int darken(int c) {
        int a = c & 0xFF000000;
        int r = ((c >> 16) & 0xFF) / 2;
        int g = ((c >> 8) & 0xFF) / 2;
        int b = (c & 0xFF) / 2;
        return a | (r << 16) | (g << 8) | b;
    }

    private int lighten(int c) {
        int a = c & 0xFF000000;
        int r = Math.min(255, ((c >> 16) & 0xFF) * 3 / 2);
        int g = Math.min(255, ((c >> 8) & 0xFF) * 3 / 2);
        int b = Math.min(255, (c & 0xFF) * 3 / 2);
        return a | (r << 16) | (g << 8) | b;
    }

    /**
     * Protected helper for subclasses to set pressed state.
     */
    protected void setPressed(boolean pressed) {
        if (this.pressed != pressed) {
            this.pressed = pressed;
            markDirty(DirtyFlag.CONTENT);
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Nothing by default
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        // Handle auto-sizing if enabled and text has changed
        if (autoSize && text != null && textChanged) {
            int textWidth = context.getStringWidth(text);
            int textHeight = context.getFontHeight();
            float newWidth = textWidth + paddingX * 2;
            float newHeight = textHeight + paddingY * 2;
            setSize(new Vector2f(newWidth, newHeight));
            textChanged = false;
        }

        int x1 = 0;
        int y1 = 0;
        int w = (int) Math.ceil(size.x);
        int h = (int) Math.ceil(size.y);

        if (outline) {
            int c1 = outlineColor1 == -1 ? darken(color) : outlineColor1;
            int c2 = outlineColor2 == -1 ? lighten(color) : outlineColor2;
            // outer border
            context.fill(-1, -1, w + 1, 0, c1); // top
            context.fill(-1, h, w + 1, h + 1, c1); // bottom
            context.fill(-1, 0, 0, h, c1); // left
            context.fill(w, 0, w + 1, h, c1); // right
            // inner border
            context.fill(0, 0, w, 1, c2); // top
            context.fill(0, h - 1, w, h, c2); // bottom
            context.fill(0, 1, 1, h - 1, c2); // left
            context.fill(w - 1, 1, w, h - 1, c2); // right
        }

        // Select sprite based on state
        SpriteData chosen = null;
        if (pressed && pressedSprite != null) {
            chosen = pressedSprite;
        } else if (hovered && hoverSprite != null) {
            chosen = hoverSprite;
        } else if (sprite != null) {
            chosen = sprite;
        }

        // Render sprite using the unified drawSprite method that handles all modes
        if (chosen != null && chosen.texture() != null) {
            context.enableBlend();
            context.drawSprite(chosen, x1, y1, w, h);
            context.disableBlend();
        } else {
            // Fall back to solid color
            context.fill(x1, y1, x1 + w, y1 + h, color);
        }

        if (text != null && !text.isEmpty()) {
            int fontHeight = context.getFontHeight();
            int textX = (int) (size.x / 2f);
            int textY = (int) ((size.y - fontHeight) / 2f);
            context.drawCenteredString(text, textX, textY, textColor);
        }
    }

    @Override
    protected boolean onEvent(io.github.currenj.gelatinui.gui.UIEvent event) {
        return super.onEvent(event);
    }

    public static class SpriteRectangleImpl extends SpriteRectangle<SpriteRectangleImpl> {
        public SpriteRectangleImpl(float width, float height, int color) {
            super(width, height, color);
        }

        public SpriteRectangleImpl(float width, float height, ResourceLocation texture) {
            super(width, height, texture);
        }

        public SpriteRectangleImpl(Vector2f size, int color) {
            super(size, color);
        }

        @Override
        protected SpriteRectangleImpl self() {
            return this;
        }
    }
}
