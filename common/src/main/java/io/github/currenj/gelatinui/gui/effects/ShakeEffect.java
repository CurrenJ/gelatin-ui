package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Rapid position jitter effect for shake animations.
 * Creates random small position offsets at high frequency.
 */
public class ShakeEffect extends AbstractEffect {
    private float amplitude = 3.0f; // Maximum shake distance in pixels
    private float frequency = 30.0f; // Samples per second
    private float decay = 0.95f; // Amplitude decay per second (1.0 = no decay)

    private float lastUpdateTime = 0f;
    private Vector2f currentOffset = new Vector2f(0, 0);

    public ShakeEffect() {
        this(null, 0, 0.5f); // Default 0.5 second shake
    }

    public ShakeEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.ADD, duration);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        // Update offset at specified frequency
        float updateInterval = 1.0f / frequency;
        if (elapsed - lastUpdateTime >= updateInterval) {
            lastUpdateTime = elapsed;

            // Apply decay
            float currentAmplitude = amplitude;
            if (duration > 0) {
                float decayFactor = (float) Math.pow(decay, elapsed);
                currentAmplitude *= decayFactor;
            }

            // Random offset
            float offsetX = (float) (Math.random() * 2.0 - 1.0) * currentAmplitude;
            float offsetY = (float) (Math.random() * 2.0 - 1.0) * currentAmplitude;
            currentOffset.set(offsetX, offsetY);
        }

        return new TransformDelta(currentOffset, 1.0f, 0f, 1.0f);
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setDecay(float decay) {
        this.decay = Math.max(0f, Math.min(1f, decay));
    }
}

