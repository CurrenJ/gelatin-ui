package io.github.currenj.gelatinui.gui;

import org.joml.Vector2f;

/**
 * Determines how the scale pivot point is computed relative to an element's size.
 * <p>
 * When any mode other than {@link #FIXED} is active, the pivot is automatically
 * recalculated whenever the element's size changes, so callers do not need to
 * call {@code setScalePivot} again after layout.
 * <p>
 * Use {@link #FIXED} (the default) together with
 * {@code UIElement.setScalePivot()} for a manually specified pivot that never
 * moves.
 */
public enum PivotMode {
    /** Pivot is set manually via {@code setScalePivot()}; not recalculated on resize. */
    FIXED,
    /** Top-left corner: (0, 0). */
    TOP_LEFT,
    /** Top edge, horizontal center: (w/2, 0). */
    TOP_CENTER,
    /** Top-right corner: (w, 0). */
    TOP_RIGHT,
    /** Left edge, vertical center: (0, h/2). */
    CENTER_LEFT,
    /** Center of the element: (w/2, h/2). */
    CENTER,
    /** Right edge, vertical center: (w, h/2). */
    CENTER_RIGHT,
    /** Bottom-left corner: (0, h). */
    BOTTOM_LEFT,
    /** Bottom edge, horizontal center: (w/2, h). */
    BOTTOM_CENTER,
    /** Bottom-right corner: (w, h). */
    BOTTOM_RIGHT;

    /**
     * Compute the pivot coordinate for an element with the given dimensions.
     *
     * @param width  element width
     * @param height element height
     * @return computed pivot, or {@code null} for {@link #FIXED}
     */
    public Vector2f compute(float width, float height) {
        return switch (this) {
            case FIXED        -> null;
            case TOP_LEFT     -> new Vector2f(0,           0);
            case TOP_CENTER   -> new Vector2f(width * 0.5f, 0);
            case TOP_RIGHT    -> new Vector2f(width,        0);
            case CENTER_LEFT  -> new Vector2f(0,           height * 0.5f);
            case CENTER       -> new Vector2f(width * 0.5f, height * 0.5f);
            case CENTER_RIGHT -> new Vector2f(width,        height * 0.5f);
            case BOTTOM_LEFT  -> new Vector2f(0,           height);
            case BOTTOM_CENTER-> new Vector2f(width * 0.5f, height);
            case BOTTOM_RIGHT -> new Vector2f(width,        height);
        };
    }
}
