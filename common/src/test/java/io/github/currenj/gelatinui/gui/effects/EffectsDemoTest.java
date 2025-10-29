package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.animation.FloatKeyframeAnimation;
import io.github.currenj.gelatinui.gui.animation.Keyframe;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example scene demonstrating the effects system in action.
 * This shows how to create interactive UI elements with various effects.
 */
class EffectsDemoTest {

    /**
     * Demonstrates a button with click bounce effect.
     */
    @Test
    void demoClickBounceButton() {
        DemoButton button = new DemoButton();
        button.setPosition(new Vector2f(100, 100));
        button.setSize(new Vector2f(80, 30));

        // Add click bounce effect
        button.addClickBounceEffect();

        // Simulate clicking the button
        button.simulateClick();

        // Update for a few frames to see the bounce
        for (int i = 0; i < 30; i++) {
            button.update(0.016f); // ~60 FPS
        }

        // Effect should have completed
        assertEquals(0, button.getEffects().size());
    }

    /**
     * Demonstrates a breathing panel with animated amplitude.
     */
    @Test
    void demoBreathingPanel() {
        DemoPanel panel = new DemoPanel();
        panel.setPosition(new Vector2f(50, 50));
        panel.setSize(new Vector2f(200, 150));

        // Add breathe effect
        BreatheEffect breathe = new BreatheEffect("breathe", 0);
        breathe.setAmplitude(0.05f);
        breathe.setFrequency(1.2f);
        panel.addEffect(breathe);

        // Animate the amplitude over time
        List<Keyframe> keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0.0f, 0.0f));    // Start subtle
        keyframes.add(new Keyframe(2.0f, 0.1f));    // Breathe more
        keyframes.add(new Keyframe(4.0f, 0.05f));   // Back to subtle

        FloatKeyframeAnimation ampAnim = EffectAnimationBinder.animateBreatheAmplitude(
                "breathe-amp",
                keyframes,
                panel,
                breathe
        );

        panel.playAnimation(ampAnim);

        // Run animation for 5 seconds
        for (int i = 0; i < 300; i++) { // 300 frames at 60 FPS = 5 seconds
            panel.update(0.016f);

            // Check that the panel is breathing
            TransformDelta delta = panel.getCombinedEffectDelta();
            assertNotNull(delta);

            // Scale should vary over time
            float scale = delta.getScaleMultiplier();
            assertTrue(scale >= 0.9f && scale <= 1.1f); // Within expected range
        }
    }

    /**
     * Demonstrates a wandering icon with shake on interaction.
     */
    @Test
    void demoWanderingIcon() {
        DemoIcon icon = new DemoIcon();
        icon.setPosition(new Vector2f(200, 200));
        icon.setSize(new Vector2f(32, 32));

        // Add subtle wander effect
        WanderEffect wander = new WanderEffect("wander", 0);
        wander.setRadius(8.0f);
        wander.setSpeed(0.4f);
        icon.addEffect(wander);

        // Simulate user interaction - add shake effect
        ShakeEffect shake = new ShakeEffect("shake", 10, 0.3f); // Higher priority
        shake.setAmplitude(4.0f);
        shake.setFrequency(25.0f);
        icon.addEffectExclusive(shake); // Exclusive on shake channel

        // Update for 1 second to see both effects
        for (int i = 0; i < 60; i++) {
            icon.update(0.016f);

            TransformDelta delta = icon.getCombinedEffectDelta();
            assertNotNull(delta);

            // Should have both position offset (from wander) and possible shake
            Vector2f posOffset = delta.getPositionOffset();
            assertNotNull(posOffset);
        }

        // Shake should complete, wander should continue
        assertEquals(1, icon.getEffects().size()); // Only wander remains
        assertEquals("wander", icon.getEffects().get(0).getChannel());
    }

    /**
     * Demonstrates stacking multiple effects with different priorities.
     */
    @Test
    void demoStackedEffects() {
        DemoElement element = new DemoElement();
        element.setPosition(new Vector2f(150, 150));
        element.setSize(new Vector2f(100, 100));

        // Add multiple effects with different priorities
        BreatheEffect breathe = new BreatheEffect("breathe", 0); // Priority 0
        breathe.setAmplitude(0.03f);
        element.addEffect(breathe);

        WanderEffect wander = new WanderEffect("wander", 5); // Priority 5
        wander.setRadius(5.0f);
        element.addEffect(wander);

        DriftEffect drift = new DriftEffect("drift", 10, 2.0f); // Priority 10, 2 second duration
        drift.setVelocity(new Vector2f(20, -10));
        element.addEffect(drift);

        // Update for 3 seconds
        for (int i = 0; i < 180; i++) {
            element.update(0.016f);

            TransformDelta delta = element.getCombinedEffectDelta();
            assertNotNull(delta);

            // All effects should be combined
            // - Breathe affects scale
            // - Wander affects position
            // - Drift affects position (higher priority)
            assertNotEquals(1.0f, delta.getScaleMultiplier());
            assertNotEquals(new Vector2f(0, 0), delta.getPositionOffset());
        }

        // Drift should complete, others should continue
        assertEquals(2, element.getEffects().size()); // Breathe and wander remain
    }

    /**
     * Demonstrates effect parameter animation with completion callbacks.
     */
    @Test
    void demoAnimatedEffectParameters() {
        DemoElement element = new DemoElement();
        element.setPosition(new Vector2f(100, 100));
        element.setSize(new Vector2f(60, 60));

        // Add shake effect
        ShakeEffect shake = new ShakeEffect("shake", 0, 3.0f);
        shake.setAmplitude(8.0f);
        element.addEffect(shake);

        // Animate shake amplitude to create a decay effect
        List<Keyframe> keyframes = new ArrayList<>();
        keyframes.add(new Keyframe(0.0f, 8.0f));    // Start strong
        keyframes.add(new Keyframe(1.5f, 4.0f));    // Half intensity
        keyframes.add(new Keyframe(3.0f, 0.0f));    // Fade out

        final boolean[] animationCompleted = {false};

        FloatKeyframeAnimation decayAnim = EffectAnimationBinder.bind(
                "shake-decay",
                keyframes,
                element,
                shake::setAmplitude,
                () -> animationCompleted[0] = true,
                DirtyFlag.POSITION
        );

        element.playAnimation(decayAnim);

        // Run for 4 seconds
        for (int i = 0; i < 240; i++) {
            element.update(0.016f);

            // Element should be marked dirty during animation
            if (i < 180) { // First 3 seconds
                assertTrue(element.needsUpdate());
            }
        }

        // Animation should have completed
        assertTrue(animationCompleted[0]);
        assertFalse(element.isAnimating());
    }

    /**
     * Demonstrates channel exclusivity and effect replacement.
     */
    @Test
    void demoChannelExclusivity() {
        DemoElement element = new DemoElement();
        element.setPosition(new Vector2f(200, 200));
        element.setSize(new Vector2f(40, 40));

        // Add initial effect on "interaction" channel
        ShakeEffect initialShake = new ShakeEffect("interaction", 0, 0.5f);
        initialShake.setAmplitude(6.0f);
        element.addEffect(initialShake);

        assertEquals(1, element.getEffects().size());

        // Add another effect on same channel - should replace the first
        BreatheEffect breathe = new BreatheEffect("interaction", 0);
        breathe.setAmplitude(0.08f);
        element.addEffectExclusive(breathe);

        // First effect should be cancelled
        assertTrue(initialShake.isCancelled());
        assertEquals(1, element.getEffects().size());
        assertEquals("interaction", element.getEffects().get(0).getChannel());

        // Update to see the new effect working
        for (int i = 0; i < 30; i++) {
            element.update(0.016f);
        }

        TransformDelta delta = element.getCombinedEffectDelta();
        assertNotEquals(1.0f, delta.getScaleMultiplier()); // Breathe effect active
    }

    // ===== Demo UI Element Classes =====

    /**
     * Demo button with click handling.
     */
    private static class DemoButton extends UIElement<DemoButton> {
        private boolean wasClicked = false;

        @Override
        protected void onUpdate(float deltaTime) {
            // Handle click bounce completion
            if (wasClicked && getEffects().isEmpty()) {
                wasClicked = false;
            }
        }

        @Override
        protected void renderSelf(IRenderContext context) {
            // Render button background
            context.fill(0, 0, (int)size.x, (int)size.y, 0xFF444444);
        }

        @Override
        protected DemoButton self() {
            return this;
        }

        public void simulateClick() {
            wasClicked = true;
            // Trigger click bounce
            addClickBounceEffect();
        }
    }

    /**
     * Demo panel with background.
     */
    private static class DemoPanel extends UIElement<DemoPanel> {
        @Override
        protected void onUpdate(float deltaTime) {
            // Panel-specific updates if needed
        }

        @Override
        protected void renderSelf(IRenderContext context) {
            // Render panel background
            context.fill(0, 0, (int)size.x, (int)size.y, 0xFF666666);
        }

        @Override
        protected DemoPanel self() {
            return this;
        }
    }

    /**
     * Demo icon element.
     */
    private static class DemoIcon extends UIElement<DemoIcon> {
        @Override
        protected void onUpdate(float deltaTime) {
            // Icon-specific updates if needed
        }

        @Override
        protected void renderSelf(IRenderContext context) {
            // Render icon as a small square
            context.fill(0, 0, (int)size.x, (int)size.y, 0xFF888888);
        }

        @Override
        protected DemoIcon self() {
            return this;
        }
    }

    /**
     * Generic demo element.
     */
    private static class DemoElement extends UIElement<DemoElement> {
        @Override
        protected void onUpdate(float deltaTime) {
            // Generic element updates
        }

        @Override
        protected void renderSelf(IRenderContext context) {
            // Render as a colored rectangle
            context.fill(0, 0, (int)size.x, (int)size.y, 0xFFAAAAAA);
        }

        @Override
        protected DemoElement self() {
            return this;
        }
    }
}

