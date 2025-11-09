package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Card-flip style animation that rotates and scales to create a 3D flip effect.
 * The element appears to flip from front to back along the Y axis.
 */
public class FlipEffect extends AbstractEffect {
    private float flipRotation = 180f; // Degrees to rotate (180 = flip to back, 360 = full flip)
    private Axis flipAxis = Axis.Y; // Which axis to flip around
    private float perspectiveScale = 0.8f; // Scale at 90° to simulate perspective (0-1)

    public enum Axis {
        X, // Flip horizontally (top to bottom)
        Y, // Flip vertically (left to right)
        Z  // Rotate in plane (no perspective effect)
    }

    public FlipEffect() {
        this(null, 0, 0.6f); // Default 0.6 second flip
    }

    public FlipEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.MULTIPLY, duration);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        
        // Ease in-out for smoother flip
        float easedT = easeInOutCubic(t);
        
        // Calculate rotation and scale
        Vector3f rotation3D = new Vector3f(0, 0, 0);
        float scale = 1.0f;
        float alpha = 1.0f;

        if (element.supports3DRotation()) {
            // True 3D rotation
            float rotation = flipRotation * easedT;
            switch (flipAxis) {
                case X:
                    rotation3D.x = rotation;
                    break;
                case Y:
                    rotation3D.y = rotation;
                    break;
                case Z:
                    rotation3D.z = rotation;
                    break;
            }
            // For 3D, no scale simulation needed
            scale = 1.0f;
        } else {
            // Fallback for 2D elements: simulate 3D with scale
            if (flipAxis == Axis.Z) {
                // Simple rotation in plane (no scale effect)
                rotation3D.z = flipRotation * easedT;
                scale = 1.0f;
            } else {
                // Simulate 3D flip with scale
                // At t=0: scale=1, at t=0.5: scale=perspectiveScale (edge view), at t=1: scale=1
                float flipProgress = easedT * 2.0f; // 0 to 2

                if (flipProgress < 1.0f) {
                    // First half: scale down to edge
                    scale = 1.0f - (1.0f - perspectiveScale) * flipProgress;
                } else {
                    // Second half: scale back up from edge
                    scale = perspectiveScale + (1.0f - perspectiveScale) * (flipProgress - 1.0f);
                }

                // For visual effect, we can add subtle rotation even though we're simulating 3D with 2D scale
                // This helps sell the effect
                rotation3D.z = flipRotation * easedT;
            }
        }
        
        return new TransformDelta(new Vector2f(0, 0), scale, alpha, rotation3D);
    }

    /**
     * Set the total rotation in degrees for the flip.
     * 180 = flip to show back, 360 = full flip to front, etc.
     */
    public FlipEffect setFlipRotation(float degrees) {
        this.flipRotation = degrees;
        return this;
    }

    /**
     * Set which axis to flip around.
     */
    public FlipEffect setFlipAxis(Axis axis) {
        this.flipAxis = axis;
        return this;
    }

    /**
     * Set the scale at the 90° point (edge-on view) to simulate perspective.
     * Lower values (e.g., 0.1) create a more dramatic perspective effect.
     */
    public FlipEffect setPerspectiveScale(float scale) {
        this.perspectiveScale = Math.max(0.01f, Math.min(1f, scale));
        return this;
    }

    /**
     * Easing function for smooth acceleration/deceleration.
     */
    private float easeInOutCubic(float t) {
        return t < 0.5f
                ? 4f * t * t * t
                : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }
}
