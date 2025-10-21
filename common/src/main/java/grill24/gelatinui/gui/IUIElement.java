package grill24.gelatinui.gui;

import org.joml.Vector2f;

import java.awt.geom.Rectangle2D;

/**
 * Base interface for all UI elements in the system.
 * Provides core functionality for rendering, updating, and event handling.
 */
public interface IUIElement {
    /**
     * Update the element. Only called if the element is dirty or animating.
     * @param deltaTime Time since last update in seconds
     */
    void update(float deltaTime);

    /**
     * Render the element and its children.
     * @param context Render context for drawing operations
     * @param viewport Current viewport bounds for culling
     */
    void render(IRenderContext context, Rectangle2D viewport);

    /**
     * Get the global bounds of this element in screen coordinates.
     * @return Rectangle representing the element's bounds
     */
    Rectangle2D getBounds();

    /**
     * Get the position of this element.
     * @return Position vector (x, y)
     */
    Vector2f getPosition();

    /**
     * Set the position of this element.
     * @param position New position
     */
    void setPosition(Vector2f position);

    /**
     * Get the size of this element.
     * @return Size vector (width, height)
     */
    Vector2f getSize();

    /**
     * Check if this element is visible.
     * @return true if visible, false otherwise
     */
    boolean isVisible();

    /**
     * Set visibility state.
     * @param visible New visibility state
     */
    void setVisible(boolean visible);

    /**
     * Check if this element needs updating.
     * @return true if dirty or animating
     */
    boolean needsUpdate();

    /**
     * Mark this element as needing an update.
     * @param flags Specific aspects that changed
     */
    void markDirty(DirtyFlag... flags);

    /**
     * Get the parent element.
     * @return Parent element or null if root
     */
    IUIElement getParent();

    /**
     * Set the parent element.
     * @param parent New parent element
     */
    void setParent(IUIElement parent);

    /**
     * Handle a UI event.
     * @param event The event to handle
     * @return true if the event was consumed
     */
    boolean handleEvent(UIEvent event);

    /**
     * Check if this element or any children intersect the viewport.
     * @param viewport Viewport bounds
     * @return true if visible in viewport
     */
    boolean isInViewport(Rectangle2D viewport);
}

