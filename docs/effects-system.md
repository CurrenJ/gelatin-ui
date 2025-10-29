# Effects System API Documentation

## Overview

The Effects System allows multiple local transform effects (scale, position, rotation, alpha) to be active on a single `UIElement` and combined each frame to produce a final transform before rendering.

## Core Components

### Effect Interface
Base interface for all effects with methods for lifecycle management, transform delta calculation, and priority/blending control.

### TransformDelta
Immutable container for transform changes with:
- Position offset (Vector2f)
- Scale multiplier (float)
- Rotation degrees (float)
- Alpha multiplier (float)

### BlendMode
Defines how effects combine:
- **ADD**: Add position/rotation, add scale deltas, multiply alpha
- **MULTIPLY**: Multiply scale/alpha, add position/rotation
- **OVERRIDE**: Highest priority overrides
- **LERP**: Interpolate by effect weight

## Built-in Effects

### ClickBounceEffect
Quick scale bounce animation similar to button press feedback.

```java
element.addClickBounceEffect();
// Or with custom channel/priority:
element.addEffect(new ClickBounceEffect("my-bounce", 0));
```

### BreatheEffect
Slow oscillating scale creating a breathing animation.

```java
BreatheEffect breathe = new BreatheEffect("breathe", 0);
breathe.setAmplitude(0.08f);  // 8% scale variation
breathe.setFrequency(1.0f);   // 1 cycle per second
element.addEffect(breathe);
```

### WanderEffect
Smooth position drift using sine waves.

```java
WanderEffect wander = new WanderEffect("wander", 0);
wander.setAmplitude(10.0f);   // Max 10 pixels drift
wander.setFrequency(0.5f);    // 0.5 cycles per second
element.addEffect(wander);
```

### ShakeEffect
Rapid position jitter for shake animations.

```java
ShakeEffect shake = new ShakeEffect("shake", 0, 0.5f); // 0.5 second duration
shake.setAmplitude(5.0f);     // 5 pixel shake radius
shake.setFrequency(30.0f);    // 30 updates per second
shake.setDecay(0.95f);        // Amplitude decay rate
element.addEffect(shake);
```

### DriftEffect
Smooth directional movement.

```java
DriftEffect drift = new DriftEffect("drift", 0, 2.0f); // 2 second duration
drift.setVelocity(new Vector2f(50, -20)); // Pixels per second
element.addEffect(drift);
```

## Basic Usage

### Adding Effects

```java
// Add single effect
element.addEffect(new BreatheEffect("breathe", 0));

// Add with channel exclusivity (cancels previous effects on same channel)
element.addEffectExclusive(new ClickBounceEffect("bounce", 0));

// Convenience methods
element.addBreatheEffect();
element.addWanderEffect();
element.addClickBounceEffect();
```

### Removing Effects

```java
// Remove by ID
element.removeEffect("effect-id");

// Cancel all effects on a channel
element.cancelEffectChannel("bounce");

// Clear all effects
element.clearEffects();
```

### Stacking Effects

Multiple effects can be active simultaneously and are combined based on their blend modes and priorities:

```java
// Add breathe (affects scale) and wander (affects position)
element.addBreatheEffect();
element.addWanderEffect();

// Both effects will be active and combined each frame
```

## Advanced Usage

### Effect Lifecycle

```java
AbstractEffect effect = new BreatheEffect("breathe", 0);

// Set looping (for infinite effects)
effect.setLoop(true);

// Set ping-pong (reverse at boundaries)
effect.setPingPong(true);

// Set weight/intensity
effect.setWeight(0.5f); // 50% intensity

element.addEffect(effect);
```

### Priority and Ordering

Effects are combined in priority order (lower first):

```java
BreatheEffect low = new BreatheEffect("low", 0);   // Priority 0
BreatheEffect high = new BreatheEffect("high", 10); // Priority 10

element.addEffect(low);
element.addEffect(high);
// 'high' will be applied after 'low'
```

### Channel Exclusivity

Like animations, effects can use channels for mutual exclusivity:

```java
// Adding a new effect with the same channel cancels the previous one
element.addEffectExclusive(new ClickBounceEffect("bounce", 0));
// ...later
element.addEffectExclusive(new ClickBounceEffect("bounce", 0)); // Cancels previous
```

## Animating Effect Parameters

Use `EffectAnimationBinder` to drive effect parameters with keyframe animations:

### Animate Effect Weight

```java
BreatheEffect effect = new BreatheEffect("breathe", 0);
element.addEffect(effect);

List<Keyframe> keyframes = Arrays.asList(
    new Keyframe(0.0f, 0.0f),  // Start at 0% weight
    new Keyframe(1.0f, 1.0f)   // End at 100% weight
);

FloatKeyframeAnimation fadeIn = EffectAnimationBinder.animateWeight(
    "breathe-fade",
    keyframes,
    element,
    effect
);

element.playAnimation(fadeIn);
```

### Animate Effect Amplitude

```java
ShakeEffect shake = new ShakeEffect("shake", 0, 2.0f);
element.addEffect(shake);

List<Keyframe> keyframes = Arrays.asList(
    new Keyframe(0.0f, 10.0f),  // Start strong
    new Keyframe(2.0f, 0.0f)    // Decay to nothing
);

FloatKeyframeAnimation decay = EffectAnimationBinder.animateShakeAmplitude(
    "shake-decay",
    keyframes,
    element,
    shake
);

element.playAnimation(decay);
```

### Custom Parameter Binding

```java
MyCustomEffect effect = new MyCustomEffect();
element.addEffect(effect);

FloatKeyframeAnimation anim = EffectAnimationBinder.bind(
    "custom-anim",
    keyframes,
    element,
    effect::setCustomParameter,  // Setter
    () -> { /* completion callback */ },
    DirtyFlag.POSITION, DirtyFlag.SIZE  // Dirty flags to set
);

element.playAnimation(anim);
```

## Integration with Rendering

Effects are automatically applied during rendering:

1. Effects are updated each frame
2. Transform deltas are combined by priority and blend mode
3. Effective position and scale are calculated
4. Rendering applies the combined transform

```java
// Get effective values (base + effects)
Vector2f effectivePos = element.getEffectivePosition();
float effectiveScale = element.getEffectiveScale();

// Get combined effect delta
TransformDelta delta = element.getCombinedEffectDelta();
```

## Creating Custom Effects

Extend `AbstractEffect` to create custom effects:

```java
public class PulseEffect extends AbstractEffect {
    private float pulseSpeed = 2.0f;
    
    public PulseEffect(String channel, int priority) {
        super(null, channel, priority, BlendMode.MULTIPLY, -1f);
        this.loop = true;
    }
    
    @Override
    protected TransformDelta calculateDelta(UIElement<?> element) {
        float t = getNormalizedTime();
        float pulse = (float) Math.sin(t * pulseSpeed * Math.PI * 2);
        float scale = 1.0f + pulse * 0.1f;
        
        return new TransformDelta(
            new Vector2f(0, 0),  // No position change
            scale,               // Pulsing scale
            0f,                  // No rotation
            1.0f                 // No alpha change
        );
    }
    
    public void setPulseSpeed(float speed) {
        this.pulseSpeed = speed;
    }
}
```

## Performance Considerations

- Effects are only updated when element needs update
- Finished effects are automatically removed
- Effect combination is deterministic and efficient
- Use priority to control combination order
- Channel exclusivity prevents effect accumulation

## Example: Button with Click Feedback

```java
Button button = new Button("Click Me");

// Add hover breathe effect
button.onMouseEnter(e -> {
    BreatheEffect breathe = new BreatheEffect("hover", 0);
    breathe.setAmplitude(0.05f);
    button.addEffectExclusive(breathe);
});

button.onMouseExit(e -> {
    button.cancelEffectChannel("hover");
});

// Add click bounce
button.onClick(e -> {
    button.addClickBounceEffect();
});
```

## Example: Stacking Multiple Effects

```java
UIElement element = new Panel();

// Background breathe
BreatheEffect breathe = new BreatheEffect("breathe", 0);
breathe.setAmplitude(0.03f);
breathe.setFrequency(0.8f);
element.addEffect(breathe);

// Subtle wander
WanderEffect wander = new WanderEffect("wander", 1);
wander.setAmplitude(3.0f);
wander.setFrequency(0.3f);
element.addEffect(wander);

// Both effects will combine each frame:
// - Breathe affects scale (multiply blend)
// - Wander affects position (add blend)
```

## API Reference

### UIElement Methods

```java
// Add/remove effects
T addEffect(Effect effect)
T addEffectExclusive(Effect effect)
T removeEffect(String effectId)
T cancelEffectChannel(String channel)
T clearEffects()

// Query effects
List<Effect> getEffects()
TransformDelta getCombinedEffectDelta()
Vector2f getEffectivePosition()
float getEffectiveScale()

// Convenience methods
T addClickBounceEffect()
T addBreatheEffect()
T addWanderEffect()
```

### Effect Interface

```java
String getId()
String getChannel()
int getPriority()
BlendMode getBlendMode()
float getWeight()
boolean update(float deltaTime, UIElement<?> element)
TransformDelta getDelta()
void cancel()
boolean isCancelled()
```

### AbstractEffect Methods

```java
void setWeight(float weight)
AbstractEffect setLoop(boolean loop)
AbstractEffect setPingPong(boolean pingPong)
float getNormalizedTime()  // 0 to 1 for current position
```

