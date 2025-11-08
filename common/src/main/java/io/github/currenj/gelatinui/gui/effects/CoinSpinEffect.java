package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Flashy coin spin animation with 3D rotation.
 * Creates a realistic 3D coin flip effect by rotating around the Y-axis.
 * For 3D items like ItemRenderer, this uses true 3D rotation.
 * For 2D elements, falls back to scale simulation.
 */
public class CoinSpinEffect extends AbstractEffect {
    private float rotationSpeed = 720f; // Degrees per second (2 full rotations)
    private float scaleAmplitude = 0.3f; // How much to vary scale (for 2D fallback)
    private float glowPulse = 0.15f; // Alpha variation for flashy effect

    public CoinSpinEffect() {
        this(null, 0, 1.0f); // Default 1 second spin
    }

    public CoinSpinEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.MULTIPLY, duration);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        
        // Rotation: linear spin around Y-axis for 3D coin flip
        float yRotation = rotationSpeed * t;
        
        // For 3D items, use true 3D rotation
        if (element.supports3DRotation()) {
            // True 3D rotation around Y-axis
            Vector3f rotation3D = new Vector3f(0, yRotation, 0);
            
            // Alpha pulse for flashy effect
            float alphaPulse = 1.0f + glowPulse * (float) Math.sin(t * Math.PI * 4.0);
            alphaPulse = Math.max(0.7f, Math.min(1.3f, alphaPulse));
            
            // No scale manipulation needed for 3D rotation
            return new TransformDelta(new Vector2f(0, 0), 1.0f, yRotation, alphaPulse, rotation3D);
        } else {
            // Fallback for 2D elements: simulate 3D with scale
            // Scale: cosine wave to simulate 3D flip (narrow at 90°, full at 0°/180°)
            float scaleT = (float) Math.abs(Math.cos(t * Math.PI * 2.0));
            float scale = 1.0f - scaleAmplitude + (scaleAmplitude * scaleT);
            
            // Alpha pulse
            float alphaPulse = 1.0f + glowPulse * (float) Math.sin(t * Math.PI * 4.0);
            alphaPulse = Math.max(0.7f, Math.min(1.3f, alphaPulse));
            
            // Use Z-axis rotation for 2D spin
            return new TransformDelta(new Vector2f(0, 0), scale, yRotation, alphaPulse);
        }
    }

    /**
     * Set the rotation speed in degrees per second.
     */
    public CoinSpinEffect setRotationSpeed(float degreesPerSecond) {
        this.rotationSpeed = degreesPerSecond;
        return this;
    }

    /**
     * Set how much the scale varies (0 to 1).
     * Higher values create a more pronounced 3D effect.
     */
    public CoinSpinEffect setScaleAmplitude(float amplitude) {
        this.scaleAmplitude = Math.max(0f, Math.min(1f, amplitude));
        return this;
    }

    /**
     * Set the intensity of the glow pulse effect (0 to 1).
     */
    public CoinSpinEffect setGlowPulse(float intensity) {
        this.glowPulse = Math.max(0f, Math.min(1f, intensity));
        return this;
    }
}
