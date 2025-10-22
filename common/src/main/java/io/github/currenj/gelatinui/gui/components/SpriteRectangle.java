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
public class SpriteRectangle extends UIElement {
    private int color = 0xFFFFFFFF;
    // Primary sprite data (may be null)
    private SpriteData sprite = null;
    private SpriteData hoverSprite = null;
    private SpriteData pressedSprite = null;

    private String text = null;
    private int textColor = 0xFFFFFFFF;

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
    public SpriteRectangle texture(SpriteData data) {
        this.sprite = data;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    public SpriteRectangle hoverTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight);
        return hoverTexture(d);
    }

    public SpriteRectangle hoverTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight, int actualWidth, int actualHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight, actualWidth, actualHeight);
        return hoverTexture(d);
    }

    public SpriteRectangle hoverTexture(SpriteData data) {
        this.hoverSprite = data;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    public SpriteRectangle pressedTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight);
        return pressedTexture(d);
    }

    public SpriteRectangle pressedTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight, int actualWidth, int actualHeight) {
        SpriteData d = new SpriteData(texture, u, v, texWidth, texHeight, actualWidth, actualHeight);
        return pressedTexture(d);
    }

    public SpriteRectangle pressedTexture(SpriteData data) {
        this.pressedSprite = data;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    public SpriteRectangle uv(int u, int v) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), u, v, sprite.texW(), sprite.texH(), sprite.actualW(), sprite.actualH(), sprite.atlasW(), sprite.atlasH());
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    public SpriteRectangle texSize(int texW, int texH) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), sprite.u(), sprite.v(), texW, texH, sprite.actualW(), sprite.actualH(), sprite.atlasW(), sprite.atlasH());
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    public SpriteRectangle actualSize(int actualW, int actualH) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), sprite.u(), sprite.v(), sprite.texW(), sprite.texH(), actualW, actualH, sprite.atlasW(), sprite.atlasH());
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    public SpriteRectangle atlasSize(int atlasW, int atlasH) {
        if (this.sprite != null) {
            this.sprite = new SpriteData(sprite.texture(), sprite.u(), sprite.v(), sprite.texW(), sprite.texH(), sprite.actualW(), sprite.actualH(), atlasW, atlasH);
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    public SpriteRectangle color(int color) {
        if (this.color != color) {
            this.color = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    public SpriteRectangle text(String text, int color) {
        if (this.text == null || !this.text.equals(text) || this.textColor != color) {
            this.text = text;
            this.textColor = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    /**
     * Get the current text content.
     */
    public String getText() {
        return text;
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
        int x1 = 0;
        int y1 = 0;
        int w = (int) Math.ceil(size.x);
        int h = (int) Math.ceil(size.y);

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
        switch (event.getType()) {
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
            default -> {
                return false;
            }
        }
    }
}
