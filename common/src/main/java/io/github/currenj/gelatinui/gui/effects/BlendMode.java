package io.github.currenj.gelatinui.gui.effects;

/**
 * Defines how multiple effects combine their transform deltas.
 */
public enum BlendMode {
    /**
     * Add position offsets, add rotation deltas, and add (scale - 1.0) for scale.
     * Alpha is multiplied.
     */
    ADD,

    /**
     * Multiply scale and alpha values. Position and rotation are added.
     */
    MULTIPLY,

    /**
     * Highest priority effect overrides the component entirely.
     * Lower priority effects are ignored for this component.
     */
    OVERRIDE,

    /**
     * Interpolate between base and effect delta by the effect's weight.
     * Weight of 1.0 means full effect, 0.0 means no effect.
     */
    LERP
}

