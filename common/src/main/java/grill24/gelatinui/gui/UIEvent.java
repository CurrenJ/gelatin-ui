package grill24.gelatinui.gui;

/**
 * Represents a UI event such as click, hover, scroll, etc.
 * Events can be dispatched, consumed, and bubbled through the UI tree.
 */
public class UIEvent {
    /**
     * Type of UI event
     */
    public enum Type {
        CLICK,
        HOVER_ENTER,
        HOVER_EXIT,
        SCROLL,
        DRAG_START,
        DRAG,
        DRAG_END,
        FOCUS,
        BLUR,
        KEY_PRESS,
        KEY_RELEASE
    }

    private final Type type;
    private final IUIElement target;
    private final int mouseX;
    private final int mouseY;
    private final float scrollDelta;
    private boolean consumed = false;

    public UIEvent(Type type, IUIElement target, int mouseX, int mouseY) {
        this(type, target, mouseX, mouseY, 0);
    }

    public UIEvent(Type type, IUIElement target, int mouseX, int mouseY, float scrollDelta) {
        this.type = type;
        this.target = target;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.scrollDelta = scrollDelta;
    }

    public Type getType() {
        return type;
    }

    public IUIElement getTarget() {
        return target;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public float getScrollDelta() {
        return scrollDelta;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        this.consumed = true;
    }
}

