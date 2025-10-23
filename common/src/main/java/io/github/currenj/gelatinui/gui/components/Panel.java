package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIContainer;

/**
 * A generic panel container with optional background.
 * Can be used as a simple grouping container or with visual styling.
 */
public class Panel extends UIContainer<Panel> {
    private int backgroundColor = 0x00000000; // Transparent by default
    private boolean drawBackground = false;

    public Panel() {
    }

    public Panel backgroundColor(int color) {
        this.backgroundColor = color;
        this.drawBackground = true;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    public Panel drawBackground(boolean draw) {
        this.drawBackground = draw;
        markDirty(DirtyFlag.CONTENT);
        return this;
    }

    @Override
    protected void performLayout() {
        // Panel uses default bounding box layout (no special positioning)
        // Children maintain their own positions
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // No special update logic for basic panel
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (drawBackground) {
            int x1 = (int) position.x;
            int y1 = (int) position.y;
            int x2 = (int) (position.x + size.x);
            int y2 = (int) (position.y + size.y);

            context.fill(x1, y1, x2, y2, backgroundColor);
        }
    }

    @Override
    protected Panel self() {
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isDrawingBackground() {
        return drawBackground;
    }
}
