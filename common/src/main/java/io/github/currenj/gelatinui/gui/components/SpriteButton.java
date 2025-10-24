package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.UIEvent;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

/**
 * A clickable button based on SpriteRectangle with hover and pressed visual states.
 * Provides a convenience onClick callback.
 */
public class SpriteButton extends SpriteRectangle<SpriteButton> {
    // pressed visual timer in seconds
    private float pressedTimer = 0f;
    private static final float PRESSED_DISPLAY_TIME = 0.12f;

    public SpriteButton(float width, float height, int color) {
        super(width, height, color);
    }

    public SpriteButton(float width, float height, ResourceLocation texture) {
        super(width, height, texture);
    }

    public SpriteButton(Vector2f size, int color) {
        super(size, color);
    }

    // Provide fluent overrides so chaining from SpriteRectangle returns SpriteButton
    @Override
    public SpriteButton text(String text, int color) {
        super.text(text, color);
        return this;
    }

    @Override
    public SpriteButton color(int color) {
        super.color(color);
        return this;
    }

    @Override
    public SpriteButton texture(ResourceLocation texture) {
        super.texture(texture);
        return this;
    }

    @Override
    public SpriteButton texture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        super.texture(texture, u, v, texWidth, texHeight);
        return this;
    }

    @Override
    public SpriteButton hoverTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        super.hoverTexture(texture, u, v, texWidth, texHeight);
        return this;
    }

    @Override
    public SpriteButton pressedTexture(ResourceLocation texture, int u, int v, int texWidth, int texHeight) {
        super.pressedTexture(texture, u, v, texWidth, texHeight);
        return this;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (pressedTimer > 0f) {
            pressedTimer -= deltaTime;
            if (pressedTimer <= 0f) {
                setPressed(false);
                pressedTimer = 0f;
            }
        }
    }

    @Override
    protected boolean onEvent(io.github.currenj.gelatinui.gui.UIEvent event) {
        // Let base class handle hover enter/exit so hovered state is updated
        if (event.getType() == UIEvent.Type.HOVER_ENTER || event.getType() == UIEvent.Type.HOVER_EXIT) {
            return super.onEvent(event);
        }

        if (event.getType() == UIEvent.Type.CLICK) {
            // Ensure click is inside this element's global bounds before activating
            int mx = event.getMouseX();
            int my = event.getMouseY();
            java.awt.geom.Rectangle2D bounds = getBounds();
            if (bounds == null || !bounds.contains(mx, my)) {
                // Not inside this button - don't consume, allow other handlers
                return false;
            }

            // Trigger pressed visual briefly and play bounce animation
            setPressed(true);
            this.pressedTimer = PRESSED_DISPLAY_TIME;
            // New: bounce click feedback
            this.playClickBounce();

            // Now call any registered click handlers
            super.onEvent(event);

            // Consume the click so parents don't also trigger
            event.consume();
            return true;
        }

        return false;
    }

    @Override
    protected String getDefaultDebugName() {
        String textContent = getText() != null && !getText().isEmpty() ? "\"" + getText().replace("\"", "\\\"") + "\"" : "empty";
        return "SpriteButton(" + textContent + ")";
    }

    @Override
    protected SpriteButton self() {
        return this;
    }
}
