# Events and Interaction

Event types
- UIEvent.Type: CLICK, HOVER_ENTER, HOVER_EXIT, SCROLL, DRAG_START, DRAG, DRAG_END, FOCUS, BLUR, KEY_PRESS, KEY_RELEASE

Dispatch rules
- UIScreen receives raw mouse events and locates the topmost element under the cursor using global getBounds().
- UIContainer.handleEvent dispatches to children front-to-back (last added on top) before calling super.handleEvent.
- handleEvent returns true if consumed. Events bubble up if not consumed.

Hover model
- UIScreen enforces an 80 ms cooldown between hover enter/exit to reduce flicker during rapid pointer transitions. During cooldown, the next hover transition is scheduled and fired when ready.
- SpriteRectangle listens for HOVER_ENTER/EXIT to set hovered state; SpriteButton then builds on it.

Click model
- SpriteButton checks the click is inside its global bounds, sets pressed visuals briefly, plays a bounce animation, invokes onClick if set, consumes the event.

Listening to events
- For any UIElement (or subclass), you can register listeners:
  - element.addEventListener(evt -> { if (evt.getType() == UIEvent.Type.HOVER_ENTER) {...} });
- Or override protected boolean onEvent(UIEvent event) in your custom element. Return true to consume.

Scroll model
- UIScreen first gives scroll events to the element under the cursor (or the scrollbar). If unconsumed and setScrollEnabled(true), it scrolls the root content.

Keyboard events
- GelatinUIScreen.keyPressed handles debug toggles for you (8/9/0/7). You can override and call super to preserve toggles.

