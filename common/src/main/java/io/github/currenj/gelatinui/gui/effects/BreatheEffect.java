package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Slow oscillating scale effect that creates a "breathing" animation.
 * X and Y scale oscillate out of phase for a more organic feel.
 */
public class BreatheEffect extends AbstractEffect {
    private float amplitude = 0.05f; // How much to scale (0.05 = 5%)
    private float frequency = 1.0f; // Cycles per second
    private float phaseOffset = 0f; // Phase offset for Y axis (radians)

    public BreatheEffect() {
        this(null, 0);
    }

    public BreatheEffect(String channel, int priority) {
        super(null, channel, priority, BlendMode.MULTIPLY, -1f); // Infinite duration
        this.loop = true;
        this.phaseOffset = (float) (Math.PI / 2.0); // 90 degrees out of phase
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float time = elapsed * frequency * 2.0f * (float) Math.PI;

        // Oscillate scale using sine wave
        float scaleX = 1.0f + amplitude * (float) Math.sin(time);
        float scaleY = 1.0f + amplitude * (float) Math.sin(time + phaseOffset);

        // Average for uniform scale (or could expose separate X/Y in future)
        float scale = (scaleX + scaleY) / 2.0f;

        return new TransformDelta(new Vector2f(0, 0), scale, 0f, 1.0f);
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setPhaseOffset(float phaseOffsetDegrees) {
        this.phaseOffset = (float) Math.toRadians(phaseOffsetDegrees);
    }
}

