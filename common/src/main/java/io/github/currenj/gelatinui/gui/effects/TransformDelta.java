package io.github.currenj.gelatinui.gui.effects;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Immutable container for transform delta values applied by effects.
 * Represents the change/offset from the base transform.
 */
public class TransformDelta {
    public static final TransformDelta IDENTITY = new TransformDelta(
            new Vector2f(0, 0), 1.0f, 1.0f, new Vector3f(0, 0, 0)
    );

    private final Vector2f positionOffset;
    private final float scaleMultiplier;
    private final float alphaMultiplier;
    private final Vector3f rotation3D;  // 3D rotation (X, Y, Z axes in degrees)

    /**
     * Constructor with rotation support.
     * The rotation3D Vector3f contains X, Y, Z axis rotations in degrees.
     * For 2D rotation, only the Z component is used.
     */
    public TransformDelta(Vector2f positionOffset, float scaleMultiplier, float alphaMultiplier, Vector3f rotation3D) {
        this.positionOffset = new Vector2f(positionOffset);
        this.scaleMultiplier = scaleMultiplier;
        this.alphaMultiplier = alphaMultiplier;
        this.rotation3D = new Vector3f(rotation3D);
    }

    public Vector2f getPositionOffset() {
        return new Vector2f(positionOffset);
    }

    public float getScaleMultiplier() {
        return scaleMultiplier;
    }

    public float getAlphaMultiplier() {
        return alphaMultiplier;
    }

    /**
     * Get 3D rotation in degrees (X, Y, Z axes).
     * For 3D items like ItemRenderer, this provides full 3D rotation control.
     * For 2D elements, only the Z component (in-plane rotation) is used.
     */
    public Vector3f getRotation3D() {
        return new Vector3f(rotation3D);
    }

    /**
     * Check if this delta uses 3D rotation (non-zero X or Y rotation).
     * If only Z rotation is present, this is considered 2D rotation.
     */
    public boolean has3DRotation() {
        return Math.abs(rotation3D.x) > 0.001f || Math.abs(rotation3D.y) > 0.001f;
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
                // Add 3D rotation
                Vector3f newRot3D = new Vector3f(rotation3D).add(other.rotation3D);
                // Multiply alpha
                float newAlpha = alphaMultiplier * other.alphaMultiplier;
                return new TransformDelta(newPos, newScale, newAlpha, newRot3D);

            case MULTIPLY:
                // Position and rotation are added
                Vector2f multPos = new Vector2f(positionOffset).add(other.positionOffset);
                // Multiply scale and alpha
                float multScale = scaleMultiplier * other.scaleMultiplier;
                float multAlpha = alphaMultiplier * other.alphaMultiplier;
                Vector3f multRot3D = new Vector3f(rotation3D).add(other.rotation3D);
                return new TransformDelta(multPos, multScale, multAlpha, multRot3D);

            case LERP:
                // Interpolate by weight
                Vector2f lerpPos = new Vector2f(positionOffset).lerp(other.positionOffset, weight);
                float lerpScale = scaleMultiplier + (other.scaleMultiplier - scaleMultiplier) * weight;
                Vector3f lerpRot3D = new Vector3f(rotation3D).lerp(other.rotation3D, weight);
                float lerpAlpha = alphaMultiplier + (other.alphaMultiplier - alphaMultiplier) * weight;
                return new TransformDelta(lerpPos, lerpScale, lerpAlpha, lerpRot3D);

            case OVERRIDE:
                // Other completely replaces this
                return other;

            default:
                return this;
        }
    }

    @Override
    public String toString() {
        return String.format("TransformDelta{pos=(%.2f,%.2f), scale=%.2f, rot3D=(%.2f,%.2f,%.2f), alpha=%.2f}",
                positionOffset.x, positionOffset.y, scaleMultiplier, 
                rotation3D.x, rotation3D.y, rotation3D.z, alphaMultiplier);
    }
}

