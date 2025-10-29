# Gelatin UI: Architecture Overview

Gelatin UI is a lightweight, composable GUI framework for Minecraft built on top of GuiGraphics. It aims to be predictable, high-performance, and easy to extend. This document explains the key concepts you’ll use every day.

Core building blocks
- IUIElement: Minimal interface implemented by all elements. Exposes update, render, getBounds, position/size, visibility, event handling, and dirty-flagging.
- UIElement: Base class for leaf elements. Implements:
  - Dirty flag system with fine-grained flags (POSITION, SIZE, CHILDREN, CONTENT, VISIBILITY, STYLE, LAYOUT).
  - Cached bounds and invalidation.
  - Smooth interpolation to target position/scale and a simple keyframe animation system (channels + keyframes).
  - Event listener list with addEventListener/removeEventListener.
  - Debug utilities: toggle semi-transparent bounds, grid, padding, and culled elements tracking.
- UIContainer: Base class for elements with children. Adds:
  - Child list management with automatic dirty propagation.
  - A small layout cache and invalidation helpers.
  - performLayout hook and convenience forceLayout.
  - Depth-first update/render/event dispatch.
- UIScreen: A root controller that:
  - Holds your root element and a viewport rectangle.
  - Ticks update/render loops, handles mouse events, and provides hover cooldown and content scrolling.
  - Maintains and renders a VerticalScrollBar overlay for long content.
- GelatinUIScreen: A vanilla Screen subclass that embeds a UIScreen. Override buildUI() to create your UI tree and call uiScreen.setRoot(...).
- IRenderContext and MinecraftRenderContext: A render abstraction so components can be engine-agnostic. MinecraftRenderContext adapts GuiGraphics and Font.
- UI (builder): Fluent factories for common components: VBox, HBox, Panel, Label, Rectangle, SpriteRectangle, SpriteButton, SpriteProgressBar, ItemRenderer, RotatingItemRing, plus color helpers.

Rendering model
- Hierarchical transforms: UIElement.render applies translation (position) and scaling (currentScale * effectScale) using the PoseStack when rendered through MinecraftRenderContext. Your renderSelf should draw in local coordinates with origin at (0,0).
- Bounds: getBounds returns global bounds by combining local position/size with the cumulative parent scale and offset. This enables accurate culling and hit-testing.
- Culling: UIScreen passes a viewport to render; elements outside skip rendering, optionally tracked for debugging.

Update model
- Dirty flags control work: update is only called when isDirty, animating, or with active keyframe animations. UIElement manages flags and runs hooks:
  - onPositionChanged, onSizeChanged, onVisibilityChanged
  - recalculateLayout (for containers) and onUpdate (for element-specific logic)
- Animation: setTargetPosition and setTargetScale interpolate smoothly. effectScale is an additional transient scale used by click bounce.
- Layout: Containers set size and position their children in performLayout; they invalidate layout when needed and markDirty(LAYOUT).

Event model
- UIEvent supports CLICK, HOVER_ENTER, HOVER_EXIT, SCROLL, DRAG_START/DRAG/DRAG_END, FOCUS/BLUR, KEY events.
- UIContainer dispatches events front-to-back to children before handling itself.
- UIScreen manages hover with a brief cooldown (80 ms) to reduce flicker and supports content scrolling when no child consumes scroll.

Out-of-the-box components
- Layout: VBox, HBox, Panel
- Visuals: Label, Rectangle, SpriteRectangle (with textures and text), SpriteButton (clickable), SpriteProgressBar
- Minecraft-specific: ItemRenderer, RotatingItemRing
- System overlay: VerticalScrollBar (owned by UIScreen)

Debug and dev ergonomics
- Toggle with keys (handled in GelatinUIScreen.keyPressed):
  - 8: bounds overlay
  - 9: grid overlay
  - 0: padding overlay
  - 7: culled/rendered elements inspector
- Builder API in UI keeps your UI code compact and expressive.

What’s next
- Read 02-getting-started.md to build your first screen, then 03-layout.md to master layouting.

