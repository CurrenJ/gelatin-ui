package io.github.currenj.gelatinui.gui.animation;

/**
 * Common easing functions for UI animations. All functions expect t in [0,1].
 */
public final class Easing {
    private Easing() {}

    public interface Func {
        float ease(float t);
    }

    public static final Func LINEAR = t -> t;

    public static final Func EASE_OUT_CUBIC = t -> 1f - (float) Math.pow(1f - clamp01(t), 3);

    public static final Func EASE_IN_OUT_CUBIC = t -> {
        t = clamp01(t);
        return t < 0.5f
                ? 4f * t * t * t
                : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    };

    /**
     * Back easing with overshoot; s=1.70158 is a common default.
     */
    public static Func easeOutBack(float overshoot) {
        final float c1 = overshoot;
        final float c3 = c1 + 1f;
        return t -> {
            t = clamp01(t) - 1f;
            return 1f + c3 * t * t * t + c1 * t * t;
        };
    }

    public static final Func EASE_OUT_BACK = easeOutBack(1.70158f);

    private static float clamp01(float t) {
        if (t < 0f) return 0f;
        if (t > 1f) return 1f;
        return t;
    }
}

