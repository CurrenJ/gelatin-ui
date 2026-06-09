package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.UIEvent;
import io.github.currenj.gelatinui.gui.UIScreen;
import org.joml.Vector2f;

/**
 * Vertical scrollbar that renders a track and thumb.
 * Click on the track to jump to a scroll position.
 * Click and drag the thumb to smoothly scroll.
 */
public class VerticalScrollBar extends UIElement<VerticalScrollBar> {
    private final UIScreen screen;
    private int barWidth = 12;
    private int padding = 2;

    private int trackColor = 0x30FFFFFF; // translucent
    private int thumbColor = 0xAAFFFFFF;
    private int thumbHoverColor = 0xFFFFFFFF;

    private boolean hovered = false;

    public VerticalScrollBar(UIScreen screen) {
        this.screen = screen;
        // default size; UIScreen will set actual size/position
        this.size.set(barWidth, 100f);
    }

    public void setBarWidth(int w) {
        this.barWidth = Math.max(4, w);
        this.size.x = barWidth;
        markDirty();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // No animation required here
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        // Local coordinates: (0,0) .. (size.x, size.y)
        int x1 = 0;
        int y1 = 0;
        int x2 = Math.max(1, (int) Math.round(size.x));
        int y2 = Math.max(1, (int) Math.round(size.y));

        // Compute thumb size and position
        float contentH = screen.getContentHeight();
        float viewH = screen.getViewHeight();

        if (contentH <= 0f || viewH <= 0f || contentH <= viewH) {
            // Nothing to scroll; hide the scrollbar
            return;
        }

        // Draw track
        context.enableBlend();
        context.fill(x1, y1, x2, y2, trackColor);

        float visibleFraction = Math.min(1f, viewH / contentH);
        int trackH = y2 - y1 - padding * 2;
        int minThumb = 16;
        int thumbH = Math.max(minThumb, (int) Math.round(trackH * visibleFraction));

        float maxScroll = screen.getMaxScrollY();
        float scroll = screen.getScrollY();
        float tRange = Math.max(1f, trackH - thumbH);
        int thumbTop = y1 + padding + (int) Math.round((scroll / Math.max(1f, maxScroll)) * tRange);
        int thumbLeft = x1 + padding;
        int thumbRight = x2 - padding;

        context.fill(thumbLeft, thumbTop, thumbRight, thumbTop + thumbH, hovered ? thumbHoverColor : thumbColor);
        context.disableBlend();
    }

    // Drag state — set to true when the user grabs the thumb
    private boolean isDragging = false;

    @Override
    protected boolean onEvent(UIEvent event) {
        switch (event.getType()) {
            case HOVER_ENTER -> {
                hovered = true;
                return true;
            }
            case HOVER_EXIT -> {
                hovered = false;
                return true;
            }
            case CLICK -> {
                // Convert global mouse Y to local coordinate relative to this element
                Vector2f gp = getGlobalPosition();
                float localY = event.getMouseY() - gp.y;
                int h = Math.max(1, (int) Math.round(size.y));

                // Compute thumb position (same logic as renderSelf)
                float contentH = screen.getContentHeight();
                float viewH = screen.getViewHeight();
                if (contentH <= 0f || viewH <= 0f || contentH <= viewH) return true; // nothing to do

                float visibleFraction = Math.min(1f, viewH / contentH);
                int trackH = h - padding * 2;
                int minThumb = 16;
                int thumbH = Math.max(minThumb, (int) Math.round(trackH * visibleFraction));

                float maxScroll = screen.getMaxScrollY();
                float scroll = screen.getScrollY();
                float tRange = Math.max(1f, trackH - thumbH);
                int thumbTop = padding + (int) Math.round((scroll / Math.max(1f, maxScroll)) * tRange);

                // Check whether the click landed on the thumb itself
                boolean onThumb = localY >= thumbTop && localY <= thumbTop + thumbH;
                if (onThumb) {
                    // Start drag — subsequent mouse movement will update scroll position
                    screen.startDrag(this, event.getMouseY(), screen.getScrollY());
                    isDragging = true;
                    return true;
                }

                // Click on track (not thumb): jump scroll position, centering thumb on click
                float clickPos = localY - padding - thumbH * 0.5f;
                float frac = clickPos / tRange;
                if (Float.isNaN(frac)) frac = 0f;
                frac = Math.max(0f, Math.min(1f, frac));
                float newScroll = frac * screen.getMaxScrollY();
                screen.setScrollY(newScroll);
                return true;
            }
            case DRAG -> {
                if (!isDragging) return false;
                // Compute scroll delta from vertical mouse movement
                float dy = event.getMouseY() - screen.getDragStartMouseY();
                float contentH = screen.getContentHeight();
                float viewH = screen.getViewHeight();
                if (contentH <= 0f || viewH <= 0f || contentH <= viewH) return true;

                float visibleFraction = Math.min(1f, viewH / contentH);
                int h = Math.max(1, (int) Math.round(size.y));
                int trackH = h - padding * 2;
                int minThumb = 16;
                int thumbH = Math.max(minThumb, (int) Math.round(trackH * visibleFraction));
                float tRange = Math.max(1f, trackH - thumbH);

                // Convert pixel delta to scroll delta
                float scrollDelta = (dy / tRange) * screen.getMaxScrollY();
                float newScroll = screen.getDragStartValue() + scrollDelta;
                newScroll = Math.max(0f, Math.min(screen.getMaxScrollY(), newScroll));
                screen.setScrollY(newScroll);
                return true;
            }
            case DRAG_END -> {
                isDragging = false;
                return true;
            }
        }
        return false;
    }

    @Override
    protected VerticalScrollBar self() {
        return this;
    }

    // Convenience: update track size when viewport changes
    public void setTrackSize(float width, float height) {
        this.size.set(width, height);
        markDirty();
    }

    public int getBarWidth() {
        return barWidth;
    }
}
