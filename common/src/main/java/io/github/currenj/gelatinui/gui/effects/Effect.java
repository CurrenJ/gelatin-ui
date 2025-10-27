package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;

/**
 * Base interface for all UI element effects.
 * Effects can modify position, scale, rotation, and alpha over time.
 */
public interface Effect {
    /**
     * Unique identifier for this effect instance.
     */
    String getId();

    /**
     * Channel name for exclusivity. Effects with the same channel will replace each other.
     * Return null for no channel exclusivity.
     */
    String getChannel();

    /**
     * Priority determines combination order. Higher priority effects are applied last.
     * Default priority is 0.
     */
    int getPriority();

    /**
     * How this effect blends with others.
     */
    BlendMode getBlendMode();

    /**
     * Weight/intensity of this effect (0.0 to 1.0). Used by LERP blend mode.
     */
    float getWeight();

    /**
     * Update this effect for one frame.
     * @param deltaTime time elapsed since last frame in seconds
     * @param element the UI element this effect is applied to
     * @return true if effect is still active, false if it should be removed
     */
    boolean update(float deltaTime, UIElement<?> element);

    /**
     * Get the current transform delta produced by this effect.
     */
    TransformDelta getDelta();

    /**
     * Cancel this effect early (before natural completion).
     */
    void cancel();

    /**
     * Check if this effect is cancelled.
     */
    boolean isCancelled();
}

