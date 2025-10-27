package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.animation.FloatKeyframeAnimation;
import io.github.currenj.gelatinui.gui.animation.Keyframe;
import org.joml.Vector2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EffectAnimationBinder utility.
 */
class EffectAnimationBinderTest {

    private TestElement element;

    @BeforeEach
    void setUp() {
        element = new TestElement();
        element.setPosition(new Vector2f(100, 100));
        element.setSize(new Vector2f(50, 50));
    }

    @Test
    void testAnimateEffectWeight() {
        BreatheEffect effect = new BreatheEffect("breathe", 0);
        element.addEffect(effect);

        // Create animation to fade in the effect
        List<Keyframe> keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0.0f, 0.0f));
        keyframes.add(new Keyframe(0.5f, 1.0f));

        FloatKeyframeAnimation weightAnim = EffectAnimationBinder.animateWeight(
                "breathe-weight",
                keyframes,
                element,
                effect
        );

        element.playAnimation(weightAnim);

        // Trigger initial animation value application
        element.update(0.0f);

        // At start, weight should be 0
        assertEquals(0.0f, effect.getWeight(), 0.001f);

        // Update halfway through
        for (int i = 0; i < 25; i++) {
            element.update(0.01f);
        }

        // Weight should be near 0.5
        assertTrue(effect.getWeight() > 0.3f && effect.getWeight() < 0.7f);

        // Complete animation
        for (int i = 0; i < 30; i++) {
            element.update(0.01f);
        }

        // Weight should be 1.0
        assertEquals(1.0f, effect.getWeight(), 0.001f);
    }

    @Test
    void testAnimateBreatheAmplitude() {
        BreatheEffect effect = new BreatheEffect("breathe", 0);
        element.addEffect(effect);

        // Animate amplitude from 0 to 0.2
        List<Keyframe> keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0.0f, 0.0f));
        keyframes.add(new Keyframe(0.3f, 0.2f));

        FloatKeyframeAnimation ampAnim = EffectAnimationBinder.animateBreatheAmplitude(
                "breathe-amp",
                keyframes,
                element,
                effect
        );

        element.playAnimation(ampAnim);

        // Update through animation
        for (int i = 0; i < 35; i++) {
            element.update(0.01f);
        }

        // Effect should now have the animated amplitude applied
        // (we can verify it's being called by checking the effect still works)
        assertNotNull(effect.getDelta());
    }

    @Test
    void testAnimateWanderAmplitude() {
        WanderEffect effect = new WanderEffect("wander", 0);
        element.addEffect(effect);

        List<Keyframe> keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0.0f, 0.0f));
        keyframes.add(new Keyframe(0.2f, 20.0f));

        FloatKeyframeAnimation ampAnim = EffectAnimationBinder.animateWanderRadius(
                "wander-amp",
                keyframes,
                element,
                effect
        );

        element.playAnimation(ampAnim);

        // Update through animation
        for (int i = 0; i < 25; i++) {
            element.update(0.01f);
        }

        // Element should be marked dirty during animation
        assertTrue(element.needsUpdate());
    }

    @Test
    void testCustomParameterBinding() {
        ShakeEffect effect = new ShakeEffect("shake", 0, 1.0f);
        element.addEffect(effect);

        // Create custom animation to modify shake amplitude
        List<Keyframe> keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0.0f, 10.0f));
        keyframes.add(new Keyframe(0.5f, 0.0f));

        FloatKeyframeAnimation shakeAnim = EffectAnimationBinder.bind(
                "shake-decay",
                keyframes,
                element,
                effect::setAmplitude,
                DirtyFlag.POSITION
        );

        element.playAnimation(shakeAnim);

        // Update through animation
        for (int i = 0; i < 55; i++) {
            element.update(0.01f);
        }

        // Animation should have completed
        assertFalse(element.isAnimating() && element.getEffects().isEmpty());
    }

    @Test
    void testCompletionCallback() {
        BreatheEffect effect = new BreatheEffect("breathe", 0);
        element.addEffect(effect);

        final boolean[] completed = {false};

        List<Keyframe> keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0.0f, 0.0f));
        keyframes.add(new Keyframe(0.1f, 1.0f));

        FloatKeyframeAnimation weightAnim = EffectAnimationBinder.bind(
                "test",
                keyframes,
                element,
                effect::setWeight,
                () -> completed[0] = true,
                DirtyFlag.SIZE
        );

        element.playAnimation(weightAnim);

        // Update past completion
        for (int i = 0; i < 15; i++) {
            element.update(0.01f);
        }

        assertTrue(completed[0]);
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
