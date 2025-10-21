package grill24.gelatinui.gui.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Drives a float value across time using keyframes and easing.
 * Each segment uses the easing specified on the end keyframe, defaulting to linear.
 */
public class FloatKeyframeAnimation implements Animation {
    private final String channel;
    private final List<Keyframe> frames;
    private final Consumer<Float> apply;
    private final Runnable onComplete;

    private float elapsed = 0f;
    private boolean started = false;

    public FloatKeyframeAnimation(String channel, List<Keyframe> keyframes, Consumer<Float> apply, Runnable onComplete) {
        this.channel = channel;
        this.apply = apply;
        this.onComplete = onComplete;
        if (keyframes == null || keyframes.isEmpty()) {
            throw new IllegalArgumentException("Keyframes must not be empty");
        }
        // Sort by time and copy
        List<Keyframe> sorted = new ArrayList<>(keyframes);
        Collections.sort(sorted, Comparator.comparingDouble(k -> k.time));
        this.frames = Collections.unmodifiableList(sorted);
    }

    public FloatKeyframeAnimation(String channel, List<Keyframe> keyframes, Consumer<Float> apply) {
        this(channel, keyframes, apply, null);
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public boolean update(float dt) {
        if (dt < 0) dt = 0;
        if (!started) {
            // Immediately apply initial value
            apply.accept(frames.get(0).value);
            started = true;
        }
        elapsed += dt;
        // If we're at or past the last keyframe, set final value and finish
        Keyframe last = frames.get(frames.size() - 1);
        if (elapsed >= last.time) {
            apply.accept(last.value);
            if (onComplete != null) onComplete.run();
            return false;
        }

        // Find current segment [i, i+1]
        int i = 0;
        while (i + 1 < frames.size() && elapsed > frames.get(i + 1).time) {
            i++;
        }
        while (i + 1 < frames.size() && elapsed < frames.get(i).time) {
            i = Math.max(0, i - 1);
        }
        // Ensure we have a next frame
        if (i + 1 >= frames.size()) {
            apply.accept(last.value);
            if (onComplete != null) onComplete.run();
            return false;
        }

        Keyframe a = frames.get(i);
        Keyframe b = frames.get(i + 1);
        float segmentStart = a.time;
        float segmentEnd = b.time;
        float duration = Math.max(1e-6f, segmentEnd - segmentStart);
        float t = (elapsed - segmentStart) / duration;
        if (t < 0f) t = 0f; else if (t > 1f) t = 1f;
        Easing.Func easing = b.easing != null ? b.easing : Easing.LINEAR;
        float et = easing.ease(t);
        float value = a.value + (b.value - a.value) * et;
        apply.accept(value);
        return true;
    }
}

