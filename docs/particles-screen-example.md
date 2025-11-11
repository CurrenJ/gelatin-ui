# GelatinUIScreenWithParticles Usage Guide

The `GelatinUIScreenWithParticles` class is an extension of `GelatinUIScreen` that provides a built-in particle system overlay covering the entire screen. The particle system is rendered independently of the UI tree, ensuring particles always appear on top of your UI elements.

## Key Features

- **Automatic Setup**: Particle system is automatically created and managed
- **Full Screen Coverage**: Particle system covers the entire screen area
- **Independent Rendering**: Particles render on top of all UI elements
- **Auto-Resize**: Particle system automatically resizes with the screen
- **Simple API**: Easy access through helper methods

## Basic Usage

### 1. Extend GelatinUIScreenWithParticles

Instead of extending `GelatinUIScreen`, extend `GelatinUIScreenWithParticles`:

```java
public class MyScreen extends GelatinUIScreenWithParticles<MyMenu> {
    public MyScreen(MyMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("My Screen"));
    }
    
    @Override
    protected void buildUI() {
        // Build your UI normally
        VBox container = new VBox();
        // ... add components
        uiScreen.setRoot(container);
    }
}
```

### 2. Access the Particle System

Use `getParticleSystem()` to access the built-in particle system:

```java
@Override
protected void buildUI() {
    // Create a button that emits particles
    SpriteButton button = new SpriteButton(100, 28, 0xFF4682B4)
        .text("Emit Particles", 0xFFFFFFFF)
        .onClick(e -> {
            // Create an emitter configuration
            ParticleEmitter emitter = new ParticleEmitter()
                .setPosition(100, 100)
                .setVelocity(0, -50)
                .setVelocityRange(20, 10)
                .setLifetime(1.0f)
                .setItemStack(new ItemStack(Items.DIAMOND, 1));
            
            // Emit particles through the screen's particle system
            getParticleSystem().emit(emitter, 10);
        });
}
```

### 3. Helper Methods

The class provides convenient helper methods:

```java
// Clear all particles
clearParticles();

// Get active particle count
int count = getActiveParticleCount();

// Set max particles
setMaxParticles(500);
```

## Complete Example

Here's a complete example showing various particle effects:

```java
public class ParticlesDemoScreen extends GelatinUIScreenWithParticles<MyMenu> {
    
    public ParticlesDemoScreen(MyMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Particles Demo"));
    }
    
    @Override
    protected void buildUI() {
        VBox container = new VBox()
            .alignment(VBox.Alignment.CENTER)
            .spacing(10);
        
        // Particle burst button
        SpriteButton burstButton = new SpriteButton(120, 28, 0xFFFF6B35)
            .text("Particle Burst", 0xFFFFFFFF)
            .onClick(e -> emitBurst());
        container.addChild(burstButton);
        
        // Clear button
        SpriteButton clearButton = new SpriteButton(120, 28, 0xFF646464)
            .text("Clear", 0xFFFFFFFF)
            .onClick(e -> clearParticles());
        container.addChild(clearButton);
        
        uiScreen.setRoot(container);
    }
    
    private void emitBurst() {
        // Get screen center
        float centerX = getParticleSystem().getSize().x / 2;
        float centerY = getParticleSystem().getSize().y / 2;
        
        // Create gravity-affected emitter
        ParticleEmitter emitter = new ParticleEmitter()
            .setPosition(centerX, centerY)
            .setVelocity(0, -150)      // Upward velocity
            .setVelocityRange(100, 50)  // Random spread
            .setGravity(0, 200)         // Gravity pulls down
            .setScale(1.5f, 0.5f)       // Shrink over time
            .setAlpha(1.0f, 0.0f)       // Fade out
            .setLifetime(2.0f)
            .setItemStack(new ItemStack(Items.DIAMOND, 1));
        
        // Emit 50 particles
        getParticleSystem().emit(emitter, 50);
    }
}
```

## Particle System Coordinates

The particle system uses screen-space coordinates:
- Origin (0, 0) is at the top-left corner
- X increases to the right
- Y increases downward
- Size matches the screen dimensions (`width` x `height`)

## Differences from Manual Particle Systems

### Before (Manual Approach)
```java
public class MyScreen extends GelatinUIScreen<MyMenu> {
    private ParticleSystem particleSystem;
    
    @Override
    protected void buildUI() {
        // Create and position particle system manually
        particleSystem = new ParticleSystem(400, 300);
        
        VBox container = new VBox();
        // ... add components
        
        // Must add to UI tree
        container.addChild(particleSystem);
        uiScreen.setRoot(container);
    }
}
```

### After (GelatinUIScreenWithParticles)
```java
public class MyScreen extends GelatinUIScreenWithParticles<MyMenu> {
    @Override
    protected void buildUI() {
        // Particle system automatically available
        VBox container = new VBox();
        // ... add components
        
        // No need to add to UI tree - renders automatically
        uiScreen.setRoot(container);
        
        // Access anytime with getParticleSystem()
        getParticleSystem().emit(emitter, 10);
    }
}
```

## Benefits

1. **Cleaner Code**: No manual particle system setup
2. **Guaranteed Overlay**: Always renders on top of UI
3. **Auto-Management**: Automatic creation, sizing, and cleanup
4. **Consistent API**: Same interface across all screens
5. **No Tree Conflicts**: Independent of UI element hierarchy

## When to Use

Use `GelatinUIScreenWithParticles` when:
- You need particle effects that overlay your entire UI
- You want particles to always render on top
- You want simplified particle system management
- You're creating effects like celebrations, backgrounds, or overlays

Use manual `ParticleSystem` (in UI tree) when:
- You need particles constrained to a specific UI area
- You want particles to scroll with content
- You need multiple independent particle systems
- You want particles affected by UI clipping/culling

## See Also

- [Particle System Documentation](particle-system.md)
- [Particle Emitters](particle-emitters.md)
- [ItemAnimationsTestScreen Example](../common/src/main/java/io/github/currenj/gelatinui/example/ItemAnimationsTestScreen.java)

