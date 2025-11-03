package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IUIElement;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;

/**
 * A container that allows manual positioning of children at specific coordinates.
 * Unlike HBox/VBox which auto-layout children, this container lets you specify
 * exact positions for each child. Children are automatically centered at their
 * designated positions and will adjust if their size changes.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Manual positioning of children at specific coordinates</li>
 *   <li>Optional limit on maximum number of children</li>
 *   <li>Fixed container size (dynamic sizing based on children disabled by default)</li>
 *   <li>Children are automatically centered at their manual positions</li>
 *   <li>Automatic repositioning when child size/shape changes</li>
 *   <li>Works well with sprite/texture backgrounds without deformation</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * ManualContainer container = new ManualContainer()
 *     .setSize(200, 100)
 *     .maxChildren(3)
 *     .backgroundSprite(mySprite);
 * 
 * // Add children at specific positions (children will be centered at these points)
 * container.addChildAt(itemIcon1, 50, 50);   // centered at (50, 50)
 * container.addChildAt(itemIcon2, 100, 50);  // centered at (100, 50)
 * container.addChildAt(itemIcon3, 150, 50);  // centered at (150, 50)
 * }</pre>
 */
public class ManualContainer extends PanelBase<ManualContainer> {
    /**
     * Tolerance for floating-point comparisons when detecting size changes.
     */
    private static final float FLOAT_TOLERANCE = 0.001f;
    
    /**
     * Map storing the manual position for each child.
     * The position represents the center point where the child should be positioned.
     * During layout, the child's top-left corner is calculated as:
     * (centerX - childWidth/2, centerY - childHeight/2)
     */
    private final Map<IUIElement, Vector2f> childPositions = new HashMap<>();
    
    /**
     * Maximum number of children allowed. 0 means unlimited.
     */
    private int maxChildren = 0;
    
    /**
     * Previous sizes of children, used to detect size changes and trigger repositioning.
     */
    private final Map<IUIElement, Vector2f> previousChildSizes = new HashMap<>();

    public ManualContainer() {
        // Disable auto-sizing to children by default since we want a fixed size
        // that matches the background sprite/texture
        this.autoSizeToChildren(false);
    }

    /**
     * Set the maximum number of children this container can hold.
     * Set to 0 for unlimited (default).
     * 
     * @param maxChildren Maximum number of children, or 0 for unlimited
     * @return this for method chaining
     * @throws IllegalArgumentException if maxChildren is negative
     */
    public ManualContainer maxChildren(int maxChildren) {
        if (maxChildren < 0) {
            throw new IllegalArgumentException("maxChildren must be >= 0");
        }
        this.maxChildren = maxChildren;
        return this;
    }

    /**
     * Add a child at a specific position.
     * The child will be centered at the given coordinates.
     * 
     * @param child The child element to add
     * @param x The x-coordinate where the child should be centered
     * @param y The y-coordinate where the child should be centered
     * @throws IllegalStateException if maxChildren limit is reached
     * @throws IllegalArgumentException if child is null
     */
    public void addChildAt(IUIElement child, float x, float y) {
        addChildAt(child, new Vector2f(x, y));
    }

    /**
     * Add a child at a specific position.
     * The child will be centered at the given coordinates.
     * 
     * @param child The child element to add
     * @param position The position where the child should be centered
     * @throws IllegalStateException if maxChildren limit is reached
     * @throws IllegalArgumentException if child is null
     */
    public void addChildAt(IUIElement child, Vector2f position) {
        if (child == null) {
            throw new IllegalArgumentException("Cannot add null child");
        }
        
        // Check max children limit
        if (maxChildren > 0 && children.size() >= maxChildren) {
            throw new IllegalStateException(
                "Cannot add child: maximum children limit (" + maxChildren + ") reached"
            );
        }
        
        // Store the manual position (represents the center point)
        childPositions.put(child, new Vector2f(position));
        
        // Store initial size for change detection
        previousChildSizes.put(child, new Vector2f(child.getSize()));
        
        // Add to parent container
        super.addChild(child);
        
        // Trigger layout to position the child
        markDirty(DirtyFlag.LAYOUT);
    }

    /**
     * Update the position for an existing child.
     * The child will be re-centered at the new coordinates.
     * 
     * @param child The child element whose position should be updated
     * @param x The new x-coordinate where the child should be centered
     * @param y The new y-coordinate where the child should be centered
     * @throws IllegalArgumentException if child is not in this container
     */
    public void updateChildPosition(IUIElement child, float x, float y) {
        updateChildPosition(child, new Vector2f(x, y));
    }

    /**
     * Update the position for an existing child.
     * The child will be re-centered at the new coordinates.
     * 
     * @param child The child element whose position should be updated
     * @param position The new position where the child should be centered
     * @throws IllegalArgumentException if child is not in this container
     */
    public void updateChildPosition(IUIElement child, Vector2f position) {
        if (!childPositions.containsKey(child)) {
            throw new IllegalArgumentException("Child is not in this container");
        }
        
        childPositions.put(child, new Vector2f(position));
        markDirty(DirtyFlag.LAYOUT);
    }

    /**
     * Get the manual position (center point) for a child.
     * 
     * @param child The child element
     * @return The position where the child is centered, or null if child is not in this container
     */
    public Vector2f getChildPosition(IUIElement child) {
        Vector2f pos = childPositions.get(child);
        return pos != null ? new Vector2f(pos) : null;
    }

    @Override
    public void removeChild(IUIElement child) {
        childPositions.remove(child);
        previousChildSizes.remove(child);
        super.removeChild(child);
    }

    @Override
    public void clearChildren() {
        childPositions.clear();
        previousChildSizes.clear();
        super.clearChildren();
    }

    @Override
    protected void performLayout() {
        // Position each child centered at its manual position
        for (IUIElement child : children) {
            if (!child.isVisible()) {
                continue;
            }
            
            Vector2f centerPos = childPositions.get(child);
            if (centerPos == null) {
                // This shouldn't happen, but handle gracefully
                continue;
            }
            
            Vector2f childSize = child.getSize();
            
            // Check if child size has changed
            Vector2f prevSize = previousChildSizes.get(child);
            boolean sizeChanged = prevSize == null || !prevSize.equals(childSize, FLOAT_TOLERANCE);
            
            if (sizeChanged) {
                // Update the stored size
                previousChildSizes.put(child, new Vector2f(childSize));
            }
            
            // Calculate top-left position to center the child at the manual position
            float childX = centerPos.x - (childSize.x / 2f);
            float childY = centerPos.y - (childSize.y / 2f);
            
            child.setPosition(new Vector2f(childX, childY));
        }
    }

    @Override
    protected ManualContainer self() {
        return this;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Check if any child sizes have changed and trigger re-layout if needed
        boolean needsRelayout = false;
        for (IUIElement child : children) {
            if (!child.isVisible()) {
                continue;
            }
            
            Vector2f currentSize = child.getSize();
            Vector2f prevSize = previousChildSizes.get(child);
            
            if (prevSize != null && !prevSize.equals(currentSize, FLOAT_TOLERANCE)) {
                needsRelayout = true;
                break;
            }
        }
        
        if (needsRelayout) {
            markDirty(DirtyFlag.LAYOUT);
        }
    }

    /**
     * Get the maximum number of children allowed.
     * 
     * @return Maximum number of children, or 0 if unlimited
     */
    public int getMaxChildren() {
        return maxChildren;
    }

    /**
     * Check if this container is at its maximum capacity.
     * 
     * @return true if the container is full (at max children limit), false otherwise
     */
    public boolean isFull() {
        return maxChildren > 0 && children.size() >= maxChildren;
    }

    /**
     * Get the number of remaining slots available in this container.
     * 
     * @return Number of children that can still be added, or Integer.MAX_VALUE if unlimited
     */
    public int getRemainingSlots() {
        if (maxChildren <= 0) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, maxChildren - children.size());
    }
}
