# Sprite Render Modes - Implementation Summary

## Overview

The sprite system has been refactored to support three rendering modes through a unified `SpriteData` class:

1. **STRETCH** - Default mode, stretches sprite to fill destination
2. **REPEAT** - Tiles sprite to fill destination (pixel-perfect tiling)
3. **SLICE** - 9-slice scaling with preserved corners/edges (pixel-art friendly)

## Key Changes

### New Classes

- **`SpriteRenderMode`** - Enum defining the three render modes
- **`SpriteData`** - Enhanced to include `renderMode` and slice parameters (sliceLeft, sliceRight, sliceTop, sliceBottom)

### Updated Classes

- **`IRenderContext`** - Added three new methods:
  - `drawSprite(SpriteData, ...)` - Unified sprite rendering (dispatches to appropriate mode)
  - `drawRepeatingTexture(...)` - Implements tiling logic
  - `drawSlicedTexture(...)` - Implements 9-slice logic
  
- **`SpriteRectangle`** - Simplified to use unified `drawSprite()` method
- **`Panel`** - Uses unified `SpriteData` for backgrounds

### Deprecated

- **`SlicedSpriteData`** - Functionality merged into `SpriteData` with SLICE mode

## API Examples

### Creating Sprites with Different Modes

```java
// Stretch (default)
SpriteData stretched = new SpriteData(texture, 0, 0, 32, 32);

// Repeat (tiling)
SpriteData tiled = SpriteData.repeating(texture, 0, 0, 16, 16);

// Slice (9-slice)
SpriteData sliced = SpriteData.sliced(texture, 0, 0, 32, 32, 4); // 4px edges
```

### Using with Components

```java
// Button with 9-slice background
SpriteButton button = new SpriteButton(120, 30, 0xFF404040)
    .texture(SpriteData.sliced(texture, 0, 0, 32, 16, 4))
    .text("Click Me", 0xFFFFFFFF);

// Panel with tiled background
Panel panel = new Panel()
    .backgroundSprite(SpriteData.repeating(pattern, 0, 0, 16, 16))
    .setSize(200, 150);
```

## Implementation Details

### REPEAT Mode

Tiles are rendered in a grid pattern:
- Calculates `tilesX = ceil(width / texWidth)` and `tilesY = ceil(height / texHeight)`
- Edge tiles are automatically clipped to fit exactly
- No texture stretching, maintains pixel-perfect appearance

### SLICE Mode

Divides sprite into 9 regions:
```
┌─────┬─────────┬─────┐
│ TL  │   Top   │ TR  │  Corners: original size
├─────┼─────────┼─────┤
│Left │ Center  │Right│  Edges: 1D stretch
├─────┼─────────┼─────┤
│ BL  │ Bottom  │ BR  │  Center: 2D stretch
└─────┴─────────┴─────┘
```

Validates that `left + right <= texWidth` and `top + bottom <= texHeight`

### Backward Compatibility

All existing code using regular `SpriteData` constructors continues to work (defaults to STRETCH mode).

## Migration Guide

### From SlicedSpriteData

```java
// Old (deprecated)
SlicedSpriteData old = new SlicedSpriteData(texture, 0, 0, 32, 32, 4);
panel.backgroundSlicedSprite(old);

// New
SpriteData sprite = SpriteData.sliced(texture, 0, 0, 32, 32, 4);
panel.backgroundSprite(sprite);
```

### SpriteRectangle Internal Changes

The `slicedSprite`, `hoverSlicedSprite`, and `pressedSlicedSprite` fields have been removed. All sprite rendering now goes through the unified `sprite`, `hoverSprite`, and `pressedSprite` fields, with the render mode controlled by the `SpriteData.renderMode` field.

## Files Modified

- `/common/src/main/java/io/github/currenj/gelatinui/gui/components/SpriteRenderMode.java` (new)
- `/common/src/main/java/io/github/currenj/gelatinui/gui/components/SpriteData.java` (updated)
- `/common/src/main/java/io/github/currenj/gelatinui/gui/IRenderContext.java` (updated)
- `/common/src/main/java/io/github/currenj/gelatinui/gui/components/SpriteRectangle.java` (updated)
- `/common/src/main/java/io/github/currenj/gelatinui/gui/components/Panel.java` (updated)
- `/docs/sliced-sprites.md` (updated with render modes documentation)

## Benefits

1. **Unified API** - One `SpriteData` class handles all rendering modes
2. **Flexibility** - Easy to switch between modes or mix different modes in UI
3. **Clean Code** - Simpler rendering logic in components
4. **Extensibility** - Easy to add new render modes in the future
5. **Type Safety** - Compile-time validation of render modes via enum

