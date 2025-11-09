package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.animation.Easing;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Continuous rotation animation for spinning items.
 * Can be configured for single spin or continuous looping.
 * For 3D items like ItemRenderer, uses true 3D rotation around Y-axis.
 * For 2D elements, uses Z-axis (in-plane) rotation.
 */
public class SpinEffect extends AbstractEffect {
    private float rotationSpeed = 360f; // Degrees per second (1 full rotation)
    private boolean clockwise = true;
    private Easing.Func easingFunc = Easing.LINEAR;

    public SpinEffect() {
        this(null, 0, 1.0f); // Default 1 second for one full rotation
    }

    public SpinEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.ADD, duration);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        
        // Apply easing function
        float easedT = easingFunc.ease(t);
        
        // Calculate rotation based on speed and time
        float rotation = rotationSpeed * easedT;
        
        // Reverse direction if counter-clockwise
        if (!clockwise) {
            rotation = -rotation;
        }
        
        // For 3D items, rotate around Y-axis (vertical axis for item showcase)
        if (element.supports3DRotation()) {
            Vector3f rotation3D = new Vector3f(0, rotation, 0);
            return new TransformDelta(new Vector2f(0, 0), 1.0f, 1.0f, rotation3D);
        } else {
            // For 2D elements, use Z-axis (in-plane) rotation
            Vector3f rotation3D = new Vector3f(0, 0, rotation);
            return new TransformDelta(new Vector2f(0, 0), 1.0f, 1.0f, rotation3D);
        }
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
     * Set the easing function for this spin effect.
     * Use Easing.LINEAR, Easing.EASE_IN_OUT_SINE, etc.
     */
    public SpinEffect setEasing(Easing.Func easingFunc) {
        this.easingFunc = easingFunc != null ? easingFunc : Easing.LINEAR;
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
