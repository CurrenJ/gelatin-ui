# Gelatin UI Particle System

## Overview

The Gelatin UI Particle System is a performant 2D particle effect framework that enables creating various visual effects within UI screens. It supports both sprite-based and 3D itemstack rendering with full physics simulation.

## Key Features

- **Time-based Property Interpolation**: Particles can smoothly transition between different visual states over their lifetime
  - Scale (start scale → end scale)
  - Alpha/Opacity (fade in/out)
  - Color (gradient transitions)

- **Physics Simulation**:
  - Velocity (X/Y movement per second)
  - Angular velocity (3D rotation for items, 2D for sprites)
  - Gravity (acceleration applied to velocity)

- **Flexible Rendering**:
  - **Sprite Mode**: Render particles as 2D textures
  - **ItemStack Mode**: Render particles as 3D Minecraft items with full rotation support

- **Performance Optimizations**:
  - Object pooling for particle reuse
  - Automatic culling of dead particles
  - Configurable maximum particle count

## Basic Usage

### Creating a Particle System

```java
// Create particle system with default size
ParticleSystem particleSystem = UI.particleSystem();

// Or with custom size
ParticleSystem particleSystem = UI.particleSystem(400, 300);

// Set maximum particles
particleSystem.setMaxParticles(500);

// Add to your UI
container.addChild(particleSystem);
```

### Emitting Particles

```java
// Create an emitter configuration
ParticleEmitter emitter = new ParticleEmitter()
    .setPosition(centerX, centerY)
    .setVelocity(0, -100)              // Initial velocity
    .setVelocityRange(50, 25)          // Random variation
    .setGravity(0, 200)                // Gravity acceleration
    .setAngularVelocity(0, 360, 0)     // Rotation (X, Y, Z axes)
    .setScale(2.0f, 0.5f)              // Scale from 2.0 to 0.5 over lifetime
    .setAlpha(1.0f, 0.0f)              // Fade out
    .setLifetime(2.0f)                 // 2 seconds lifetime
    .setItemStack(new ItemStack(Items.DIAMOND, 1));

// Emit particles
particleSystem.emit(emitter, 50);  // Emit 50 particles
```

### Gravity-Affected Burst Example

```java
// Quick helper for gravity bursts
ParticleEmitter gravityBurst = ParticleEmitter.gravityBurst(
    x, y,           // Position
    150,            // Upward speed
    100,            // Sideways speed
    200             // Gravity
);

particleSystem.emit(gravityBurst, 30);
```

## Particle Properties

### Position and Movement
- `setPosition(x, y)` - Starting position
- `setPositionRange(rangeX, rangeY)` - Random position offset
- `setVelocity(x, y)` - Initial velocity (pixels per second)
- `setVelocityRange(rangeX, rangeY)` - Random velocity variation

### Rotation
- `setAngularVelocity(x, y, z)` - Rotation speed (degrees per second)
  - X: Pitch (up/down rotation)
  - Y: Yaw (left/right rotation) - primary axis for coin-spin effects
  - Z: Roll (2D in-plane rotation)
- `setAngularVelocityRange(rangeX, rangeY, rangeZ)` - Random rotation variation

### Visual Properties
- `setScale(start, end)` - Scale interpolation
- `setScaleRange(range)` - Random scale variation
- `setAlpha(start, end)` - Opacity interpolation (0.0 to 1.0)
- `setColor(startColor, endColor)` - Color interpolation (ARGB format)

### Physics
- `setGravity(x, y)` - Gravity acceleration (pixels per second squared)
- `setLifetime(seconds)` - How long particle lives
- `setLifetimeRange(range)` - Random lifetime variation

### Rendering
- `setSpriteTexture(ResourceLocation)` - Use sprite rendering
- `setItemStack(ItemStack)` - Use 3D item rendering

## Advanced Features

### Custom Particle Initialization

```java
ParticleEmitter emitter = new ParticleEmitter()
    .setCustomInitializer(particle -> {
        // Custom logic for each particle
        float randomRotation = random.nextFloat() * 360;
        particle.setRotation(0, randomRotation, 0);
    });
```

### Multiple Emitter Types

```java
// Different items with different properties
for (int i = 0; i < 5; i++) {
    Item randomItem = getRandomItem();
    ParticleEmitter emitter = new ParticleEmitter()
        .setPosition(x, y)
        .setVelocity(0, -150)
        .setVelocityRange(100, 50)
        .setGravity(0, 200)
        .setAngularVelocity(0, 360, 0)
        .setScale(1.5f, 0.5f)
        .setAlpha(1.0f, 0.0f)
        .setLifetime(2.0f)
        .setItemStack(new ItemStack(randomItem, 1));
    
    particleSystem.emit(emitter, 10);
}
```

## Performance Considerations

1. **Max Particles**: Set appropriate limits with `setMaxParticles()` to prevent excessive particle counts
2. **Lifetime**: Shorter lifetimes mean particles are culled faster, improving performance
3. **Object Pooling**: Particles are automatically pooled and reused
4. **Update Frequency**: Particles update every frame - keep particle counts reasonable for smooth performance

## Example: Item Animations Screen

See `ItemAnimationsTestScreen.java` for a complete example demonstrating:
- Gravity-affected particle burst
- Multiple item types as particles
- 3D rotation with angular velocity
- Property interpolation (scale, alpha)
- Integration with UI buttons

## API Reference

### ParticleSystem
- `emit(ParticleEmitter, count)` - Emit particles
- `emitBurst(x, y, ParticleEmitter, count)` - Emit from a specific position
- `clear()` - Remove all active particles
- `getActiveParticleCount()` - Get number of active particles
- `setMaxParticles(max)` - Set maximum particle limit

### ParticleEmitter
- All setter methods return `this` for method chaining
- Static helpers: `burst()` and `gravityBurst()` for common patterns

### Particle (Advanced)
Direct particle manipulation is typically not needed, but available for advanced use cases:
- `update(deltaTime)` - Update particle physics and properties
- `isAlive()` - Check if particle is still active
- `kill()` - Immediately terminate the particle
- `reset()` - Reset all properties (used by object pool)
