package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;

/**
 * Fall from above and bounce into place animation.
 * Item starts above the screen, falls with gravity, and bounces to rest at the target position.
 */
public class FallBounceEffect extends AbstractEffect {
    private float fallDistance = 100f; // Starting height above target (pixels)
    private float bounceHeight = 30f; // Height of first bounce (pixels)
    private float bounceDecay = 0.5f; // Energy retained after each bounce (0-1)
    private int bounceCount = 3; // Number of bounces before settling
    private float rotation = 0f; // Optional rotation during fall (degrees)

    public FallBounceEffect() {
        this(null, 0, 1.5f); // Default 1.5 second fall and bounce
    }

    public FallBounceEffect(String channel, int priority, float duration) {
        super(null, channel, priority, BlendMode.ADD, duration);
    }

    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        
        float yOffset = 0f;
        float currentRotation = 0f;
        float squash = 1.0f;
        
        // Split animation into fall phase and bounce phase
        float fallPhase = 0.4f; // First 40% is falling
        
        if (t < fallPhase) {
            // Falling phase: accelerate down with gravity
            float fallT = t / fallPhase;
            // Ease in quad for gravity acceleration
            float easedT = fallT * fallT;
            yOffset = -fallDistance * (1f - easedT);
            
            // Optional rotation during fall
            currentRotation = rotation * fallT;
            
            // Slight stretch during fast fall
            if (fallT > 0.5f) {
                squash = 1.0f + 0.1f * (fallT - 0.5f);
            }
        } else {
            // Bouncing phase
            float bounceT = (t - fallPhase) / (1f - fallPhase);
            float bouncePhase = bounceT * bounceCount;
            int currentBounce = (int) bouncePhase;
            float localT = bouncePhase - currentBounce;
            
            if (currentBounce < bounceCount) {
                // Calculate bounce height with decay
                float currentHeight = bounceHeight * (float) Math.pow(bounceDecay, currentBounce);
                
                // Parabolic motion for this bounce
                yOffset = -currentHeight * (1f - 4f * (localT - 0.5f) * (localT - 0.5f));
                
                // Add squash/stretch on impact
                if (localT < 0.15f) {
                    // Impact squash (compress vertically)
                    float impactT = localT / 0.15f;
                    squash = 1.0f - 0.2f * (1f - impactT);
                } else if (localT > 0.85f) {
                    // Pre-impact stretch (stretch vertically before landing)
                    float preImpactT = (localT - 0.85f) / 0.15f;
                    squash = 1.0f + 0.1f * preImpactT;
                }
            } else {
                // Settled at rest position
                yOffset = 0f;
                squash = 1.0f;
            }
            
            currentRotation = rotation;
        }
        
        return new TransformDelta(new Vector2f(0, yOffset), squash, currentRotation, 1.0f);
    }

    /**
     * Set the starting distance above the target position (pixels).
     */
    public FallBounceEffect setFallDistance(float distance) {
        this.fallDistance = Math.max(0f, distance);
        return this;
    }

    /**
     * Set the height of the first bounce (pixels).
     */
    public FallBounceEffect setBounceHeight(float height) {
        this.bounceHeight = Math.max(0f, height);
        return this;
    }

    /**
     * Set the bounce decay factor (0-1).
     * Higher values = more bouncy, lower values = quickly settles.
     */
    public FallBounceEffect setBounceDecay(float decay) {
        this.bounceDecay = Math.max(0f, Math.min(1f, decay));
        return this;
    }

    /**
     * Set the number of bounces before settling.
     */
    public FallBounceEffect setBounceCount(int count) {
        this.bounceCount = Math.max(0, count);
        return this;
    }

    /**
     * Set rotation during fall (degrees).
     * The item will rotate to this angle during the fall and maintain it.
     */
    public FallBounceEffect setRotation(float degrees) {
        this.rotation = degrees;
        return this;
    }
}
