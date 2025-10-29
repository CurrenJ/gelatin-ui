Panel
- Panel is a simple container with an optional background fill; it doesn’t alter child positions by itself.

Practical tips
- For scrollable content: put everything under a VBox root and let UIScreen’s vertical scrolling handle overflow. UIScreen computes max scroll based on root.getSize().y.
- To quickly center a group: set VBox/HBox alignment to CENTER and adjust padding/spacing; or enable UIScreen.setAutoCenterRoot(true) if you want automatic centering of the entire root.
# Layout: VBox and HBox

Gelatin UI favors explicit, predictable layout. Two containers cover most needs:
- VBox: vertical stack
- HBox: horizontal row

Shared concepts
- spacing(float): gap between children (unscaled base units)
- padding(float): inner padding on all sides
- alignment: how children align on the cross axis (VBox: LEFT/CENTER/RIGHT; HBox: TOP/CENTER/BOTTOM)
- fillWidth(boolean), fillHeight(boolean): try to match parent (or viewport when root)
- setScreenWidth/Height(float): UIScreen sets these on root boxes so fill flags work even when parent is null
- scaleToFit(boolean) + maxWidth/Height(float): uniform scale down children if content exceeds bounds
- forceLayout(): recompute layout immediately (handy after big changes)

Sizing rules
- Base size is computed from children’s unscaled sizes plus padding and spacing.
- If fillWidth/fillHeight is true, the container adopts the parent (or screen) dimension on that axis.
- If scaleToFit is true, a uniform scaleFactor is computed so content fits within available space (or maxWidth/Height if provided). The scaleFactor is applied to children via setTargetScale.

Positioning rules
- Children are placed using their effective size (including their currentScale and optional scaleToFit factor).
- When alignment changes, next performLayout animates child repositioning by using setTargetPosition with animate.

Working with nested boxes
- You can nest VBoxes and HBoxes freely. Containers call forceLayout() on child containers before measuring to ensure consistent sizes.


