package io.github.currenj.gelatinui.gui;

import org.joml.Vector2f;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Base class for container elements that can hold children.
 * Implements efficient child management with layout caching and dirty propagation.
 */
public abstract class UIContainer<T extends UIContainer<T>> extends UIElement<T> {
    // Children collection
    protected List<IUIElement> children = new ArrayList<>();

    // Layout cache
    protected LayoutCache layoutCache = new LayoutCache();

    // Size alignment - restrict width/height to multiples of these values
    // 0 means no alignment restriction (default)
    protected int alignWidthToMultiple = 0;
    protected int alignHeightToMultiple = 0;

    // Alignment offsets - for patterns like offset + multiple * x (e.g., 8 + 7*x)
    protected int alignWidthOffset = 0;
    protected int alignHeightOffset = 0;

    /**
     * Cache for layout calculations to avoid redundant recalculation.
     */
    protected static class LayoutCache {
        Rectangle2D bounds;
        Map<IUIElement, Rectangle2D> childBounds = new HashMap<>();
        long timestamp;
        boolean valid = false;

        void invalidate() {
            valid = false;
            bounds = null;
            childBounds.clear();
        }
    }

    /**
     * Add a child element.
     */
    public void addChild(IUIElement child) {
        if (child == null) {
            throw new IllegalArgumentException("Cannot add null child");
        }

        children.add(child);
        child.setParent(this);
        markDirty(DirtyFlag.CHILDREN, DirtyFlag.LAYOUT);
    }

    /**
     * Remove a child element.
     */
    public void removeChild(IUIElement child) {
        if (children.remove(child)) {
            child.setParent(null);
            markDirty(DirtyFlag.CHILDREN, DirtyFlag.LAYOUT);
        }
    }

    /**
     * Remove all children.
     */
    public void clearChildren() {
        for (IUIElement child : children) {
            child.setParent(null);
        }
        children.clear();
        markDirty(DirtyFlag.CHILDREN, DirtyFlag.LAYOUT);
    }

    /**
     * Get all children.
     */
    public List<IUIElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Get the number of children.
     */
    public int getChildCount() {
        return children.size();
    }

    @Override
    public void update(float deltaTime) {
        if (!needsUpdate()) {
            return; // Skip if container and all children are clean
        }

        // Update self first
        super.update(deltaTime);

        // Update children that need it
        for (IUIElement child : children) {
            if (child.needsUpdate()) {
                child.update(deltaTime);
            }
        }
    }

    @Override
    protected void renderChildren(IRenderContext context, Rectangle2D viewport) {
        for (IUIElement child : children) {
            if (child.isVisible()) {
                child.render(context, viewport);
            }
        }
    }

    @Override
    public boolean handleEvent(UIEvent event) {
        if (!visible) {
            return false;
        }

        // Propagate to children first (front to back)
        for (int i = children.size() - 1; i >= 0; i--) {
            IUIElement child = children.get(i);
            if (child.handleEvent(event)) {
                return true; // Event consumed by child
            }
        }

        // Then try to handle locally
        return super.handleEvent(event);
    }

    @Override
    protected Rectangle2D calculateBounds() {
        if (layoutCache.valid && layoutCache.bounds != null) {
            return layoutCache.bounds;
        }

        if (children.isEmpty()) {
            return new Rectangle2D.Float(position.x, position.y, size.x, size.y);
        }

        // Calculate bounding box of all children
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

        if (minX == Float.MAX_VALUE) {
            // No visible children
            return new Rectangle2D.Float(position.x, position.y, size.x, size.y);
        }

        Rectangle2D bounds = new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
        layoutCache.bounds = bounds;
        layoutCache.valid = true;
        layoutCache.timestamp = System.currentTimeMillis();

        return bounds;
    }

    @Override
    protected void onChildDirty(DirtyFlag... flags) {
        // Invalidate layout cache if needed
        if (shouldInvalidateLayout(flags)) {
            layoutCache.invalidate();
            markDirty(DirtyFlag.LAYOUT);
        }

        // Propagate to parent
        super.onChildDirty(flags);
    }

    /**
     * Check if any child needs updating.
     */
    protected boolean anyChildNeedsUpdate() {
        for (IUIElement child : children) {
            if (child.needsUpdate()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Invalidate bounds of all descendants recursively.
     * Called when this element's transform changes, which affects all child bounds.
     */
    @Override
    protected void invalidateChildBounds() {
        for (IUIElement child : children) {
            if (child instanceof UIElement uiChild) {
                uiChild.boundsValid = false;
                uiChild.cachedBounds = null;
                uiChild.invalidateChildBounds();
            }
        }
    }

    @Override
    protected void onSizeChanged() {
        super.onSizeChanged();
        // Invalidate layout cache on size change
        layoutCache.invalidate();
    }

    /**
     * Determine if layout cache should be invalidated based on dirty flags.
     */
    protected boolean shouldInvalidateLayout(DirtyFlag... flags) {
        for (DirtyFlag flag : flags) {
            switch (flag) {
                case POSITION:
                case SIZE:
                case CHILDREN:
                case LAYOUT:
                case VISIBILITY:
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void recalculateLayout() {
        // Mark layout cache as invalid
        layoutCache.invalidate();

        // Perform layout
        performLayout();
    }

    /**
     * Force a layout pass immediately. Public helper for tests and callers.
     */
    public void forceLayout() {
        recalculateLayout();
    }

    @Override
    public void setPosition(Vector2f position) {
        // Only act if the position actually changed (super handles marking),
        // but ensure layout caches are invalidated so children's global bounds are recomputed.
        boolean changed = !this.position.equals(position);
        super.setPosition(position);
        if (changed) {
            // Invalidate our layout cache since global child bounds used in bounding calculation are no longer valid
            layoutCache.invalidate();

            // Mark children as position-dirty so their cached bounds are recomputed (propagates up as needed)
            for (IUIElement child : children) {
                child.markDirty(DirtyFlag.POSITION);
            }
        }
    }

    @Override
    public Rectangle2D getBounds() {
        // If we or any parent is position-dirty, invalidate cached bounds to prevent stale bounds during culling
        if (dirtyFlags.contains(DirtyFlag.POSITION)) {
            boundsValid = false;
            layoutCache.invalidate();
        }
        return super.getBounds();
    }

    @Override
    public boolean needsUpdate() {
        return super.needsUpdate() || anyChildNeedsUpdate();
    }

    /**
     * Set the width alignment. The container's width will be rounded up to the nearest multiple
     * of this value. Useful for aligning with tiled sprite patterns.
     * Set to 0 to disable width alignment (default).
     *
     * @param multiple The multiple to align width to (e.g., 3 for panel2 which has 3-pixel repeating segments)
     * @return this for method chaining
     */
    public T alignWidthToMultiple(int multiple) {
        return alignWidthToMultiple(multiple, 0);
    }

    /**
     * Set the width alignment with an offset. The container's width will be rounded to the nearest
     * value matching the pattern: offset + multiple * x (e.g., 8 + 7*x).
     * Useful for aligning with tiled sprite patterns that have fixed borders.
     *
     * @param multiple The multiple to align width to
     * @param offset The base offset (y-intercept)
     * @return this for method chaining
     */
    public T alignWidthToMultiple(int multiple, int offset) {
        if (multiple < 0) {
            throw new IllegalArgumentException("Alignment multiple must be >= 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Alignment offset must be >= 0");
        }
        this.alignWidthToMultiple = multiple;
        this.alignWidthOffset = offset;
        // Re-apply current size to trigger alignment
        if (multiple > 0) {
            setSize(size);
        }
        return self();
    }

    /**
     * Set the height alignment. The container's height will be rounded up to the nearest multiple
     * of this value. Useful for aligning with tiled sprite patterns.
     * Set to 0 to disable height alignment (default).
     *
     * @param multiple The multiple to align height to (e.g., 3 for panel2 which has 3-pixel repeating segments)
     * @return this for method chaining
     */
    public T alignHeightToMultiple(int multiple) {
        return alignHeightToMultiple(multiple, 0);
    }

    /**
     * Set the height alignment with an offset. The container's height will be rounded to the nearest
     * value matching the pattern: offset + multiple * x (e.g., 8 + 7*x).
     * Useful for aligning with tiled sprite patterns that have fixed borders.
     *
     * @param multiple The multiple to align height to
     * @param offset The base offset (y-intercept)
     * @return this for method chaining
     */
    public T alignHeightToMultiple(int multiple, int offset) {
        if (multiple < 0) {
            throw new IllegalArgumentException("Alignment multiple must be >= 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Alignment offset must be >= 0");
        }
        this.alignHeightToMultiple = multiple;
        this.alignHeightOffset = offset;
        // Re-apply current size to trigger alignment
        if (multiple > 0) {
            setSize(size);
        }
        return self();
    }

    /**
     * Set both width and height alignment to the same value.
     *
     * @param multiple The multiple to align both dimensions to
     * @return this for method chaining
     */
    public T alignSizeToMultiple(int multiple) {
        return alignSizeToMultiple(multiple, 0);
    }

    /**
     * Set both width and height alignment to the same value with an offset.
     * Both dimensions will be rounded to the pattern: offset + multiple * x.
     *
     * @param multiple The multiple to align both dimensions to
     * @param offset The base offset (y-intercept)
     * @return this for method chaining
     */
    public T alignSizeToMultiple(int multiple, int offset) {
        alignWidthToMultiple(multiple, offset);
        alignHeightToMultiple(multiple, offset);
        return self();
    }

    @Override
    public T setSize(Vector2f size) {
        // Apply alignment if configured
        float alignedWidth = size.x;
        float alignedHeight = size.y;

        if (alignWidthToMultiple > 0) {
            // Round to pattern: offset + multiple * x
            // Formula: offset + ceil((size - offset) / multiple) * multiple
            float sizeAfterOffset = Math.max(0, size.x - alignWidthOffset);
            alignedWidth = alignWidthOffset + (float) Math.ceil(sizeAfterOffset / alignWidthToMultiple) * alignWidthToMultiple;
        }

        if (alignHeightToMultiple > 0) {
            // Round to pattern: offset + multiple * x
            float sizeAfterOffset = Math.max(0, size.y - alignHeightOffset);
            alignedHeight = alignHeightOffset + (float) Math.ceil(sizeAfterOffset / alignHeightToMultiple) * alignHeightToMultiple;
        }

        // Call parent setSize with aligned dimensions
        return super.setSize(new Vector2f(alignedWidth, alignedHeight));
    }

    @Override
    public T setSize(float width, float height) {
        return setSize(new Vector2f(width, height));
    }

    public int getAlignWidthToMultiple() {
        return alignWidthToMultiple;
    }

    public int getAlignHeightToMultiple() {
        return alignHeightToMultiple;
    }

    public int getAlignWidthOffset() {
        return alignWidthOffset;
    }

    public int getAlignHeightOffset() {
        return alignHeightOffset;
    }

    /**
     * Perform the actual layout calculation.
     * Override in subclasses to implement specific layout algorithms.
     */
    protected abstract void performLayout();
}
