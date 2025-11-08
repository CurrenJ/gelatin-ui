package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Flashy coin spin animation with rotation and scale oscillation.
 * Creates a 3D coin flip effect by varying scale on one axis while rotating.
 */
public class CoinSpinEffect extends AbstractEffect {
    private float rotationSpeed = 720f; // Degrees per second (2 full rotations)
    private float scaleAmplitude = 0.3f; // How much to vary scale (simulates 3D depth)
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
        
        // Rotation: linear spin
        float rotation = rotationSpeed * t;
        
        // Scale: cosine wave to simulate 3D flip (narrow at 90°, full at 0°/180°)
        // Absolute cosine to always keep positive scale
        float scaleT = (float) Math.abs(Math.cos(t * Math.PI * 2.0)); // 0 to 1 to 0 twice
        float scale = 1.0f - scaleAmplitude + (scaleAmplitude * scaleT);
        
        // Alpha: slight pulse for flashy effect using sine wave
        float alphaPulse = 1.0f + glowPulse * (float) Math.sin(t * Math.PI * 4.0); // Faster pulse
        alphaPulse = Math.max(0.7f, Math.min(1.3f, alphaPulse)); // Clamp for visibility
        
        return new TransformDelta(new Vector2f(0, 0), scale, rotation, alphaPulse);
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
