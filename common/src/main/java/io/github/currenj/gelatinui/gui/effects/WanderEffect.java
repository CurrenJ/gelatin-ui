package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Slow, smooth position drift using perlin-like noise.
 * Creates wandering motion within a bounded area.
 */
public class WanderEffect extends AbstractEffect {
    private float radius = 5.0f; // Maximum drift distance in pixels
    private float speed = 0.5f; // Movement speed multiplier
    private float seed = (float) Math.random() * 1000f;

    // Use different frequencies for X and Y to create more organic movement
    private final float xFrequency = 0.7f;
    private final float yFrequency = 0.53f;

    public WanderEffect() {
        this(null, 0);
    }

    public WanderEffect(String channel, int priority) {
        super(null, channel, priority, BlendMode.ADD, -1f); // Infinite duration
        this.loop = true;
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = elapsed * speed;

        // Create circular-ish motion with varying radius using layered sine waves
        float angle1 = t * xFrequency * 2.0f * (float) Math.PI + seed;
        float angle2 = t * yFrequency * 2.0f * (float) Math.PI + seed;

        // Layer multiple sine waves for more organic feel
        float offsetX = (float) (Math.sin(angle1) + 0.3 * Math.sin(angle1 * 2.3)) * radius * 0.5f;
        float offsetY = (float) (Math.sin(angle2) + 0.3 * Math.sin(angle2 * 1.7)) * radius * 0.5f;

        return new TransformDelta(new Vector2f(offsetX, offsetY), 1.0f, 0f, 1.0f);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
