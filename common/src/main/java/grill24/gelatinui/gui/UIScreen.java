package grill24.gelatinui.gui;

import java.util.ArrayList;
import java.util.List;

import grill24.gelatinui.gui.components.HBox;
import grill24.gelatinui.gui.components.VBox;
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

    public UIScreen(int screenWidth, int screenHeight) {
        this.viewport = new Rectangle2D.Float(0, 0, screenWidth, screenHeight);
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
     * Set the root element of this screen.
     */
    public void setRoot(IUIElement root) {
        this.root = root;
        if (root != null) {
            root.setPosition(new Vector2f(0, 0));
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
                    root.setPosition(new Vector2f(centerX, centerY));
                    lastRootSize = new Vector2f(rootSize);
                }
            }
        }
    }

    /**
     * Render the UI tree.
     */
    public void render(IRenderContext context) {
        if (root != null) {
            root.render(context, viewport);
        }
    }

    /**
     * Handle mouse movement and hover events.
     */
    public void onMouseMove(int mouseX, int mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        if (root != null) {
            IUIElement newHover = findElementAt(root, mouseX, mouseY);

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
     */
    public boolean onMouseScroll(int mouseX, int mouseY, float scrollDelta) {
        if (root != null) {
            IUIElement target = findElementAt(root, mouseX, mouseY);
            if (target != null) {
                UIEvent event = new UIEvent(UIEvent.Type.SCROLL, target, mouseX, mouseY, scrollDelta);
                return target.handleEvent(event);
            }
        }
        return false;
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
}
