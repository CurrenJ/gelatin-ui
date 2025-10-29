package io.github.currenj.gelatinui.gui.animation;

/**
 * A single keyframe for a float animation.
 */
public class Keyframe {
    public final float time;   // seconds from animation start
    public final float value;  // target value at this keyframe
    public final Easing.Func easing; // easing to use for segment ending at this keyframe (may be null)

    public Keyframe(float time, float value) {
        this(time, value, null);
    }

    public Keyframe(float time, float value, Easing.Func easing) {
        this.time = time;
        this.value = value;
        this.easing = easing;
    }
}

