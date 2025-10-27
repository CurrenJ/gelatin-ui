package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Smooth directional movement effect.
 * Drifts in a specified direction at a constant velocity.
 */
public class DriftEffect extends AbstractEffect {
    private Vector2f velocity = new Vector2f(0, 10); // pixels per second
    private Vector2f currentOffset = new Vector2f(0, 0);

    public DriftEffect() {
        this(null, 0, -1f);
    }

    public DriftEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.ADD, duration);
    }

    public DriftEffect(Vector2f velocity, float duration) {
        this(null, 0, duration);
        this.velocity.set(velocity);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        // Calculate offset based on elapsed time and velocity
        currentOffset.set(velocity).mul(elapsed);
        return new TransformDelta(currentOffset, 1.0f, 0f, 1.0f);
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity.set(velocity);
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    public Vector2f getVelocity() {
        return new Vector2f(velocity);
    }
}

