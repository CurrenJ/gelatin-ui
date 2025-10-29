# Recipes

1) Centered menu with title and buttons
- Use a VBox with alignment(CENTER) and padding.
- Label.updateSize(context) for the title.
- Add SpriteButtons with text(...) and onClick(...) actions.

2) Scrollable list
- Make your root a VBox with spacing; let it grow naturally. UIScreen adds a VerticalScrollBar automatically.
- Optional: call uiScreen.setScrollSensitivity(pixelsPerUnit) for feel tuning.

3) Horizontal toolbar
- HBox with alignment(CENTER), spacing(8). Place SpriteButtons or ItemRenderers.

4) Modal overlay
- Put your page in a VBox, then add a Panel sized to the viewport with a semi-transparent background. Use pushScissor/popScissor if you clip inner content.

5) Image button with hover/pressed states
- SpriteButton.texture(default), .hoverTexture(...), .pressedTexture(...). Optionally add text for a caption.

6) Custom component with progress
- Extend UIElement, add a float progress field.
- In renderSelf, draw a background then a filled bar proportional to progress.
- Expose progress(float) that sets the field and markDirty(DirtyFlag.CONTENT).

7) Rotating item carousel
- Use RotatingItemRing, setItems(listOfStacks), tune radius and speeds.
- Hover to pause; click to center and enlarge.

8) Responsive scale-to-fit block
- VBox/HBox with scaleToFit(true) and maxHeight(viewportH) to keep content within the screen.

