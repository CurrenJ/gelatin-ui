package grill24.gelatinui.gui;

import org.joml.Vector2f;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Base class for container elements that can hold children.
 * Implements efficient child management with layout caching and dirty propagation.
 */
public abstract class UIContainer extends UIElement {
    // Children collection
    protected List<IUIElement> children = new ArrayList<>();

    // Layout cache
    protected LayoutCache layoutCache = new LayoutCache();

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
        if (!needsUpdate() && !anyChildNeedsUpdate()) {
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

    @Override
    protected void invalidateChildBounds() {
        // Recursively invalidate bounds of all children since they depend on parent's transform
        for (IUIElement child : children) {
            if (child instanceof UIElement) {
                UIElement uiChild = (UIElement) child;
                uiChild.boundsValid = false;
                uiChild.invalidateChildBounds(); // Recurse down the tree
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

    /**
     * Perform the actual layout calculation.
     * Override in subclasses to implement specific layout algorithms.
     */
    protected abstract void performLayout();
}
