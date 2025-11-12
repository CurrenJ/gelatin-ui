package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.IUIElement;
import io.github.currenj.gelatinui.gui.UIContainer;
import net.minecraft.resources.ResourceLocation;

import java.awt.geom.Rectangle2D;

/**
 * Abstract base class for panels with background support.
 * Provides background color and sprite functionality.
 * Subclasses must implement the generic self() method for fluent API.
 */
public abstract class PanelBase<T extends PanelBase<T>> extends UIContainer<T> {
    private int backgroundColor = 0x00000000; // Transparent by default
    private boolean drawBackground = false;
    private SpriteData backgroundSprite = null;
    private boolean autoSizeToChildren = false;
    private boolean centerBackgroundSprite = false; // When true, draw sprite at actual size centered in container

    public PanelBase() {
    }

    public T backgroundColor(int color) {
        this.backgroundColor = color;
        this.drawBackground = true;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    public T drawBackground(boolean draw) {
        this.drawBackground = draw;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /**
     * Set a sprite as the background.
     * The sprite's render mode (STRETCH, REPEAT, or SLICE) determines how it's rendered.
     */
    public T backgroundSprite(SpriteData sprite) {
        this.backgroundSprite = sprite;
        this.drawBackground = true;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    /**
     * Set whether the panel should automatically size itself to fit its children.
     * If true, the panel will adjust its size based on the combined size of its children.
     */
    public T autoSizeToChildren(boolean autoSize) {
        this.autoSizeToChildren = autoSize;
        markDirty(DirtyFlag.SIZE);
        return self();
    }

    /**
     * Set whether the background sprite should be drawn at its actual size and centered in the container.
     * When true, the sprite will be drawn at the size specified in its sprite data (actualW x actualH or regionW x regionH)
     * and positioned in the center of the panel, rather than being stretched to fit the panel's dimensions.
     * When false (default), the sprite is stretched to fill the entire panel.
     */
    public T centerBackgroundSprite(boolean center) {
        this.centerBackgroundSprite = center;
        markDirty(DirtyFlag.CONTENT);
        return self();
    }

    @Override
    protected void performLayout() {
        // Panel uses default bounding box layout (no special positioning)
        // Children maintain their own positions

        // If autoSizeToChildren is enabled, calculate the panel size based on children
        if (autoSizeToChildren) {
            if (children.isEmpty()) {
                // If no children, keep current size or set to minimum
                setSize(Math.max(1, size.x), Math.max(1, size.y));
                return;
            }

            // Calculate bounding box of all children relative to panel position
            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            for (IUIElement child : children) {
                if (child.isVisible()) {
                    Rectangle2D childBounds = child.getBounds();
                    minX = Math.min(minX, (float) childBounds.getMinX());
                    minY = Math.min(minY, (float) childBounds.getMinY());
                    maxX = Math.max(maxX, (float) childBounds.getMaxX());
                    maxY = Math.max(maxY, (float) childBounds.getMaxY());
                }
            }

            // Set the panel size to encompass all children
            float newWidth = maxX - minX;
            float newHeight = maxY - minY;
            setSize(Math.max(1, newWidth), Math.max(1, newHeight));
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // No special update logic for basic panel
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (drawBackground) {
            float w = size.x;
            float h = size.y;

            // Render sprite if available using unified drawSprite method
            if (backgroundSprite != null && backgroundSprite.texture() != null) {
                // Apply animation effects to ITEM mode sprites
                SpriteData spriteToRender = backgroundSprite;
                if (backgroundSprite.renderMode() == SpriteRenderMode.ITEM) {
                    // Get the current effect delta rotation
                    org.joml.Vector3f effectRotation = combinedEffectDelta.getRotation3D();
                    
                    // Add effect rotation to sprite's base rotation
                    org.joml.Vector3f currentRotation = backgroundSprite.itemRotation();
                    org.joml.Vector3f combinedRotation = new org.joml.Vector3f(
                        currentRotation.x + effectRotation.x,
                        currentRotation.y + effectRotation.y,
                        currentRotation.z + effectRotation.z
                    );
                    
                    // Create a new sprite with the combined rotation
                    spriteToRender = backgroundSprite.itemRotation(
                        combinedRotation.x, 
                        combinedRotation.y, 
                        combinedRotation.z
                    );
                }
                
                if (centerBackgroundSprite) {
                    // Draw sprite at its actual size, centered in the container
                    // Use actualW/actualH if set, otherwise use regionW/regionH
                    int spriteW = spriteToRender.actualW() > 0 ? spriteToRender.actualW() : spriteToRender.regionW();
                    int spriteH = spriteToRender.actualH() > 0 ? spriteToRender.actualH() : spriteToRender.regionH();
                    
                    // Calculate centered position
                    float x = (w - spriteW) / 2f;
                    float y = (h - spriteH) / 2f;
                    
                    context.enableBlend();
                    context.drawSprite(spriteToRender, x, y, spriteW, spriteH);
                    context.disableBlend();
                } else {
                    // Default behavior: stretch sprite to match panel's size
                    int wi = (int) Math.ceil(w);
                    int hi = (int) Math.ceil(h);
                    SpriteData sizedSprite = spriteToRender.actualSize(wi, hi);
                    context.enableBlend();
                    context.drawSprite(sizedSprite, 0, 0, wi, hi);
                    context.disableBlend();
                }
            }
            // Fall back to solid color
            else {
                int wi = (int) Math.ceil(w);
                int hi = (int) Math.ceil(h);
                context.fill(0, 0, wi, hi, backgroundColor);
            }
        }
    }

    protected abstract T self();

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isDrawingBackground() {
        return drawBackground;
    }

    public SpriteData getBackgroundSprite() {
        return backgroundSprite;
    }

    public boolean isAutoSizeToChildren() {
        return autoSizeToChildren;
    }

    public boolean isCenterBackgroundSprite() {
        return centerBackgroundSprite;
    }
}
