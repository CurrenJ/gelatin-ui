# Animations

Built-in interpolation
- Position: setTargetPosition(Vector2f, boolean animate). When animate = true, UIElement interpolates position toward the target smoothly. Use getTargetPosition() to inspect the current target.
- Scale: setTargetScale(float, boolean animate). UIElement interpolates currentScale toward targetScale.
- effectScale: An extra transient scale used for feedback like click bounce; combined with currentScale for rendering and bounds.

Keyframe animation system
- UIElement maintains a list of Animation instances.
- FloatKeyframeAnimation drives a float value over time using Keyframe(time, value, easing) and a Consumer<Float> apply callback.
- Channels: Each animation has a channel string (e.g., "effectScale"). Adding a new animation on the same channel cancels the previous one, ensuring exclusivity.
- Easing: Use Easing.LINEAR, EASE_OUT_CUBIC, EASE_IN_OUT_CUBIC, EASE_OUT_BACK, or your own via Easing.easeOutBack(k).

Click bounce
- UIElement.playClickBounce() demonstrates a channelled FloatKeyframeAnimation that animates effectScale with an overshoot.

Implement your own
- Build keyframes, then call element.playAnimation(new FloatKeyframeAnimation("myChannel", frames, value -> { /* apply */ }, () -> { /* onComplete */ }));
- You can cancel specific channels via cancelAnimationChannel("myChannel") or clear all with clearAnimations().

Performance tips
- Animations keep an element updating until they finish (needsUpdate returns true while animating), so avoid long-lived no-op animations.

