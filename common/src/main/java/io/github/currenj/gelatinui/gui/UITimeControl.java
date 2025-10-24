package io.github.currenj.gelatinui.gui;

/**
 * Global time control system for UI animations and updates.
 * Provides timescale adjustment and step-by-step debugging capabilities.
 */
public class UITimeControl {
    // Global timescale (1.0 = normal speed, 0.5 = half speed, 2.0 = double speed)
    private static float globalTimescale = 1.0f;

    // Pause state
    private static boolean isPaused = false;

    // Step mode: when paused, accumulate requested steps
    private static int stepsRequested = 0;
    private static float stepSize = 0.016f; // Default to ~60fps step (1/60 second)

    /**
     * Set the global timescale for all UI animations and updates.
     * @param scale The timescale multiplier (1.0 = normal, 0.5 = half speed, 2.0 = double speed)
     */
    public static void setTimescale(float scale) {
        globalTimescale = Math.max(0.0f, scale);
    }

    /**
     * Get the current global timescale.
     */
    public static float getTimescale() {
        return globalTimescale;
    }

    /**
     * Pause all UI updates and animations.
     */
    public static void pause() {
        isPaused = true;
    }

    /**
     * Resume all UI updates and animations.
     */
    public static void resume() {
        isPaused = false;
        stepsRequested = 0;
    }

    /**
     * Toggle pause state.
     */
    public static void togglePause() {
        isPaused = !isPaused;
        if (!isPaused) {
            stepsRequested = 0;
        }
    }

    /**
     * Check if the UI is currently paused.
     */
    public static boolean isPaused() {
        return isPaused;
    }

    /**
     * When paused, advance the animation by one step.
     */
    public static void step() {
        if (isPaused) {
            stepsRequested++;
        }
    }

    /**
     * When paused, advance the animation by multiple steps.
     */
    public static void step(int count) {
        if (isPaused) {
            stepsRequested += Math.max(0, count);
        }
    }

    /**
     * Set the delta time used for each step when in step mode.
     * @param seconds The time delta per step in seconds (default is 0.016 = ~60fps)
     */
    public static void setStepSize(float seconds) {
        stepSize = Math.max(0.001f, seconds);
    }

    /**
     * Get the configured step size in seconds.
     */
    public static float getStepSize() {
        return stepSize;
    }

    /**
     * Process a delta time through the time control system.
     * This applies timescale and handles pause/step logic.
     *
     * @param deltaTime The raw delta time in seconds
     * @return The adjusted delta time to use for updates (0 if paused, scaled otherwise)
     */
    public static float processDeltaTime(float deltaTime) {
        if (isPaused) {
            // In pause mode, only advance if steps are requested
            if (stepsRequested > 0) {
                stepsRequested--;
                return stepSize;
            }
            return 0.0f;
        }

        // Apply timescale to normal deltaTime
        return deltaTime * globalTimescale;
    }

    /**
     * Reset time control to default state.
     */
    public static void reset() {
        globalTimescale = 1.0f;
        isPaused = false;
        stepsRequested = 0;
        stepSize = 0.016f;
    }

    /**
     * Get a status string describing the current time control state.
     */
    public static String getStatusString() {
        if (isPaused) {
            return String.format("PAUSED (steps pending: %d, step size: %.3fs)", stepsRequested, stepSize);
        } else {
            return String.format("RUNNING (timescale: %.2fx)", globalTimescale);
        }
    }
}

