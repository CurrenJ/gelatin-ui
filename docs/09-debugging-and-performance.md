# Debugging & Performance

Debug overlays (toggle in GelatinUIScreen)
- 8: Bounds overlay — semi-transparent fill for each element’s local bounds.
- 9: Grid overlay — quarter lines to visualize layout and borders.
- 0: Padding overlay — shows container padding strips.
- 7: Culled/rendered inspector — lists elements culled vs rendered this frame.

Dirty flags & caching
- UIElement only updates when dirty or animating. Keep your components honest with markDirty(...) when visual or layout state changes.
- UIElement caches global bounds; position/size/layout flags invalidate them. UIContainer also caches layout bounds and invalidates on child changes.

Culling
- UIScreen renders with a viewport and skips off-screen elements. Ensure getBounds stays correct to maximize culling benefits.

Measuring text
- Measure Labels via updateSize(context) before layout to avoid layout thrash.

Scrolling
- UIScreen computes maxScrollY from root.getSize().y vs viewport height. If your content seems not to scroll, ensure the root container’s size reflects its children (e.g., VBox/HBox performLayout) and that fillHeight isn’t pinning the height unintentionally.

Profiling tips
- Temporarily enable culled inspector (7) to see if large trees are being skipped as expected.
- Watch for elements that stay animating=true (needsUpdate) unnecessarily; ensure animations end and elements clearAnimations() when done.

