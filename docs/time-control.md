# Time Control System

The Gelatin UI framework includes a comprehensive time control system for debugging and fine-tuning animations. This allows you to pause, step through, and adjust the speed of all UI updates and animations.

## Overview

The `UITimeControl` class provides global control over the UI animation timeline. All UI updates and animations automatically respect these settings without requiring any changes to your existing code.

## Features

### Global Timescale
Adjust the speed of all UI animations and updates:
- **Normal speed**: 1.0x (default)
- **Slow motion**: 0.5x or slower
- **Fast forward**: 2.0x or faster
- **Range**: 0.0x to 5.0x (configurable)

### Pause Mode
Completely pause all UI updates and animations to inspect the current state.

### Step-by-Step Mode
When paused, advance the animation frame-by-frame for precise debugging:
- Each step advances by a configurable time delta (default: 0.016s ≈ 60fps)
- Perfect for debugging complex animation sequences

## Keyboard Shortcuts

The following keyboard shortcuts are available in any `GelatinUIScreen`:

### Time Control
- **P** - Toggle pause/resume
- **N** - Step forward one frame (only when paused)
- **[** - Decrease timescale by 0.1x
- **]** - Increase timescale by 0.1x

### Timescale Presets
- **4** - Reset timescale to 1.0x (normal speed)
- **5** - Set timescale to 0.5x (slow motion)
- **6** - Set timescale to 2.0x (fast forward)

### Debug Overlays
- **7** - Toggle culled elements debug view
- **8** - Toggle bounds debug overlay
- **9** - Toggle grid debug overlay
- **0** - Toggle padding debug overlay

## Programmatic Usage

### Basic Time Control

```java
import io.github.currenj.gelatinui.gui.UITimeControl;

// Set timescale
UITimeControl.setTimescale(0.5f);  // Half speed
UITimeControl.setTimescale(2.0f);  // Double speed

// Pause/Resume
UITimeControl.pause();
UITimeControl.resume();
UITimeControl.togglePause();

// Check pause state
if (UITimeControl.isPaused()) {
    // Handle paused state
}
```

### Step-by-Step Animation

```java
// Pause and step through animation
UITimeControl.pause();

// Advance by one step
UITimeControl.step();

// Advance by multiple steps
UITimeControl.step(5);

// Configure step size (default is 0.016s)
UITimeControl.setStepSize(0.033f);  // 30fps steps
```

### Get Current State

```java
// Get current timescale
float scale = UITimeControl.getTimescale();

// Get status string for display
String status = UITimeControl.getStatusString();
// Returns: "PAUSED (steps pending: 0, step size: 0.016s)"
// or: "RUNNING (timescale: 1.50x)"

// Reset to defaults
UITimeControl.reset();
```

## How It Works

### Integration with UIScreen

The `UIScreen` class automatically applies time control to all updates:

```java
public void update(float deltaTime) {
    // Delta time is automatically processed through UITimeControl
    float adjustedDeltaTime = UITimeControl.processDeltaTime(deltaTime);
    
    if (root != null) {
        root.update(adjustedDeltaTime);
        // ... rest of update logic
    }
}
```

### Integration with Animations

All animations in the UI system receive the adjusted delta time:
- **Position interpolation** - Smooth transitions respect timescale
- **Scale animations** - Resize effects can be slowed down or sped up
- **Keyframe animations** - Custom animations work with time control
- **Effect animations** - Click bounces and other effects are controlled

### Status Display

When time control is active (paused or non-default timescale), a status overlay is automatically displayed in the bottom-right corner of the screen:
- **Yellow text** - Displayed when paused
- **White text** - Displayed when timescale is adjusted

## Use Cases

### Animation Debugging
1. **Pause** the UI to inspect the current state
2. **Step** through frames to see exactly how animations progress
3. **Slow down** complex animations to see what's happening

### Performance Testing
1. **Speed up** animations to test how they look at higher speeds
2. **Verify** animations complete correctly at different timescales
3. **Test** for timing-dependent bugs

### Visual Design
1. **Slow motion** mode to fine-tune animation curves
2. **Step through** to verify frame-by-frame appearance
3. **Adjust** timescale to find the perfect animation speed

## Technical Details

### Time Processing

The `processDeltaTime()` method handles all timing logic:

```java
public static float processDeltaTime(float deltaTime) {
    if (isPaused) {
        // In pause mode, only advance if steps are requested
        if (stepsRequested > 0) {
            stepsRequested--;
            return stepSize;
        }
        return 0.0f;
    }
    
    // Apply timescale to normal deltaTime
    return deltaTime * globalTimescale;
}
```

### Thread Safety

The `UITimeControl` class uses static methods and is designed for single-threaded UI updates. All time control operations should be performed on the main render thread.

### Performance

Time control has minimal performance overhead:
- Single multiplication for timescale (when running)
- Simple branching for pause state
- No allocations or complex calculations

## Best Practices

1. **Use shortcuts during development** - Quickly pause and inspect animations
2. **Reset after debugging** - Press '4' to return to normal speed
3. **Document animation timing** - Note which timescales look best
4. **Test at extremes** - Try 0.1x and 5.0x to ensure animations work at all speeds
5. **Combine with debug overlays** - Use with bounds/grid/padding visualization

## Example: Debugging a Complex Animation

```java
// In your GelatinUIScreen implementation
@Override
protected void buildUI() {
    UIElement myElement = createComplexAnimatedElement();
    
    // Start paused for inspection
    UITimeControl.pause();
    
    // ... add to screen
}

// During testing:
// 1. Press 'P' to toggle pause
// 2. Press 'N' repeatedly to step through frames
// 3. Press '5' for slow motion when resuming
// 4. Press '[' or ']' to fine-tune speed
// 5. Press '4' to return to normal when done
```

## Limitations

- Time control affects all UI elements globally (cannot control individual elements)
- Timescale range is limited to 0.0x - 5.0x for practical purposes
- External timing systems (like Minecraft's game tick) are not affected
- Mouse events and user input are not time-scaled

## See Also

- [Animation System](06-animations.md) - Learn about UI animations
- [Debugging and Performance](09-debugging-and-performance.md) - Other debugging tools
- [UIElement API](04-components.md) - Component documentation

