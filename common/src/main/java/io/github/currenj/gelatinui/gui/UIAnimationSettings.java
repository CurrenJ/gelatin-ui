package io.github.currenj.gelatinui.gui;

/**
 * Client-local settings for UI animation speeds.
 * Values are held in memory for the current session and can be adjusted at
 * runtime (e.g. via the /gelatin debug animate command).
 *
 * Exponential-smoothing formula: t = 1 - exp(-speed * dt)
 *   speed =  1  →  ~63 % coverage per second (slow / floaty)
 *   speed = 10  →  ~300 ms to reach 95 %      (default, snappy)
 *   speed = 20  →  ~150 ms to reach 95 %      (very snappy)
 */
public final class UIAnimationSettings {

    public static final float DEFAULT_POSITION_SPEED = 4.0f;
    public static final float DEFAULT_SCALE_SPEED    = 4.0f;

    private static float positionSpeed = DEFAULT_POSITION_SPEED;
    private static float scaleSpeed    = DEFAULT_SCALE_SPEED;

    private UIAnimationSettings() {}

    // ── Getters ──────────────────────────────────────────────────────────────

    public static float getPositionSpeed() {
        return positionSpeed;
    }

    public static float getScaleSpeed() {
        return scaleSpeed;
    }

    // ── Setters ──────────────────────────────────────────────────────────────

    /** @param speed Exponential-smoothing speed ≥ 0.1. Higher = faster transitions. */
    public static void setPositionSpeed(float speed) {
        positionSpeed = Math.max(0.1f, speed);
    }

    /** @param speed Exponential-smoothing speed ≥ 0.1. Higher = faster transitions. */
    public static void setScaleSpeed(float speed) {
        scaleSpeed = Math.max(0.1f, speed);
    }

    /** Resets both speeds back to their compile-time defaults. */
    public static void reset() {
        positionSpeed = DEFAULT_POSITION_SPEED;
        scaleSpeed    = DEFAULT_SCALE_SPEED;
    }
}
