package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * A simple colored rectangle component.
 * Can be used for backgrounds, borders, or simple geometric shapes.
 */
public class Rectangle extends UIElement<Rectangle> {
    private int color;

    public Rectangle(float width, float height, int color) {
        this.size.set(width, height);
        this.color = color;
    }

    public Rectangle(Vector2f size, int color) {
        this.size.set(size);
        this.color = color;
    }

    public Rectangle color(int color) {
        if (this.color != color) {
            this.color = color;
            markDirty(DirtyFlag.CONTENT);
        }
        return this;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Rectangles are typically static
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        int x1 = (int) position.x;
        int y1 = (int) position.y;
        int x2 = (int) (position.x + size.x);
        int y2 = (int) (position.y + size.y);

        context.fill(x1, y1, x2, y2, color);
    }

    @Override
    protected Rectangle self() {
        return this;
    }

    public int getColor() {
        return color;
    }
}

