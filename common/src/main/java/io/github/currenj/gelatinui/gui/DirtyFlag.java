package io.github.currenj.gelatinui.gui;

/**
 * Flags indicating what aspects of a UI element have changed.
 * Used for efficient dirty-checking and selective updates.
 */
public enum DirtyFlag {
    /**
     * Position has changed
     */
    POSITION,

    /**
     * Size has changed
     */
    SIZE,

    /**
     * Child list has been modified (add/remove)
     */
    CHILDREN,

    /**
     * Content has changed (text, color, texture, etc.)
     */
    CONTENT,

    /**
     * Visibility state has changed
     */
    VISIBILITY,

    /**
     * Style/theme properties have changed
     */
    STYLE,

    /**
     * Layout needs recalculation
     */
    LAYOUT
}

