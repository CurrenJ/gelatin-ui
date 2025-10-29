package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Quick scale bounce effect, similar to UIElement.playClickBounce().
 * Shrinks briefly then overshoots before settling back to normal scale.
 */
public class ClickBounceEffect extends AbstractEffect {
    private float amplitude = 0.08f; // How much to shrink initially
    private float overshoot = 0.06f; // How much to overshoot

    public ClickBounceEffect() {
        this(null, 0);
    }

    public ClickBounceEffect(String channel, int priority) {
        super(null, channel, priority, BlendMode.MULTIPLY, 0.22f);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        float scale;

        if (t < 0.27f) {
            // Shrink phase (0 to 0.27 normalized = 0 to 0.06s)
            float shrinkT = t / 0.27f;
            scale = 1.0f - amplitude * easeOutCubic(shrinkT);
        } else if (t < 0.64f) {
            // Overshoot phase (0.27 to 0.64 normalized = 0.06s to 0.14s)
            float overshootT = (t - 0.27f) / (0.64f - 0.27f);
            scale = 1.0f + overshoot * easeOutBack(overshootT);
        } else {
            // Settle phase (0.64 to 1.0 normalized = 0.14s to 0.22s)
            float settleT = (t - 0.64f) / (1.0f - 0.64f);
            scale = 1.0f + overshoot * (1.0f - easeInOutCubic(settleT));
        }

        return new TransformDelta(new Vector2f(0, 0), scale, 0f, 1.0f);
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public void setOvershoot(float overshoot) {
        this.overshoot = overshoot;
    }

    // Simple easing functions
    private float easeOutCubic(float t) {
        return 1f - (float) Math.pow(1f - t, 3);
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        t = t - 1f;
        return 1f + c3 * t * t * t + c1 * t * t;
    }

    private float easeInOutCubic(float t) {
        return t < 0.5f
                ? 4f * t * t * t
                : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }
}

