package io.github.currenj.gelatinui.gui.effects;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.animation.FloatKeyframeAnimation;
import io.github.currenj.gelatinui.gui.animation.Keyframe;

import java.util.List;
import java.util.function.Consumer;

/**
 * Helper class to bind FloatKeyframeAnimation to effect parameters.
 * This allows driving effect properties (amplitude, frequency, weight, etc.) with keyframe animations.
 */
public class EffectAnimationBinder {

    /**
     * Create a float animation that modifies an effect parameter and marks the element dirty.
     *
     * @param channel    Animation channel for exclusivity
     * @param keyframes  Keyframes defining the animation
     * @param element    The UI element owning the effect
     * @param setter     Function to set the effect parameter
     * @param dirtyFlags Dirty flags to set when parameter changes
     * @return FloatKeyframeAnimation instance
     */
    public static FloatKeyframeAnimation bind(
            String channel,
            List<Keyframe> keyframes,
            UIElement<?> element,
            Consumer<Float> setter,
            DirtyFlag... dirtyFlags) {

        return new FloatKeyframeAnimation(
                channel,
                keyframes,
                value -> {
                    setter.accept(value);
                    if (dirtyFlags.length > 0) {
                        element.markDirty(dirtyFlags);
                    }
                },
                null
        );
    }

    /**
     * Create a float animation that modifies an effect parameter with completion callback.
     *
     * @param channel      Animation channel for exclusivity
     * @param keyframes    Keyframes defining the animation
     * @param element      The UI element owning the effect
     * @param setter       Function to set the effect parameter
     * @param onComplete   Callback when animation completes
     * @param dirtyFlags   Dirty flags to set when parameter changes
     * @return FloatKeyframeAnimation instance
     */
    public static FloatKeyframeAnimation bind(
            String channel,
            List<Keyframe> keyframes,
            UIElement<?> element,
            Consumer<Float> setter,
            Runnable onComplete,
            DirtyFlag... dirtyFlags) {

        return new FloatKeyframeAnimation(
                channel,
                keyframes,
                value -> {
                    setter.accept(value);
                    if (dirtyFlags.length > 0) {
                        element.markDirty(dirtyFlags);
                    }
                },
                onComplete
        );
    }

    /**
     * Convenience method to animate an effect's weight over time.
     *
     * @param channel   Animation channel
     * @param keyframes Weight keyframes (0.0 to 1.0)
     * @param element   The UI element
     * @param effect    The effect to modify
     * @return FloatKeyframeAnimation instance
     */
    public static FloatKeyframeAnimation animateWeight(
            String channel,
            List<Keyframe> keyframes,
            UIElement<?> element,
            AbstractEffect effect) {

        return bind(channel, keyframes, element, effect::setWeight, DirtyFlag.SIZE);
    }

    /**
     * Convenience method to animate a BreatheEffect's amplitude.
     */
    public static FloatKeyframeAnimation animateBreatheAmplitude(
            String channel,
            List<Keyframe> keyframes,
            UIElement<?> element,
            BreatheEffect effect) {

        return bind(channel, keyframes, element, effect::setAmplitude, DirtyFlag.SIZE);
    }

    /**
     * Convenience method to animate a WanderEffect's radius.
     */
    public static FloatKeyframeAnimation animateWanderRadius(
            String channel,
            List<Keyframe> keyframes,
            UIElement<?> element,
            WanderEffect effect) {

        return bind(channel, keyframes, element, effect::setRadius, DirtyFlag.POSITION);
    }

    /**
     * Convenience method to animate a ShakeEffect's amplitude.
     */
    public static FloatKeyframeAnimation animateShakeAmplitude(
            String channel,
            List<Keyframe> keyframes,
            UIElement<?> element,
            ShakeEffect effect) {

        return bind(channel, keyframes, element, effect::setAmplitude, DirtyFlag.POSITION);
    }

    /**
     * Convenience method to animate a ClickBounceEffect's amplitude.
     */
    public static FloatKeyframeAnimation animateClickBounceAmplitude(
            String channel,
            List<Keyframe> keyframes,
            UIElement<?> element,
            ClickBounceEffect effect) {

        return bind(channel, keyframes, element, effect::setAmplitude, DirtyFlag.SIZE);
    }
}
