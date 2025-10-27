package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

import java.util.UUID;

/**
 * Abstract base class for effects with common fields and lifecycle management.
 */
public abstract class AbstractEffect implements Effect {
    protected final String id;
    protected final String channel;
    protected final int priority;
    protected final BlendMode blendMode;
    protected float weight = 1.0f;

    // Lifecycle
    protected float duration; // seconds, -1 for infinite
    protected float elapsed = 0f;
    protected boolean loop = false;
    protected boolean pingPong = false;
    protected boolean cancelled = false;
    protected boolean forward = true; // for ping-pong

    // Cached delta
    protected TransformDelta currentDelta = TransformDelta.IDENTITY;

    protected AbstractEffect(String id, String channel, int priority, BlendMode blendMode, float duration) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.channel = channel;
        this.priority = priority;
        this.blendMode = blendMode;
        this.duration = duration;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public BlendMode getBlendMode() {
        return blendMode;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = Math.max(0f, Math.min(1f, weight));
    }

    @Override
    public TransformDelta getDelta() {
        return currentDelta;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean update(float deltaTime, UIElement<?> element) {
        if (cancelled) {
            return false;
        }

        elapsed += deltaTime;

        // Handle duration and looping
        if (duration > 0) {
            if (pingPong) {
                // Ping-pong: reverse direction at boundaries
                if (forward && elapsed >= duration) {
                    forward = false;
                    elapsed = duration;
                } else if (!forward && elapsed <= 0) {
                    if (loop) {
                        forward = true;
                        elapsed = 0;
                    } else {
                        return false; // finished
                    }
                }
            } else {
                // Normal looping
                if (elapsed >= duration) {
                    if (loop) {
                        elapsed = elapsed % duration;
                    } else {
                        elapsed = duration;
                        // Calculate final delta and finish
                        currentDelta = calculateDelta(element);
                        return false;
                    }
                }
            }
        }

        // Calculate current delta
        TransformDelta oldDelta = currentDelta;
        currentDelta = calculateDelta(element);

        // Mark element dirty if delta changed significantly
        if (!deltasEqual(oldDelta, currentDelta)) {
            markElementDirty(element, oldDelta, currentDelta);
        }

        return true;
    }

    /**
     * Calculate the transform delta for the current state.
     * Subclasses implement this to define their effect behavior.
     */
    protected abstract TransformDelta calculateDelta(UIElement<?> element);

    /**
     * Mark the element dirty based on what changed in the delta.
     */
    protected void markElementDirty(UIElement<?> element, TransformDelta oldDelta, TransformDelta newDelta) {
        if (!oldDelta.getPositionOffset().equals(newDelta.getPositionOffset())) {
            element.markDirty(DirtyFlag.POSITION);
        }
        if (Math.abs(oldDelta.getScaleMultiplier() - newDelta.getScaleMultiplier()) > 0.0001f) {
            element.markDirty(DirtyFlag.SIZE);
        }
        // Rotation and alpha changes also affect rendering but don't need specific dirty flags
        // They'll be picked up by the general dirty state
    }

    /**
     * Check if two deltas are approximately equal.
     */
    protected boolean deltasEqual(TransformDelta a, TransformDelta b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        float epsilon = 0.001f;
        return a.getPositionOffset().distance(b.getPositionOffset()) < epsilon
                && Math.abs(a.getScaleMultiplier() - b.getScaleMultiplier()) < epsilon
                && Math.abs(a.getRotationDeg() - b.getRotationDeg()) < epsilon
                && Math.abs(a.getAlphaMultiplier() - b.getAlphaMultiplier()) < epsilon;
    }

    /**
     * Get normalized time (0 to 1) for current position in effect.
     */
    protected float getNormalizedTime() {
        if (duration <= 0) {
            return 0f;
        }
        float t = elapsed / duration;
        if (pingPong && !forward) {
            t = 1.0f - (elapsed / duration);
        }
        return Math.max(0f, Math.min(1f, t));
    }

    // Builder-style setters
    public AbstractEffect setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public AbstractEffect setPingPong(boolean pingPong) {
        this.pingPong = pingPong;
        return this;
    }
}

