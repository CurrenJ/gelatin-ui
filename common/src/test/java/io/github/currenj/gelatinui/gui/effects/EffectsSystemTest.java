package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the effects system integration with UIElement.
 */
class EffectsSystemTest {

    private TestElement element;

    @BeforeEach
    void setUp() {
        element = new TestElement();
        element.setPosition(new Vector2f(100, 100));
        element.setSize(new Vector2f(50, 50));
    }

    @Test
    void testAddEffect() {
        BreatheEffect effect = new BreatheEffect("breathe", 0);
        element.addEffect(effect);

        assertEquals(1, element.getEffects().size());
        assertTrue(element.isAnimating());
    }

    @Test
    void testEffectChannelExclusivity() {
        BreatheEffect effect1 = new BreatheEffect("test-channel", 0);
        BreatheEffect effect2 = new BreatheEffect("test-channel", 0);

        element.addEffectExclusive(effect1);
        assertEquals(1, element.getEffects().size());

        element.addEffectExclusive(effect2);
        assertEquals(1, element.getEffects().size());
        assertTrue(effect1.isCancelled());
    }

    @Test
    void testMultipleEffectsStacking() {
        // Add breathe (scale) and wander (position) effects
        BreatheEffect breathe = new BreatheEffect("breathe", 0);
        WanderEffect wander = new WanderEffect("wander", 0);

        element.addEffect(breathe);
        element.addEffect(wander);

        assertEquals(2, element.getEffects().size());

        // Update effects
        element.update(0.1f);

        TransformDelta delta = element.getCombinedEffectDelta();
        assertNotNull(delta);

        // Breathe should affect scale
        assertNotEquals(1.0f, delta.getScaleMultiplier(), 0.001f);

        // Wander should affect position
        Vector2f posOffset = delta.getPositionOffset();
        assertTrue(posOffset.x != 0 || posOffset.y != 0);
    }

    @Test
    void testEffectLifecycle() {
        // Create a short-duration shake effect
        ShakeEffect shake = new ShakeEffect("shake", 0, 0.2f);
        element.addEffect(shake);

        assertTrue(element.isAnimating());

        // Update for longer than duration
        for (int i = 0; i < 30; i++) {
            element.update(0.01f);
        }

        // Effect should be removed after completion
        assertEquals(0, element.getEffects().size());
    }

    @Test
    void testCancelEffect() {
        BreatheEffect effect = new BreatheEffect("breathe", 0);
        element.addEffect(effect);

        assertEquals(1, element.getEffects().size());

        effect.cancel();
        element.update(0.01f);

        assertTrue(effect.isCancelled());
        assertEquals(0, element.getEffects().size());
    }

    @Test
    void testClearEffects() {
        element.addEffect(new BreatheEffect("breathe", 0));
        element.addEffect(new WanderEffect("wander", 0));

        assertEquals(2, element.getEffects().size());

        element.clearEffects();

        assertEquals(0, element.getEffects().size());
        assertEquals(TransformDelta.IDENTITY, element.getCombinedEffectDelta());
    }

    @Test
    void testEffectPriority() {
        // Create effects with different priorities
        BreatheEffect low = new BreatheEffect("breathe-low", 0);
        BreatheEffect high = new BreatheEffect("breathe-high", 10);

        element.addEffect(low);
        element.addEffect(high);

        element.update(0.1f);

        // Higher priority effect should be applied last
        // Both affect scale, so we should see combined result
        TransformDelta delta = element.getCombinedEffectDelta();
        assertNotNull(delta);
    }

    @Test
    void testBlendModeMultiply() {
        // Create two breathe effects (multiply blend mode)
        BreatheEffect breathe1 = new BreatheEffect("breathe1", 0);
        breathe1.setAmplitude(0.1f);
        BreatheEffect breathe2 = new BreatheEffect("breathe2", 1);
        breathe2.setAmplitude(0.1f);

        element.addEffect(breathe1);
        element.addEffect(breathe2);

        element.update(0.1f);

        // With multiply blend mode, scales should multiply
        TransformDelta delta = element.getCombinedEffectDelta();
        assertNotNull(delta);
    }

    @Test
    void testEffectivePositionAndScale() {
        // Add effects that modify position and scale
        WanderEffect wander = new WanderEffect("wander", 0);
        wander.setRadius(10f);
        BreatheEffect breathe = new BreatheEffect("breathe", 0);
        breathe.setAmplitude(0.2f);

        element.addEffect(wander);
        element.addEffect(breathe);

        element.update(0.1f);

        // Get effective values
        Vector2f effectivePos = element.getEffectivePosition();
        float effectiveScale = element.getEffectiveScale();

        // Effective position should include wander offset
        Vector2f basePos = element.getPosition();
        assertNotEquals(basePos, effectivePos);

        // Effective scale should include breathe multiplier
        assertNotEquals(1.0f, effectiveScale, 0.001f);
    }

    @Test
    void testDirtyFlagsOnEffectChange() {
        // Reset element dirty state
        element.update(0.01f);

        // Add effect that affects position
        WanderEffect wander = new WanderEffect("wander", 0);
        element.addEffect(wander);

        // Element should be marked dirty
        assertTrue(element.needsUpdate());
    }

    @Test
    void testClickBounceEffect() {
        element.addClickBounceEffect();

        assertEquals(1, element.getEffects().size());

        // Update through the bounce duration
        for (int i = 0; i < 25; i++) {
            element.update(0.01f);
        }

        // Effect should complete and be removed
        assertEquals(0, element.getEffects().size());
    }

    @Test
    void testConvenienceEffectMethods() {
        // Test convenience methods
        element.addBreatheEffect();
        assertEquals(1, element.getEffects().size());

        element.addWanderEffect();
        assertEquals(2, element.getEffects().size());

        element.addClickBounceEffect();
        assertEquals(3, element.getEffects().size());
    }

    /**
     * Simple test UIElement implementation.
     */
    private static class TestElement extends UIElement<TestElement> {
        @Override
        protected void onUpdate(float deltaTime) {
            // No-op for test
        }

        @Override
        protected void renderSelf(IRenderContext context) {
            // No-op for test
        }

        @Override
        protected TestElement self() {
            return this;
        }
    }
}

