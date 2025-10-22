# Components Catalog

Builder helpers live in UI, but you can also instantiate classes directly. Below is a quick reference of provided components and notable APIs.

Layout containers
- Panel: UIContainer with optional background.
  - backgroundColor(int argb), drawBackground(boolean)
- VBox: Vertical stack.
  - spacing(float), padding(float), alignment(VBox.Alignment), fillWidth(boolean), fillHeight(boolean)
  - scaleToFit(boolean), maxWidth(float), maxHeight(float)
- HBox: Horizontal row.
  - spacing(float), padding(float), alignment(HBox.Alignment), fillWidth(boolean), fillHeight(boolean)
  - scaleToFit(boolean), maxWidth(float), maxHeight(float)

Text and shapes
- Label: Auto-sized text.
  - text(String), color(int), centered(boolean)
  - updateSize(IRenderContext) must be called to measure base size
  - Inherits UIElement.scale(float)
- Rectangle: Solid color rectangle.
  - color(int)

Sprites and buttons
- SpriteRectangle: Draw a texture region or a solid color with optional centered text.
  - texture(ResourceLocation | SpriteData), hoverTexture(...), pressedTexture(...)
  - color(int), text(String, int)
  - Supports hovered/pressed states via events
- SpriteButton extends SpriteRectangle: Clickable with onClick(Runnable) and pressed visual feedback.
  - Inherits all SpriteRectangle APIs; adds click handling and a bounce animation

Progress & status
- SpriteProgressBar: A layered textured bar with skill-level decorations.
  - progress(float 0..1), skillLevel(int)

Minecraft items
- ItemRenderer: Renders an ItemStack.
  - itemStack(ItemStack), showCount(boolean), itemScale(float)

Composed/animated
- RotatingItemRing: Arranges ItemRenderers on a rotating ring, with hover/selection interactions.
  - radius, defaultAngularSpeed, defaultItemScale, hoverItemScale, selectedItemScale
  - setItems(List<ItemStack>), addItem(ItemStack), select(int)

System overlay (auto-managed by UIScreen)
- VerticalScrollBar: Visual scroll bar aligned to the right; responds to hover and click-to-jump.

Colors
- UI.rgb(r,g,b) and UI.argb(a,r,g,b) are convenience methods. UI.hex("FFAABBCC") parses hex.

Conventions
- All components are UIElements. For sizing, set the base (unscaled) size in the component, and let UIElement handle scaling and global bounds.
- For labels and text, measure via updateSize(context) to keep layout precise.

