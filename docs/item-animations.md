# Item Animations

This document describes the reusable item animations available in Gelatin UI.

## Overview

Gelatin UI provides a collection of pre-built animation effects specifically designed for UI item components. These animations can be triggered on demand and support modification of pose properties including scale, position, rotation, and alpha.

## Available Animations

### 1. Coin Spin Effect

A flashy coin spin animation that rotates the item while varying its scale to simulate a 3D coin flip.

**Usage:**
```java
itemRenderer.addCoinSpinEffect();

// Or with custom parameters:
CoinSpinEffect effect = new CoinSpinEffect("coin-spin", 0, 1.0f);
effect.setRotationSpeed(720f); // Degrees per second
effect.setScaleAmplitude(0.3f); // 3D depth effect
effect.setGlowPulse(0.15f);     // Flashy glow intensity
itemRenderer.addEffect(effect);
```

**Features:**
- Rotates while simulating 3D depth through scale variation
- Alpha pulsing for flashy visual effect
- Configurable rotation speed, scale amplitude, and glow intensity

---

### 2. Jump Bounce Effect

A realistic jump and bounce animation with physics-based motion and multiple bounces.

**Usage:**
```java
itemRenderer.addJumpBounceEffect();

// Or with custom parameters:
JumpBounceEffect effect = new JumpBounceEffect("jump-bounce", 0, 1.2f);
effect.setJumpHeight(40f);        // Maximum height in pixels
effect.setBounceDecay(0.6f);      // Energy retained per bounce
effect.setBounceCount(2);         // Number of bounces
itemRenderer.addEffect(effect);
```

**Features:**
- Parabolic motion for natural jump arc
- Multiple diminishing bounces
- Squash and stretch effects on impact
- Realistic physics simulation

---

### 3. Spin Effect

A clean rotation animation for spinning items. Can be configured for single spin or continuous looping.

**Usage:**
```java
itemRenderer.addSpinEffect();

// Continuous spinning:
SpinEffect effect = SpinEffect.continuous(1.5f); // 1.5 rotations per second
itemRenderer.addEffect(effect);

// Single spin with easing:
SpinEffect effect = new SpinEffect("spin", 0, 1.0f);
effect.setRotationSpeed(360f);    // 360 degrees per second
effect.setClockwise(true);        // Direction
effect.setEasingPower(2.0f);      // Ease out for smooth deceleration
itemRenderer.addEffect(effect);
```

**Features:**
- Configurable speed and direction
- Easing support for smooth acceleration/deceleration
- Can be one-shot or continuous

---

### 4. Flip Effect

A card-flip style animation that simulates a 3D flip by varying scale and rotation.

**Usage:**
```java
itemRenderer.addFlipEffect();

// Or with custom parameters:
FlipEffect effect = new FlipEffect("flip", 0, 0.6f);
effect.setFlipRotation(180f);              // Degrees to flip
effect.setFlipAxis(FlipEffect.Axis.Y);     // Flip axis (X, Y, or Z)
effect.setPerspectiveScale(0.1f);          // Scale at edge view
itemRenderer.addEffect(effect);
```

**Features:**
- Simulates 3D card flip with 2D transformations
- Configurable flip axis (horizontal, vertical, or in-plane)
- Adjustable perspective depth
- Smooth easing for natural motion

---

### 5. Fall Bounce Effect

An entry animation where the item falls from above the screen and bounces into place.

**Usage:**
```java
itemRenderer.addFallBounceEffect();

// Or with custom parameters:
FallBounceEffect effect = new FallBounceEffect("fall-bounce", 0, 1.5f);
effect.setFallDistance(100f);     // Starting height above target
effect.setBounceHeight(30f);      // First bounce height
effect.setBounceDecay(0.5f);      // Energy retained per bounce
effect.setBounceCount(3);         // Number of bounces
effect.setRotation(45f);          // Optional rotation during fall
itemRenderer.addEffect(effect);
```

**Features:**
- Falls with gravity acceleration
- Multiple diminishing bounces on landing
- Squash and stretch effects
- Optional rotation during fall

---

### 6. Pulse Glow Effect

A unique creative animation that combines pulsing scale with glow effects, perfect for highlighting rare or special items.

**Usage:**
```java
itemRenderer.addPulseGlowEffect();

// One-shot pulse:
PulseGlowEffect effect = PulseGlowEffect.oneShot(2.0f); // 2 second pulse
itemRenderer.addEffect(effect);

// Continuous with custom parameters:
PulseGlowEffect effect = new PulseGlowEffect("pulse-glow", 0);
effect.setScaleAmplitude(0.15f);   // Scale pulse amount
effect.setGlowIntensity(0.4f);     // Alpha variation
effect.setPulseFrequency(2.0f);    // Pulses per second
effect.setRotationSpeed(20f);      // Optional rotation
effect.setEnableRotation(true);    // Toggle rotation
itemRenderer.addEffect(effect);
```

**Features:**
- Rhythmic scale pulsing
- Phase-shifted glow effect for "breathing light"
- Optional subtle rotation
- Can be continuous or one-shot
- Perfect for rare/magical items

---

## General Usage Patterns

### Convenience Methods

All animations have convenience methods on `UIElement` for quick access:

```java
element.addCoinSpinEffect();
element.addJumpBounceEffect();
element.addSpinEffect();
element.addFlipEffect();
element.addFallBounceEffect();
element.addPulseGlowEffect();
```

### Custom Configuration

For more control, create effect instances directly:

```java
CoinSpinEffect spin = new CoinSpinEffect("my-spin", 0, 2.0f);
spin.setRotationSpeed(1080f);  // 3 full rotations
spin.setScaleAmplitude(0.5f);  // More pronounced 3D effect
element.addEffect(spin);
```

### Stacking Effects

Effects can be stacked and will combine according to their blend modes:

```java
element.addPulseGlowEffect();  // Continuous glow
element.addCoinSpinEffect();   // Trigger coin spin on top
```

### Effect Channels

Use channels to prevent conflicts between similar effects:

```java
// These will replace each other due to same channel
element.addCoinSpinEffect();  // Uses "coin-spin" channel
element.addCoinSpinEffect();  // Replaces the previous one

// Different channels can coexist
CoinSpinEffect spin1 = new CoinSpinEffect("spin-1", 0, 1.0f);
CoinSpinEffect spin2 = new CoinSpinEffect("spin-2", 0, 1.0f);
element.addEffect(spin1);
element.addEffect(spin2);  // Both will run
```

## Examples

### Rare Item Pickup Animation

```java
// Dramatic entrance for a rare item
itemRenderer.addFallBounceEffect();
// Then continuous highlight
itemRenderer.addPulseGlowEffect();
```

### Item Collection Feedback

```java
// Quick satisfying feedback when collecting an item
itemRenderer.addCoinSpinEffect();
```

### UI Item Selection

```java
// Flip to reveal item details
itemRenderer.addFlipEffect();
```

### Playful Item Display

```java
// Bouncy, energetic presentation
itemRenderer.addJumpBounceEffect();
```

## Technical Details

All effects:
- Extend `AbstractEffect` for consistent behavior
- Use `TransformDelta` to modify pose properties
- Support looping, ping-pong, and one-shot modes
- Can be cancelled early via `cancel()`
- Have configurable priorities for layering
- Support multiple blend modes (ADD, MULTIPLY, LERP, OVERRIDE)

Effects modify:
- **Position**: Offset from base position (x, y in pixels)
- **Scale**: Multiplier for size (1.0 = normal)
- **Rotation**: Angle in degrees
- **Alpha**: Opacity multiplier (0.0 - 1.0+)

## Interactive Demo

Try the **Item Animations Test Screen** to see all effects in action:
- Access via command: `/gelatinui example/item_animations`
- Features 6 item renderers with different items
- Individual buttons for each animation type
- "Trigger All", "Clear All", and "Advanced Demo" buttons
- Status feedback for each action

The demo screen showcases true 3D rotation on actual ItemRenderer components with various Minecraft items (Gold Ingot, Diamond, Emerald, Iron Ingot, Netherite Ingot, Nether Star).

## See Also

- `ItemAnimationsTestScreen.java` - Dedicated demo screen for item animations
- `EffectsTestScreen.java` - Interactive demo of all effects
- `AbstractEffect.java` - Base class for creating custom effects
- `TransformDelta.java` - Transform modification container
