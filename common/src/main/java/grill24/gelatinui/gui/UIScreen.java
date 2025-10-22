package grill24.gelatinui.gui;

import java.util.ArrayList;
import java.util.List;

import grill24.gelatinui.gui.components.HBox;
import grill24.gelatinui.gui.components.VBox;
import grill24.gelatinui.gui.components.VerticalScrollBar;
import java.awt.geom.Rectangle2D;
import org.joml.Vector2f;

/**
 * Root UI manager that handles the main update and render loop.
 * Manages event dispatching and viewport setup.
 */
public class UIScreen {
    private IUIElement root;
    private final List<IUIElement> dirtyElements = new ArrayList<>();
    private Rectangle2D viewport;
    // visual scrollbar
    private final VerticalScrollBar vscroll;
    private int scrollbarMargin = 6; // pixels from right edge

    // Mouse state for event handling
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private IUIElement hoveredElement = null;

    // Hover cooldown state: enforce a minimum time between enter/exit events.
    // Use monotonic nanosecond timing for correctness across system time jumps.
    private long lastHoverEventTimeNanos = 0L; // monotonic nanos
    private final long hoverCooldownNanos = 80_000_000L; // 80 ms in nanos
    private IUIElement pendingHover = null; // scheduled hover target
    private long pendingHoverFireTimeNanos = 0L; // nanos timestamp when pendingHover should be applied

    // Auto-centering: if enabled, UIScreen will center the root element in the viewport when its size changes
    private boolean autoCenterRoot = false;
    private Vector2f lastRootSize = null;
    private float autoCenterThreshold = 0.5f; // minimum size delta to trigger re-centering (pixels)

    // Vertical scroll state (in pixels)
    private float scrollY = 0f;            // positive -> content scrolled up (root moved up)
    private float maxScrollY = 0f;
    private boolean scrollEnabled = true;  // allow screens to scroll by default
    private float scrollSensitivity = 0.125f; // pixels per scroll unit
    private float scrollVelocity = 0f;     // for inertia
    private float inertiaDecay = 0.95f;    // slight inertia decay factor

    // Base root position (before scroll offset applied). Auto-centering updates this.
    private Vector2f baseRootPosition = new Vector2f(0, 0);

    public UIScreen(int screenWidth, int screenHeight) {
        this.viewport = new Rectangle2D.Float(0, 0, screenWidth, screenHeight);
        this.vscroll = new VerticalScrollBar(this);
        // position/size will be set in resize or setRoot
    }

    /**
     * Enable or disable automatic centering of the root element.
     */
    public void setAutoCenterRoot(boolean autoCenterRoot) {
        this.autoCenterRoot = autoCenterRoot;
        // reset last size so centering will run on next update
        this.lastRootSize = null;
    }

    /**
     * Set the threshold (in pixels) that determines when to re-center the root based on size changes.
     */
    public void setAutoCenterThreshold(float threshold) {
        this.autoCenterThreshold = Math.max(0f, threshold);
    }

    /**
     * Enable or disable vertical scrolling of the root content.
     */
    public void setScrollEnabled(boolean enabled) {
        this.scrollEnabled = enabled;
    }

    /**
     * Set scroll sensitivity in pixels per scroll unit.
     */
    public void setScrollSensitivity(float pixelsPerUnit) {
        this.scrollSensitivity = Math.max(1f, pixelsPerUnit);
    }

    /**
     * Set the root element of this screen.
     */
    public void setRoot(IUIElement root) {
        this.root = root;
        if (root != null) {
            // Initialize base root position from the element's current position
            Vector2f rp = root.getPosition();
            baseRootPosition.set(rp.x, rp.y);

            // Apply scroll offset on top
            applyScrollToRoot();

            root.markDirty(DirtyFlag.LAYOUT);
            // reset lastRootSize so auto-centering will apply on next update if enabled
            lastRootSize = null;

            // If the root is an HBox or VBox, propagate current viewport size so fillWidth/fillHeight
            // can work without manual setScreenWidth/setScreenHeight calls.
            if (root instanceof VBox vroot) {
                vroot.setScreenWidth((float) viewport.getWidth());
                vroot.setScreenHeight((float) viewport.getHeight());
            } else if (root instanceof HBox hroot) {
                hroot.setScreenWidth((float) viewport.getWidth());
                hroot.setScreenHeight((float) viewport.getHeight());
            }

            // Recompute scrolling bounds now that root exists
            recomputeScrollBounds();

            // initialize scrollbar size & position
            positionScrollbar();
        }
    }

    /**
     * Get the root element.
     */
    public IUIElement getRoot() {
        return root;
    }

    /**
     * Update the UI tree.
     */
    public void update(float deltaTime) {
        // Process any pending hover transitions that were deferred due to cooldown
        processPendingHover();

        if (root != null) {
            root.update(deltaTime);
            // update scrollbar as well
            vscroll.update(deltaTime);

            // After root update/layout, optionally auto-center the root element
            if (autoCenterRoot) {
                // Use reported size from root
                Vector2f rootSize = root.getSize();
                boolean shouldCenter = false;
                if (lastRootSize == null) {
                    shouldCenter = true;
                } else if (Math.abs(rootSize.x - lastRootSize.x) > autoCenterThreshold || Math.abs(rootSize.y - lastRootSize.y) > autoCenterThreshold) {
                    shouldCenter = true;
                }

                if (shouldCenter) {
                    float centerX = (float) viewport.getWidth() / 2f - rootSize.x / 2f;
                    float centerY = (float) viewport.getHeight() / 2f - rootSize.y / 2f;
                    // Update base position; scroll offset is applied on top
                    baseRootPosition.set(centerX, centerY);
                    root.setPosition(new Vector2f(baseRootPosition.x, baseRootPosition.y - scrollY));
                    lastRootSize = new Vector2f(rootSize);
                    // Reset scroll when auto-centering
                    scrollY = 0f;
                    scrollVelocity = 0f;
                }
            }

            // Recompute scroll bounds after layout pass and clamp scroll position
            recomputeScrollBounds();
            clampScroll();

            // Apply inertia
            scrollY += scrollVelocity;
            clampScroll();
            scrollVelocity *= inertiaDecay;
            if (Math.abs(scrollVelocity) < 0.1f) scrollVelocity = 0f;

            // Apply scroll by positioning the root relative to baseRootPosition
            applyScrollToRoot();

            // update scrollbar position/size each frame in case viewport/root sizes changed
            positionScrollbar();
        }
    }

    /**
     * Render the UI tree.
     */
    public void render(IRenderContext context) {
        if (root != null) {
            // Clear culled and rendered elements lists before rendering
            UIElement.clearCulledElements();
            UIElement.clearRenderedElements();

            root.render(context, viewport);
            // draw scrollbar on top
            vscroll.render(context, viewport);

            // Render debug overlay for culled elements if enabled
            if (UIElement.isDebugCulledEnabled()) {
                renderCulledElementsOverlay(context);
            }
        }
    }

    /**
     * Render a debug overlay showing the list of culled and rendered elements.
     */
    private void renderCulledElementsOverlay(IRenderContext context) {
        List<String> culledNames = UIElement.getCulledElementNames();
        List<String> renderedNames = UIElement.getRenderedElementNames();

        // Use Minecraft render context to draw text
        if (context instanceof grill24.gelatinui.gui.minecraft.MinecraftRenderContext) {
            grill24.gelatinui.gui.minecraft.MinecraftRenderContext mc = (grill24.gelatinui.gui.minecraft.MinecraftRenderContext) context;

            int x = 10;
            int y = 10;
            int lineHeight = 10;
            int backgroundColor = 0xC0000000; // Semi-transparent black
            int culledTextColor = 0xFFFF5555; // Red text for culled
            int renderedTextColor = 0xFF55FF55; // Green text for rendered

            // Calculate overlay size based on both lists
            int maxWidth = 250;
            int totalLines = 1 + (culledNames.isEmpty() ? 0 : culledNames.size() + 1) + (renderedNames.isEmpty() ? 0 : renderedNames.size() + 1);
            int height = totalLines * lineHeight + 10;

            // Draw background
            context.fill(x - 5, y - 5, x + maxWidth + 5, y + height, backgroundColor);

            // Draw title
            mc.drawString("Debug Elements:", x, y, 0xFFFFFFFF);
            y += lineHeight;

            // Draw culled elements
            if (!culledNames.isEmpty()) {
                mc.drawString("Culled (" + culledNames.size() + "):", x, y, 0xFFFFAAAA);
                y += lineHeight;
                for (String name : culledNames) {
                    mc.drawString("  - " + name, x, y, culledTextColor);
                    y += lineHeight;
                }
            }

            // Draw rendered elements
            if (!renderedNames.isEmpty()) {
                if (!culledNames.isEmpty()) {
                    y += lineHeight / 2; // Add small gap between sections
                }
                mc.drawString("Rendered (" + renderedNames.size() + "):", x, y, 0xFFAAFFAA);
                y += lineHeight;
                for (String name : renderedNames) {
                    mc.drawString("  - " + name, x, y, renderedTextColor);
                    y += lineHeight;
                }
            }
        }
    }

    /**
     * Handle mouse movement and hover events.
     */
    public void onMouseMove(int mouseX, int mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        if (root != null) {
            // If mouse is over scrollbar, prefer it as the hovered element
            IUIElement newHover = null;
            if (isPointInScrollbar(mouseX, mouseY)) {
                newHover = vscroll;
            } else {
                newHover = findElementAt(root, mouseX, mouseY);
            }

            // If hover hasn't changed, cancel any pending transition and return
            if (newHover == hoveredElement) {
                pendingHover = null;
                return;
            }

            long now = System.nanoTime();
            long elapsed = now - lastHoverEventTimeNanos;

            // If enough time has passed since the last hover event, fire immediately.
            if (elapsed >= hoverCooldownNanos && pendingHover == null) {
                // Exit previous hover
                if (hoveredElement != null) {
                    UIEvent exitEvent = new UIEvent(UIEvent.Type.HOVER_EXIT, hoveredElement, mouseX, mouseY);
                    hoveredElement.handleEvent(exitEvent);
                }

                // Enter new hover
                if (newHover != null) {
                    UIEvent enterEvent = new UIEvent(UIEvent.Type.HOVER_ENTER, newHover, mouseX, mouseY);
                    newHover.handleEvent(enterEvent);
                }

                hoveredElement = newHover;
                lastHoverEventTimeNanos = now;
                pendingHover = null; // clear any previously scheduled
            } else {
                // Within cooldown: schedule the transition to happen after the remaining cooldown.
                pendingHover = newHover;
                long fireTime = lastHoverEventTimeNanos + hoverCooldownNanos;
                if (fireTime < now) {
                    fireTime = now; // defensive: ensure not in past
                }
                pendingHoverFireTimeNanos = fireTime;
            }
        }
    }

    /**
     * Handle mouse click.
     */
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        if (root != null) {
            // If click hits scrollbar, dispatch there first
            if (isPointInScrollbar(mouseX, mouseY)) {
                UIEvent evt = new UIEvent(UIEvent.Type.CLICK, vscroll, mouseX, mouseY);
                return vscroll.handleEvent(evt);
            }
            IUIElement target = findElementAt(root, mouseX, mouseY);
            if (target != null) {
                UIEvent event = new UIEvent(UIEvent.Type.CLICK, target, mouseX, mouseY);
                return target.handleEvent(event);
            }
        }
        return false;
    }

    /**
     * Handle mouse scroll.
     * If a child under the pointer handles the scroll (consumes the event) we return true.
     * Otherwise, treat the scroll as a vertical scroll for the screen content (if enabled).
     */
    public boolean onMouseScroll(int mouseX, int mouseY, float scrollDelta) {
        if (root != null) {
            // If over scrollbar, let it be the target first
            IUIElement target = null;
            if (isPointInScrollbar(mouseX, mouseY)) {
                target = vscroll;
            } else {
                target = findElementAt(root, mouseX, mouseY);
            }
             if (target != null) {
                 UIEvent event = new UIEvent(UIEvent.Type.SCROLL, target, mouseX, mouseY, scrollDelta);
                 boolean consumed = target.handleEvent(event);
                 if (consumed) return true;
             }

             // Fallback: if nothing consumed the scroll, use it to scroll the root vertically
             if (scrollEnabled) {
                 // If there's no delta, nothing to do
                 if (Math.abs(scrollDelta) < 1e-6f) return false;

                 // Ensure scroll bounds are fresh in case layout hasn't run recently
                 recomputeScrollBounds();

                 // scrollDelta is typically 1.0/-1.0 per notch; positive should scroll up.
                 float dy = -scrollDelta * scrollSensitivity; // invert so positive scroll moves content down visually
                 scrollVelocity += dy;
                 return true;
             }
         }
         return false;
     }

    private boolean isPointInScrollbar(int mouseX, int mouseY) {
        if (vscroll == null) return false;
        // Use scrollbar bounds in global coordinates
        java.awt.geom.Rectangle2D bounds = vscroll.getBounds();
        return bounds != null && bounds.contains(mouseX, mouseY);
    }

    private void positionScrollbar() {
        // Place the scrollbar at right edge with margin
        float w = vscroll.getBarWidth();
        float h = (float) viewport.getHeight();
        float x = (float) viewport.getWidth() - w - scrollbarMargin;
        float y = 0f;
        vscroll.setPosition(new Vector2f(x, y));
        vscroll.setTrackSize(w, h);
    }

    // Expose helpers for VerticalScrollBar
    public float getContentHeight() {
        if (root == null) return 0f;
        return root.getSize().y;
    }

    public float getViewHeight() {
        return (float) viewport.getHeight();
    }

    public float getMaxScrollY() {
        return maxScrollY;
    }

    /**
     * Update viewport size.
     */
    public void resize(int width, int height) {
        this.viewport = new Rectangle2D.Float(0, 0, width, height);
        if (root != null) {
            root.markDirty(DirtyFlag.LAYOUT);
            lastRootSize = null; // force re-center next update if enabled
            // propagate new viewport size to root containers that support it
            if (root instanceof VBox vroot) {
                vroot.setScreenWidth((float) width);
                vroot.setScreenHeight((float) height);
            } else if (root instanceof HBox hroot) {
                hroot.setScreenWidth((float) width);
                hroot.setScreenHeight((float) height);
            }

            // Recompute scroll bounds when viewport changes
            recomputeScrollBounds();
            clampScroll();
            applyScrollToRoot();
        }
    }

    /**
     * Find the topmost element at the given coordinates.
     */
    private IUIElement findElementAt(IUIElement element, int x, int y) {
        if (!element.isVisible()) {
            return null;
        }

        Rectangle2D bounds = element.getBounds();
        if (!bounds.contains(x, y)) {
            return null;
        }

        // Check children first (front to back)
        if (element instanceof UIContainer) {
            List<IUIElement> children = ((UIContainer) element).getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                IUIElement child = children.get(i);
                IUIElement found = findElementAt(child, x, y);
                if (found != null) {
                    return found;
                }
            }
        }

        return element;
    }

    /**
     * Get the current viewport.
     */
    public Rectangle2D getViewport() {
        return viewport;
    }

    /**
     * Process any scheduled hover transition that was deferred due to cooldown.
     * This should be called frequently (e.g., from update) so delayed enter/exit events fire promptly.
     */
    private void processPendingHover() {
        if (pendingHover == null) {
            return;
        }

        long now = System.nanoTime();
        if (now < pendingHoverFireTimeNanos) {
            return; // not ready yet
        }

        IUIElement newHover = pendingHover;
        pendingHover = null;

        // If the scheduled target matches the current hovered element, nothing to do
        if (newHover == hoveredElement) {
            lastHoverEventTimeNanos = now;
            return;
        }

        // Exit previous hover
        if (hoveredElement != null) {
            UIEvent exitEvent = new UIEvent(UIEvent.Type.HOVER_EXIT, hoveredElement, lastMouseX, lastMouseY);
            hoveredElement.handleEvent(exitEvent);
        }

        // Enter new hover
        if (newHover != null) {
            UIEvent enterEvent = new UIEvent(UIEvent.Type.HOVER_ENTER, newHover, lastMouseX, lastMouseY);
            newHover.handleEvent(enterEvent);
        }

        hoveredElement = newHover;
        lastHoverEventTimeNanos = now;
    }

    // ----- Scrolling helpers -----
    private void recomputeScrollBounds() {
        if (root == null) {
            maxScrollY = 0f;
            return;
        }
        float contentHeight = root.getSize().y;
        float viewH = (float) viewport.getHeight();
        maxScrollY = Math.max(0f, contentHeight - viewH);
    }

    private void clampScroll() {
        if (scrollY < 0f) scrollY = 0f;
        if (scrollY > maxScrollY) scrollY = maxScrollY;
    }

    private void applyScrollToRoot() {
        if (root == null) return;
        float x = baseRootPosition.x;
        float y = baseRootPosition.y - scrollY; // root positioned as base minus scroll so content moves up when scrollY positive
        Vector2f pos = root.getPosition();
        if (Math.abs(pos.x - x) > 0.001f || Math.abs(pos.y - y) > 0.001f) {
            root.setPosition(new Vector2f(x, y));
        }
    }

    public void setScrollY(float y) {
        this.scrollY = y;
        scrollVelocity = 0f; // Reset velocity when scroll is set directly
        clampScroll();
        applyScrollToRoot();
    }

    public float getScrollY() {
        return scrollY;
    }
}
