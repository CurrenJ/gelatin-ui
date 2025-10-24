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

    // Convenience: set texture with no region info
    public SpriteRectangle texture(ResourceLocation texture) {
        SpriteData d = new SpriteData(texture);
        return texture(d);
    }

    /**
     * Set texture with explicit source UV/size in texture pixels.
     */
    public SpriteRectangle texture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight);
        return texture(d);
    }

    /**
     * Set texture with explicit source UV/size in texture pixels and the actual content size
     * inside that source region (useful when the source region includes padding/margins).
     */
    public SpriteRectangle texture(ResourceLocation texture, int u, int v, int texWidth, int texHeight, int actualWidth, int actualHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight, actualWidth, actualHeight);
        return texture(d);
    }

    /**
     * Primary sprite setter using the SpriteData record.
     */
    public T texture(SpriteData data) {
        this.sprite = data;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T hoverTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight);
        return hoverTexture(d);
    }

    public T hoverTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight, int actualWidth, int actualHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight, actualWidth, actualHeight);
        return hoverTexture(d);
    }

    public T hoverTexture(SpriteData data) {
        this.hoverSprite = data;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T pressedTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight);
        return pressedTexture(d);
    }

    public T pressedTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight, int actualWidth, int actualHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight, actualWidth, actualHeight);
        return pressedTexture(d);
    }

    public T pressedTexture(SpriteData data) {
        this.pressedSprite = data;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T uv(int u, int v) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), u, v, sprite.texW(), sprite.texH(), sprite.actualW(), sprite.actualH(), sprite.atlasW(), sprite.atlasH());
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T texSize(int texW, int texH) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), sprite.u(), sprite.v(), texW, texH, sprite.actualW(), sprite.actualH(), sprite.atlasW(), sprite.atlasH());
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T actualSize(int actualW, int actualH) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), sprite.u(), sprite.v(), sprite.texW(), sprite.texH(), actualW, actualH, sprite.atlasW(), sprite.atlasH());
            markDirty(DirtyFlag.CONTENT);
        }
        return self();
    }

    public T atlasSize(int atlasW, int atlasH) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), sprite.u(), sprite.v(), sprite.texW(), sprite.texH(), sprite.actualW(), sprite.actualH(), atlasW, atlasH);
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

        SpriteData chosen = null;
        if (pressed && pressedSprite != null) {
            chosen = pressedSprite;
        } else if (hovered && hoverSprite != null) {
            chosen = hoverSprite;
        } else if (sprite != null) {
            chosen = sprite;
        }

        if (chosen != null && chosen.texture() != null) {
            int du = chosen.u();
            int dv = chosen.v();
            int dw = chosen.texW();
            int dh = chosen.texH();

            // if actual content size is provided, center that inside the full source region
            if (chosen.actualW() > 0 && chosen.actualH() > 0 && dw > 0 && dh > 0) {
                int dx = (dw - chosen.actualW()) / 2;
                int dy = (dh - chosen.actualH()) / 2;
                du += Math.max(0, dx);
                dv += Math.max(0, dy);
                dw = chosen.actualW();
                dh = chosen.actualH();
            }

            context.enableBlend();
            if (dw > 0 && dh > 0) {
                context.drawTexture(chosen.texture(), x1, y1, w, h, du, dv, dw, dh, chosen.atlasW(), chosen.atlasH());
            } else {
                context.drawTexture(chosen.texture(), x1, y1, w, h);
            }
            context.disableBlend();
        } else {
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
