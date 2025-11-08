package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Continuous rotation animation for spinning items.
 * Can be configured for single spin or continuous looping.
 */
public class SpinEffect extends AbstractEffect {
    private float rotationSpeed = 360f; // Degrees per second (1 full rotation)
    private boolean clockwise = true;
    private float easingPower = 1.0f; // 1.0 = linear, >1 = ease out, <1 = ease in

    public SpinEffect() {
        this(null, 0, 1.0f); // Default 1 second for one full rotation
    }

    public SpinEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.ADD, duration);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        
        // Apply easing if configured
        float easedT = t;
        if (easingPower != 1.0f) {
            easedT = (float) Math.pow(t, easingPower);
        }
        
        // Calculate rotation based on speed and time
        float rotation = rotationSpeed * easedT;
        
        // Reverse direction if counter-clockwise
        if (!clockwise) {
            rotation = -rotation;
        }
        
        return new TransformDelta(new Vector2f(0, 0), 1.0f, rotation, 1.0f);
    }

    /**
     * Set the rotation speed in degrees per second.
     * For a full 360° rotation in the duration, use 360 / duration.
     */
    public SpinEffect setRotationSpeed(float degreesPerSecond) {
        this.rotationSpeed = degreesPerSecond;
        return this;
    }

    /**
     * Set rotation direction.
     */
    public SpinEffect setClockwise(boolean clockwise) {
        this.clockwise = clockwise;
        return this;
    }

    /**
     * Set easing power for acceleration/deceleration.
     * 1.0 = linear (default)
     * > 1.0 = ease out (fast start, slow end)
     * < 1.0 = ease in (slow start, fast end)
     */
    public SpinEffect setEasingPower(float power) {
        this.easingPower = Math.max(0.1f, power);
        return this;
    }

    /**
     * Create a continuous spinning effect that loops indefinitely.
     */
    public static SpinEffect continuous(float rotationsPerSecond) {
        SpinEffect effect = new SpinEffect(null, 0, -1f); // Infinite duration
        effect.setRotationSpeed(rotationsPerSecond * 360f);
        effect.setLoop(true);
        return effect;
    }
}
