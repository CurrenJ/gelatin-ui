package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Jump up and bounce down animation with realistic physics.
 * Uses parabolic motion for the jump arc and diminishing bounces on landing.
 */
public class JumpBounceEffect extends AbstractEffect {
    private float jumpHeight = 40f; // Maximum height in pixels
    private float gravity = 200f; // Gravity strength (affects fall speed)
    private float bounceDecay = 0.6f; // Energy retained after each bounce (0-1)
    private int bounceCount = 2; // Number of bounces before settling

    public JumpBounceEffect() {
        this(null, 0, 1.2f); // Default 1.2 second animation
    }

    public JumpBounceEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.ADD, duration);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        
        float yOffset = 0f;
        float squash = 1.0f;
        
        // Physics-based jump with bounces
        if (t < 0.5f) {
            // Jump up phase (first half)
            float jumpT = t / 0.5f;
            // Parabolic motion: y = -4h(t - 0.5)^2 + h
            yOffset = -jumpHeight * (1f - 4f * (jumpT - 0.5f) * (jumpT - 0.5f));
        } else {
            // Bounce phase (second half)
            float bounceT = (t - 0.5f) / 0.5f;
            
            // Calculate which bounce we're on
            float bouncePhase = bounceT * bounceCount;
            int currentBounce = (int) bouncePhase;
            float localT = bouncePhase - currentBounce;
            
            if (currentBounce < bounceCount) {
                // Calculate bounce height with decay
                float currentHeight = jumpHeight * (float) Math.pow(bounceDecay, currentBounce + 1);
                
                // Parabolic motion for this bounce
                yOffset = -currentHeight * (1f - 4f * (localT - 0.5f) * (localT - 0.5f));
                
                // Add squash/stretch on impact
                if (localT < 0.1f) {
                    // Impact squash
                    squash = 1.0f - 0.15f * (1f - localT / 0.1f);
                } else if (localT > 0.9f) {
                    // Pre-impact squash
                    squash = 1.0f - 0.1f * ((localT - 0.9f) / 0.1f);
                }
            }
        }
        
        return new TransformDelta(new Vector2f(0, yOffset), squash, 1.0f, new org.joml.Vector3f(0, 0, 0));
    }

    /**
     * Set the maximum jump height in pixels.
     */
    public JumpBounceEffect setJumpHeight(float height) {
        this.jumpHeight = Math.max(0f, height);
        return this;
    }

    /**
     * Set the gravity strength (affects fall speed).
     */
    public JumpBounceEffect setGravity(float gravity) {
        this.gravity = Math.max(0f, gravity);
        return this;
    }

    /**
     * Set the bounce decay factor (0-1).
     * Higher values = more bouncy, lower values = quickly settles.
     */
    public JumpBounceEffect setBounceDecay(float decay) {
        this.bounceDecay = Math.max(0f, Math.min(1f, decay));
        return this;
    }

    /**
     * Set the number of bounces before settling.
     */
    public JumpBounceEffect setBounceCount(int count) {
        this.bounceCount = Math.max(0, count);
        return this;
    }
}
