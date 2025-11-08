package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Unique creative animation that combines pulsing scale with glow effects.
 * Creates an attention-grabbing effect with rhythmic expansion and brightness pulses,
 * like a magical or rare item highlighting itself.
 */
public class PulseGlowEffect extends AbstractEffect {
    private float scaleAmplitude = 0.15f; // How much to pulse the scale (0.15 = 15%)
    private float glowIntensity = 0.4f; // Alpha variation intensity
    private float pulseFrequency = 2.0f; // Pulses per second
    private float rotationSpeed = 20f; // Slow rotation for extra flair (degrees per second)
    private boolean enableRotation = true;

    public PulseGlowEffect() {
        this(null, 0);
    }

    public PulseGlowEffect(String channel, int priority) {
        super(null, channel, priority, BlendMode.MULTIPLY, -1f); // Infinite by default
        this.loop = true;
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float time = elapsed;
        
        // Primary pulse: smooth sine wave for scale
        float pulsePhase = time * pulseFrequency * 2.0f * (float) Math.PI;
        float scalePulse = (float) Math.sin(pulsePhase);
        float scale = 1.0f + scaleAmplitude * scalePulse;
        
        // Glow effect: phase-shifted pulse for alpha (creates a "breathing light" effect)
        // Use a different phase so glow peaks slightly after scale peaks
        float glowPhase = pulsePhase + (float) Math.PI * 0.3f;
        float glowPulse = (float) Math.sin(glowPhase);
        // Map from -1..1 to a reasonable alpha range
        float alpha = 1.0f + glowIntensity * glowPulse * 0.5f;
        alpha = Math.max(0.6f, Math.min(1.4f, alpha)); // Clamp for visibility
        
        // Optional subtle rotation for extra flair
        float rotation = 0f;
        if (enableRotation) {
            rotation = (time * rotationSpeed) % 360f;
        }
        
        // Add a secondary subtle pulse at double frequency for complexity
        float secondaryPulse = (float) Math.sin(pulsePhase * 2.0f) * 0.05f;
        scale += secondaryPulse;
        
        return new TransformDelta(new Vector2f(0, 0), scale, rotation, alpha);
    }

    /**
     * Set the scale pulse amplitude (0-1).
     * Higher values create more dramatic size changes.
     */
    public PulseGlowEffect setScaleAmplitude(float amplitude) {
        this.scaleAmplitude = Math.max(0f, Math.min(1f, amplitude));
        return this;
    }

    /**
     * Set the glow intensity (0-1).
     * Controls how much the alpha/brightness varies.
     */
    public PulseGlowEffect setGlowIntensity(float intensity) {
        this.glowIntensity = Math.max(0f, Math.min(1f, intensity));
        return this;
    }

    /**
     * Set the pulse frequency (pulses per second).
     */
    public PulseGlowEffect setPulseFrequency(float frequency) {
        this.pulseFrequency = Math.max(0.1f, frequency);
        return this;
    }

    /**
     * Set the rotation speed in degrees per second.
     */
    public PulseGlowEffect setRotationSpeed(float degreesPerSecond) {
        this.rotationSpeed = degreesPerSecond;
        return this;
    }

    /**
     * Enable or disable the rotation component.
     */
    public PulseGlowEffect setEnableRotation(boolean enable) {
        this.enableRotation = enable;
        return this;
    }

    /**
     * Create a one-shot pulse (non-looping) with specific duration.
     */
    public static PulseGlowEffect oneShot(float duration) {
        PulseGlowEffect effect = new PulseGlowEffect(null, 0);
        effect.duration = duration;
        effect.loop = false;
        return effect;
    }
}
