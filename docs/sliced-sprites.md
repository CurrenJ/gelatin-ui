# Sprite Rendering Modes

## Overview

The sprite system supports three rendering modes to handle different use cases:

- **STRETCH** (default): Stretches the sprite to fill the destination area
- **REPEAT**: Tiles the sprite to fill the destination area (pixel-perfect repetition)
- **SLICE**: Uses 9-slice scaling to preserve corners and edges (perfect for pixel-art UI)

## SpriteRenderMode Enum

```java
public enum SpriteRenderMode {
    STRETCH,  // Default - stretches entire sprite
    REPEAT,   // Tiles sprite to fill area
    SLICE     // 9-slice with preserved corners/edges
}
```

## Using Render Modes with SpriteData

### Stretch Mode (Default)

The default behavior - stretches the sprite to fit the destination size:

```java
// Simple stretch (default mode)
SpriteData sprite = new SpriteData(texture, 0, 0, 32, 32);

// Explicit stretch mode
SpriteData sprite = new SpriteData(
    texture, 0, 0, 32, 32,
    0, 0,              // actualW, actualH
    256, 256,          // atlas size
    SpriteRenderMode.STRETCH
);
```

### Repeat Mode (Tiling)

Perfect for seamless patterns that should tile:

```java
// Using factory method
SpriteData tiledBg = SpriteData.repeating(texture, 0, 0, 16, 16);

// Or explicit constructor
SpriteData tiledBg = new SpriteData(
    texture, 0, 0, 16, 16,
    0, 0, 256, 256,
    SpriteRenderMode.REPEAT
);
```

The texture will repeat horizontally and vertically to fill the destination area, with edge tiles automatically clipped.

### Slice Mode (9-Slice Scaling)

Perfect for pixel-art UI panels that need to scale cleanly:

```java
// Using factory method with uniform edge size
SpriteData panelBg = SpriteData.sliced(texture, 0, 0, 32, 32, 4);

// Separate horizontal and vertical edge sizes
SpriteData panelBg = SpriteData.sliced(texture, 0, 0, 32, 32, 4, 3);

// Individual edge sizes (left, right, top, bottom)
SpriteData panelBg = SpriteData.sliced(texture, 0, 0, 32, 32, 3, 3, 2, 2);
```

The sprite is divided into 9 regions:
- **Corners**: Rendered at original size (never stretched)
- **Edges**: Stretched in one dimension only
- **Center**: Stretched in both dimensions

## Usage Examples

### SpriteRectangle/SpriteButton with Different Modes

```java
// Stretched button background
SpriteButton button1 = new SpriteButton(100, 30, 0xFF404040)
    .texture(new SpriteData(texture, 0, 0, 32, 16))
    .text("Stretch", 0xFFFFFFFF);

// Tiled pattern background
SpriteButton button2 = new SpriteButton(100, 30, 0xFF404040)
    .texture(SpriteData.repeating(pattern, 0, 0, 8, 8))
    .text("Repeat", 0xFFFFFFFF);

// 9-slice pixel-art button
SpriteButton button3 = new SpriteButton(100, 30, 0xFF404040)
    .texture(SpriteData.sliced(pixelArt, 0, 0, 32, 16, 4))
    .text("Slice", 0xFFFFFFFF);
```

### Panel Backgrounds

```java
// Stretched background
Panel panel1 = new Panel()
    .backgroundSprite(new SpriteData(texture, 0, 0, 64, 64))
    .setSize(200, 150);

// Tiled background pattern
Panel panel2 = new Panel()
    .backgroundSprite(SpriteData.repeating(pattern, 0, 0, 16, 16))
    .setSize(200, 150);

// 9-slice pixel-art panel
Panel panel3 = new Panel()
    .backgroundSprite(SpriteData.sliced(panelTexture, 0, 0, 32, 32, 4))
    .setSize(200, 150);
```

### State-Based Rendering

Different render modes can be used for different button states:

```java
SpriteButton button = new SpriteButton(120, 30, 0xFF404040)
    .texture(SpriteData.sliced(normalTexture, 0, 0, 32, 16, 4))
    .hoverTexture(SpriteData.sliced(hoverTexture, 0, 16, 32, 16, 4))
    .pressedTexture(SpriteData.sliced(pressedTexture, 0, 32, 32, 16, 4))
    .text("Click Me", 0xFFFFFFFF);
```

## Implementation Details

### IRenderContext Methods

The rendering is handled through these methods in `IRenderContext`:

```java
// Unified sprite rendering (handles all modes automatically)
void drawSprite(SpriteData sprite, int x, int y, int width, int height);

// Individual mode implementations
void drawTexture(...);              // STRETCH mode
void drawRepeatingTexture(...);     // REPEAT mode  
void drawSlicedTexture(...);        // SLICE mode
```

### How REPEAT Works

The repeat mode calculates the number of tiles needed and renders each tile:

```java
tilesX = ceil(destinationWidth / sourceWidth)
tilesY = ceil(destinationHeight / sourceHeight)
```

Edge tiles are automatically clipped to fit the destination area precisely.

### How SLICE Works

The 9-slice algorithm divides the source texture into regions:

```
+-------+-------+-------+
|  TL   |  Top  |  TR   |  Corners: original size
+-------+-------+-------+
| Left  | Center| Right |  Edges: stretched in 1D
+-------+-------+-------+
|  BL   | Bottom|  BR   |  Center: stretched in 2D
+-------+-------+-------+
```

Minimum size for sliced rendering: `leftWidth + rightWidth` by `topHeight + bottomHeight`

## Best Practices

1. **Use STRETCH for**: Photos, gradients, simple backgrounds that can distort
2. **Use REPEAT for**: Seamless patterns, textures that should tile naturally
3. **Use SLICE for**: Pixel-art UI panels, buttons, frames that need clean scaling

4. **Performance**: REPEAT and SLICE make multiple draw calls - use STRETCH when performance is critical

5. **Texture Design**:
   - REPEAT textures should tile seamlessly at edges
   - SLICE textures should have consistent corner/edge styling
   - Design slice edges to be 1-dimensional patterns

6. **Validation**: SLICE mode validates that slice dimensions don't exceed texture size

## Migration from SlicedSpriteData

The old `SlicedSpriteData` class is deprecated. Use `SpriteData.sliced()` factory methods instead:

```java
// Old way (deprecated)
SlicedSpriteData old = new SlicedSpriteData(texture, 0, 0, 32, 32, 4);
panel.backgroundSlicedSprite(old);

// New way
SpriteData sprite = SpriteData.sliced(texture, 0, 0, 32, 32, 4);
panel.backgroundSprite(sprite);
```

## See Also

- `SpriteData` - Unified sprite data with render mode
- `SpriteRenderMode` - Render mode enum
- `Panel` - Container with sprite backgrounds
- `SpriteRectangle` - Base class for sprite-based UI elements

