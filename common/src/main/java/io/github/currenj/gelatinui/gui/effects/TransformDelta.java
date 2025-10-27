package io.github.currenj.gelatinui.gui.effects;

import org.joml.Vector2f;

/**
 * Immutable container for transform delta values applied by effects.
 * Represents the change/offset from the base transform.
 */
public class TransformDelta {
    public static final TransformDelta IDENTITY = new TransformDelta(
            new Vector2f(0, 0), 1.0f, 0.0f, 1.0f
    );

    private final Vector2f positionOffset;
    private final float scaleMultiplier;
    private final float rotationDeg;
    private final float alphaMultiplier;

    public TransformDelta(Vector2f positionOffset, float scaleMultiplier, float rotationDeg, float alphaMultiplier) {
        this.positionOffset = new Vector2f(positionOffset);
        this.scaleMultiplier = scaleMultiplier;
        this.rotationDeg = rotationDeg;
        this.alphaMultiplier = alphaMultiplier;
    }

    public Vector2f getPositionOffset() {
        return new Vector2f(positionOffset);
    }

    public float getScaleMultiplier() {
        return scaleMultiplier;
    }

    public float getRotationDeg() {
        return rotationDeg;
    }

    public float getAlphaMultiplier() {
        return alphaMultiplier;
    }

    /**
     * Combine two deltas according to a blend mode.
     * This is used to stack multiple effects.
     */
    public TransformDelta combine(TransformDelta other, BlendMode mode, float weight) {
        if (other == null || other == IDENTITY) {
            return this;
        }

        switch (mode) {
            case ADD:
                // Add position offsets
                Vector2f newPos = new Vector2f(positionOffset).add(other.positionOffset);
                // Add scale deltas: (s1-1) + (s2-1) + 1 = s1 + s2 - 1
                float newScale = scaleMultiplier + other.scaleMultiplier - 1.0f;
                // Add rotation
                float newRot = rotationDeg + other.rotationDeg;
                // Multiply alpha
                float newAlpha = alphaMultiplier * other.alphaMultiplier;
                return new TransformDelta(newPos, newScale, newRot, newAlpha);

            case MULTIPLY:
                // Position and rotation are added
                Vector2f multPos = new Vector2f(positionOffset).add(other.positionOffset);
                // Multiply scale and alpha
                float multScale = scaleMultiplier * other.scaleMultiplier;
                float multAlpha = alphaMultiplier * other.alphaMultiplier;
                float multRot = rotationDeg + other.rotationDeg;
                return new TransformDelta(multPos, multScale, multRot, multAlpha);

            case LERP:
                // Interpolate by weight
                Vector2f lerpPos = new Vector2f(positionOffset).lerp(other.positionOffset, weight);
                float lerpScale = scaleMultiplier + (other.scaleMultiplier - scaleMultiplier) * weight;
                float lerpRot = rotationDeg + (other.rotationDeg - rotationDeg) * weight;
                float lerpAlpha = alphaMultiplier + (other.alphaMultiplier - alphaMultiplier) * weight;
                return new TransformDelta(lerpPos, lerpScale, lerpRot, lerpAlpha);

            case OVERRIDE:
                // Other completely replaces this
                return other;

            default:
                return this;
        }
    }

    @Override
    public String toString() {
        return String.format("TransformDelta{pos=(%.2f,%.2f), scale=%.2f, rot=%.2f, alpha=%.2f}",
                positionOffset.x, positionOffset.y, scaleMultiplier, rotationDeg, alphaMultiplier);
    }
}

