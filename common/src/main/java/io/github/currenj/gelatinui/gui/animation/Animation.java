package io.github.currenj.gelatinui.gui.animation;

/**
 * Base interface for time-based animations that can be driven each frame.
 */
public interface Animation {
    /**
     * A logical channel name used to make certain animations exclusive (e.g., "effectScale").
     * Returning null means no channel and no exclusivity.
     */
    String getChannel();

    /**
     * Advance the animation by dt seconds and apply side effects via configured callbacks.
     * @param dt delta time in seconds
     * @return true if the animation is still running, false if it completed
     */
    boolean update(float dt);

    /**
     * Called when an animation is cancelled or replaced by a new one on the same channel.
     * Implementations may perform cleanup.
     */
    default void cancel() {}
}
