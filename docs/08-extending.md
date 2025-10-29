# Extending Gelatin UI

Create a custom element (leaf)
1) Extend UIElement.
2) In the constructor, set base size (unscaled) via this.size.set(w,h) or setSize(Vector2f).
3) Override renderSelf(IRenderContext) and draw in local coordinates (0,0 to size.x,size.y). Scaling/translation are applied for you under MinecraftRenderContext.
4) Override onUpdate(float dt) if you have time-based logic. Use markDirty(DirtyFlag.CONTENT/SIZE/...) when state changes.
5) Optionally override onEvent(UIEvent) to handle hover/click/scroll/etc.
6) If you expose sub-properties that affect layout or bounds, call markDirty(DirtyFlag.LAYOUT) or DirtyFlag.SIZE accordingly.

Create a custom container
1) Extend UIContainer.
2) Implement performLayout(): set this.size and call child.setPosition(...) for each child. Use children’s getSize() and their current scale (via ((UIElement)child).getCurrentScale()) when needed.
3) Call markDirty(DirtyFlag.LAYOUT) when layout-affecting properties change (e.g., spacing, alignment).
4) If global transform (position/scale) changes, UIContainer handles invalidating child bounds with invalidateChildBounds.

Dirty flags: quick guide
- POSITION: element moved (invalidates own and descendant bounds)
- SIZE: element’s base size changed (re-layout or bounds change)
- CHILDREN: add/remove children
- CONTENT: visual-only change (no layout), but re-render needed
- VISIBILITY: show/hide may affect layout
- LAYOUT: request performLayout pass

Hit-testing and bounds
- getBounds returns global bounds using hierarchical scale and position — use it for click tests. If you cache anything that affects bounds, ensure you invalidate when POSITION/SIZE/LAYOUT changes.

Naming and debugging
- setDebugName("MyThing") gives you helpful labels in the culled/rendered overlay.

Animations
- Use setTargetPosition/setTargetScale for implicit interpolation.
- For custom properties, wire FloatKeyframeAnimation and apply via a Consumer<Float> that sets your field and markDirty(...) appropriately.

