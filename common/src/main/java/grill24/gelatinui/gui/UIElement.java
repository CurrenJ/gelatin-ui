package grill24.gelatinui.gui;

import org.joml.Vector2f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Base implementation of IUIElement with dirty-flag system and caching.
 * This class provides efficient update mechanisms to avoid redundant calculations.
 */
public abstract class UIElement implements IUIElement {
    // Debug rendering flags (global for all UI elements)
    private static boolean debugShowBounds = false;
    private static boolean debugShowGrid = false;
    private static boolean debugShowPadding = false;
    private static boolean debugShowCulled = false;

    // Debug tracking
    private String debugName = null;
    private static final List<String> culledElementNames = new ArrayList<>();
    private static final List<String> renderedElementNames = new ArrayList<>();

    // Parent-child relationships
    protected IUIElement parent;

    // Position and size (position is the current position, local to parent)
    protected Vector2f position = new Vector2f();
    protected Vector2f size = new Vector2f();

    // Target position for smooth interpolation (local)
    protected Vector2f targetPosition = new Vector2f();

    // Scale (applies to elements that use it for rendering/sizing)
    protected float currentScale = 1.0f;
    protected float targetScale = 1.0f;

    // Additional transient scale driven by animations (e.g., click bounce).
    // Multiplied by currentScale for rendering and bounds.
    protected float effectScale = 1.0f;

    // Visibility state
    protected boolean visible = true;

    // Dirty flag system
    protected boolean isDirty = true;
    protected EnumSet<DirtyFlag> dirtyFlags = EnumSet.noneOf(DirtyFlag.class);

    // Animation state
    protected boolean isAnimating = false;

    // Keyframe animation system (per element)
    private final List<grill24.gelatinui.gui.animation.Animation> animations = new ArrayList<>();

    // Interpolation speeds (per-second)
    // Increased speeds so tests and UI see noticeable motion within a few frames.
    private static final float POSITION_SPEED = 1.0f;
    private static final float SCALE_SPEED = 1.0f;

    // Cached bounds
    protected Rectangle2D cachedBounds;
    protected boolean boundsValid = false;

    // Event listeners
    protected List<UIEventListener> eventListeners = new ArrayList<>();

    @Override
    public void update(float deltaTime) {
        // Skip update if element is clean and not animating
        if (!needsUpdate()) {
            return;
        }

        // First: run element-level interpolation (position/scale) and step keyframe animations
        animate(deltaTime);

        // Snapshot existing flags and then clear them so flags set during onUpdate are distinct
        EnumSet<DirtyFlag> preFlags = EnumSet.copyOf(dirtyFlags);
        dirtyFlags.clear();

        // Process dirty flags that were present
        if (preFlags.contains(DirtyFlag.POSITION)) {
            onPositionChanged();
        }
        if (preFlags.contains(DirtyFlag.SIZE)) {
            onSizeChanged();
        }
        if (preFlags.contains(DirtyFlag.LAYOUT)) {
            recalculateLayout();
        }
        if (preFlags.contains(DirtyFlag.VISIBILITY)) {
            onVisibilityChanged();
        }

        // Custom update logic (may call markDirty() and add flags to dirtyFlags)
        onUpdate(deltaTime);

        // After onUpdate, dirtyFlags contains flags added during onUpdate.
        // Keep element dirty if new flags exist or if animating.
        isDirty = !dirtyFlags.isEmpty() || isAnimating;

        // Invalidate cached bounds if position/size/layout flags were present before or were added during onUpdate
        if (preFlags.contains(DirtyFlag.POSITION) || preFlags.contains(DirtyFlag.SIZE) || preFlags.contains(DirtyFlag.LAYOUT)
                || dirtyFlags.contains(DirtyFlag.POSITION) || dirtyFlags.contains(DirtyFlag.SIZE) || dirtyFlags.contains(DirtyFlag.LAYOUT)) {
            boundsValid = false;
        }
    }

    /**
     * Interpolate position and scale towards targets, and advance keyframe animations.
     * Marks dirty when values change.
     */
    protected void animate(float deltaTime) {
        boolean positionAnimating = false;
        boolean scaleAnimating = false;

        // Position interpolation (exponential smoothing)
        if (position.distance(targetPosition) > 0.001f) {
            float t = 1.0f - (float) Math.exp(-POSITION_SPEED * Math.max(0f, deltaTime));
            float nx = position.x + (targetPosition.x - position.x) * t;
            float ny = position.y + (targetPosition.y - position.y) * t;
            position.set(nx, ny);
            markDirty(DirtyFlag.POSITION);
            positionAnimating = true;
        }

        // Scale interpolation
        if (Math.abs(currentScale - targetScale) > 0.0001f) {
            float t = 1.0f - (float) Math.exp(-SCALE_SPEED * Math.max(0f, deltaTime));
            currentScale = currentScale + (targetScale - currentScale) * t;
            // Scaling affects size/layout in most elements
            markDirty(DirtyFlag.SIZE);
            scaleAnimating = true;
            // If close enough, snap
            if (Math.abs(currentScale - targetScale) <= 0.001f) {
                currentScale = targetScale;
                scaleAnimating = false;
            }
        }

        // Step keyframe animations (if any)
        boolean anyKeyframeAnimating = false;
        if (!animations.isEmpty()) {
            // Iterate over a copy to allow removal during iteration
            List<grill24.gelatinui.gui.animation.Animation> toRemove = new ArrayList<>();
            for (grill24.gelatinui.gui.animation.Animation anim : new ArrayList<>(animations)) {
                boolean alive = anim.update(Math.max(0f, deltaTime));
                if (!alive) {
                    toRemove.add(anim);
                } else {
                    anyKeyframeAnimating = true;
                }
            }
            if (!toRemove.isEmpty()) {
                animations.removeAll(toRemove);
            }
        }

        isAnimating = positionAnimating || scaleAnimating || anyKeyframeAnimating;
    }

    @Override
    public void render(IRenderContext context, Rectangle2D viewport) {
        if (!visible) {
            return;
        }

        if (!isInViewport(viewport)) {
            // Track culled elements for debug visualization
            if (debugShowCulled) {
                String elementName = debugName != null ? debugName : getDefaultDebugName();
                trackCulledElement(elementName);
            }
            return; // Culling: skip off-screen elements
        }

        // Apply hierarchical transform: translate by local position, scale by local scale, then render self and children within this transform.
        if (context instanceof grill24.gelatinui.gui.minecraft.MinecraftRenderContext) {
            grill24.gelatinui.gui.minecraft.MinecraftRenderContext mc = (grill24.gelatinui.gui.minecraft.MinecraftRenderContext) context;
            var pose = mc.getGraphics().pose();
            pose.pushPose();

            // translate-local-position *in parent space*
            pose.translate(position.x, position.y, 0);
            float combinedScale = currentScale * effectScale;
            pose.scale(combinedScale, combinedScale, 1.0f);

            // render self and children under same transform so children inherit the parent's transform
            renderSelf(context);
            renderChildren(context, viewport);

            // Render debug overlays if enabled
            if (debugShowBounds || debugShowGrid || debugShowPadding) {
                renderDebugOverlays(context);
            }

            pose.popPose();
        } else {
            // Non-Minecraft contexts: no pose stack available; render normally
            renderSelf(context);
            renderChildren(context, viewport);

            // Render debug overlays if enabled
            if (debugShowBounds || debugShowGrid || debugShowPadding) {
                renderDebugOverlays(context);
            }
        }

        // Track rendered elements for debug visualization
        if (debugShowCulled) {
            String elementName = debugName != null ? debugName : getDefaultDebugName();
            trackRenderedElement(elementName);
        }
    }

    /**
     * Render debug overlays showing bounds and/or grid.
     */
    protected void renderDebugOverlays(IRenderContext context) {
        // Render in local coordinates (0,0 to size.x, size.y)
        int x1 = 0;
        int y1 = 0;
        int x2 = (int) size.x;
        int y2 = (int) size.y;

        if (debugShowBounds) {
            // Semi-transparent white background
            int boundsColor = 0x40FFFFFF; // 25% opacity white
            context.fill(x1, y1, x2, y2, boundsColor);
        }

        if (debugShowPadding) {
            // If this element exposes a padding measure (like VBox/HBox), draw the padding strips
            float pad = 0f;
            if (this instanceof grill24.gelatinui.gui.components.VBox) {
                pad = ((grill24.gelatinui.gui.components.VBox) this).getPadding();
            } else if (this instanceof grill24.gelatinui.gui.components.HBox) {
                pad = ((grill24.gelatinui.gui.components.HBox) this).getPadding();
            }
            if (pad > 0f) {
                int p = Math.max(0, (int) Math.round(pad));
                int cx1 = x1 + p;
                int cy1 = y1 + p;
                int cx2 = x2 - p;
                int cy2 = y2 - p;

                // Draw the padding area (the strips between bounds and content)
                int padColor = 0x60FF8800; // semi-transparent orange to distinguish from bounds

                // Top padding strip
                if (cy1 > y1) {
                    context.fill(x1, y1, x2, cy1, padColor);
                }
                // Bottom padding strip
                if (cy2 < y2) {
                    context.fill(x1, cy2, x2, y2, padColor);
                }
                // Left padding strip (excluding corners already drawn)
                if (cx1 > x1) {
                    context.fill(x1, cy1, cx1, cy2, padColor);
                }
                // Right padding strip (excluding corners already drawn)
                if (cx2 < x2) {
                    context.fill(cx2, cy1, x2, cy2, padColor);
                }
            }
        }

        if (debugShowGrid) {
            // Draw grid lines at 1/4, 1/2, and 3/4 positions
            int gridColor = 0x80FFFFFF; // 50% opacity white

            // Compute fractional positions (clamped to integer coordinates)
            int[] xs = new int[3];
            int[] ys = new int[3];
            xs[0] = x1 + Math.max(0, (int) Math.floor(size.x * 0.25f));
            xs[1] = x1 + Math.max(0, (int) Math.floor(size.x * 0.5f));
            xs[2] = x1 + Math.max(0, (int) Math.floor(size.x * 0.75f));

            ys[0] = y1 + Math.max(0, (int) Math.floor(size.y * 0.25f));
            ys[1] = y1 + Math.max(0, (int) Math.floor(size.y * 0.5f));
            ys[2] = y1 + Math.max(0, (int) Math.floor(size.y * 0.75f));

            // Vertical quarter lines
            for (int xi : xs) {
                // ensure line is inside bounds
                if (xi > x1 && xi < x2) {
                    context.fill(xi, y1, xi + 1, y2, gridColor);
                }
            }

            // Horizontal quarter lines
            for (int yi : ys) {
                if (yi > y1 && yi < y2) {
                    context.fill(x1, yi, x2, yi + 1, gridColor);
                }
            }

            // Draw border in brighter color
            int borderColor = 0xFFFFFFFF; // Full opacity white
            // Top and bottom
            context.fill(x1, y1, x2, y1 + 1, borderColor);
            context.fill(x1, y2 - 1, x2, y2, borderColor);
            // Left and right
            context.fill(x1, y1, x1 + 1, y2, borderColor);
            context.fill(x2 - 1, y1, x2, y2, borderColor);
        }
    }

    @Override
    public Rectangle2D getBounds() {
        if (boundsValid && cachedBounds != null) {
            return cachedBounds;
        }

        cachedBounds = calculateBounds();
        boundsValid = true;
        return cachedBounds;
    }

    /**
     * Compute the global (screen) position by walking up the parent chain and applying parent scales.
     */
    public Vector2f getGlobalPosition() {
        Vector2f gp = new Vector2f(position);
        if (parent instanceof UIElement) {
            UIElement p = (UIElement) parent;
            Vector2f parentGlobal = p.getGlobalPosition();
            float parentScale = p.getGlobalScale();
            gp.mul(parentScale);
            gp.add(parentGlobal);
        }
        return gp;
    }

    /**
     * Compute the global scale by multiplying up the parent chain, including effectScale.
     */
    public float getGlobalScale() {
        float s = currentScale * effectScale;
        if (parent instanceof UIElement) {
            s *= ((UIElement) parent).getGlobalScale();
        }
        return s;
    }

    /**
     * Calculate bounds using global position and global scale.
     */
    protected Rectangle2D calculateBounds() {
        Vector2f gp = getGlobalPosition();
        float gs = getGlobalScale();
        return new Rectangle2D.Float(gp.x, gp.y, size.x * gs, size.y * gs);
    }

    @Override
    public Vector2f getPosition() {
        return new Vector2f(position);
    }

    @Override
    public void setPosition(Vector2f position) {
        if (!this.position.equals(position)) {
            this.position.set(position);
            this.targetPosition.set(position);
            markDirty(DirtyFlag.POSITION);
        }
    }

    /**
     * Set a target position to animate towards. If animate is false the position jumps immediately.
     */
    public void setTargetPosition(Vector2f target, boolean animate) {
        if (animate) {
            this.targetPosition.set(target);
            this.isAnimating = true;
            markDirty(DirtyFlag.POSITION);
        } else {
            setPosition(target);
        }
    }

    public Vector2f getTargetPosition() {
        return new Vector2f(targetPosition);
    }

    @Override
    public Vector2f getSize() {
        return new Vector2f(size);
    }

    public void setSize(Vector2f size) {
        if (!this.size.equals(size)) {
            this.size.set(size);
            markDirty(DirtyFlag.SIZE);
        }
    }

    /**
     * Set the target scale. If animate is true, smooth interpolation will occur.
     */
    public void setTargetScale(float scale, boolean animate) {
        if (animate) {
            this.targetScale = scale;
            this.isAnimating = true;
            markDirty(DirtyFlag.SIZE);
        } else {
            this.currentScale = scale;
            this.targetScale = scale;
            markDirty(DirtyFlag.SIZE);
        }
    }

    /**
     * Convenience: set target scale and animate.
     */
    public UIElement scale(float scale) {
        setTargetScale(scale, true);
        return this;
    }

    public float getCurrentScale() {
        return currentScale;
    }

    /**
     * Public accessor for transient animation-based scale.
     */
    public float getEffectScale() {
        return effectScale;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            markDirty(DirtyFlag.VISIBILITY);
        }
    }

    @Override
    public boolean needsUpdate() {
        return isDirty || isAnimating || !animations.isEmpty();
    }

    public boolean isAnimating() {
        return isAnimating || !animations.isEmpty();
    }

    @Override
    public void markDirty(DirtyFlag... flags) {
        // Add the new flags first
        boolean hadNewFlags = false;
        for (DirtyFlag flag : flags) {
            if (!dirtyFlags.contains(flag)) {
                dirtyFlags.add(flag);
                hadNewFlags = true;
            }
        }

        // Invalidate cached bounds if position or size changed
        if (dirtyFlags.contains(DirtyFlag.POSITION) ||
            dirtyFlags.contains(DirtyFlag.SIZE) ||
            dirtyFlags.contains(DirtyFlag.LAYOUT)) {
            boundsValid = false;
            // Invalidate children's bounds too since they depend on parent's transform
            invalidateChildBounds();
        }

        // Set dirty state
        boolean wasClean = !isDirty;
        isDirty = true;

        // Propagate dirty state up the tree only if we were clean or have new flags
        if ((wasClean || hadNewFlags) && parent != null) {
            onChildDirty(flags);
        }
    }

    /**
     * Invalidate bounds of all descendants recursively.
     * Called when this element's transform changes, which affects all child bounds.
     */
    protected void invalidateChildBounds() {
        // Default: no children (leaf element)
        // Containers will override this
    }

    @Override
    public IUIElement getParent() {
        return parent;
    }

    @Override
    public void setParent(IUIElement parent) {
        this.parent = parent;
    }

    @Override
    public boolean handleEvent(UIEvent event) {
        if (!visible) {
            return false;
        }

        // Try to handle event locally first
        if (onEvent(event)) {
            event.consume();
            return true;
        }

        // Dispatch to listeners
        for (UIEventListener listener : eventListeners) {
            listener.onEvent(event);
            if (event.isConsumed()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isInViewport(Rectangle2D viewport) {
        if (!visible) {
            return false;
        }

        Rectangle2D bounds = getBounds();
        return viewport.intersects(bounds);
    }

    /**
     * Add an event listener.
     */
    public void addEventListener(UIEventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Remove an event listener.
     */
    public void removeEventListener(UIEventListener listener) {
        eventListeners.remove(listener);
    }

    /**
     * Called when position changes. Override for custom behavior.
     */
    protected void onPositionChanged() {
        // Default: no-op
    }

    /**
     * Called when size changes. Override for custom behavior.
     */
    protected void onSizeChanged() {
        // Default: no-op
    }

    /**
     * Called when visibility changes. Override for custom behavior.
     */
    protected void onVisibilityChanged() {
        // Default: no-op
    }

    /**
     * Called when a child element is marked dirty.
     * Override to handle child changes efficiently.
     */
    protected void onChildDirty(DirtyFlag... flags) {
        // Default: propagate to parent
        if (parent != null && parent instanceof UIElement) {
            ((UIElement) parent).onChildDirty(flags);
        }
    }


    /**
     * Custom update logic. Override to implement element-specific updates.
     */
    protected abstract void onUpdate(float deltaTime);

    /**
     * Render this element (not children). Override to implement rendering.
     */
    protected abstract void renderSelf(IRenderContext context);

    /**
     * Render children. Override in container classes.
     */
    protected void renderChildren(IRenderContext context, Rectangle2D viewport) {
        // Default: no children (leaf element)
    }

    /**
     * Handle an event locally. Override to implement event handling.
     * @return true if event was consumed
     */
    protected boolean onEvent(UIEvent event) {
        return false;
    }

    /**
     * Keyframe animation helpers: manage per-element animations with optional channel exclusivity.
     */
    public void playAnimation(grill24.gelatinui.gui.animation.Animation animation) {
        if (animation == null) return;
        String channel = animation.getChannel();
        if (channel != null) {
            for (int i = animations.size() - 1; i >= 0; i--) {
                grill24.gelatinui.gui.animation.Animation a = animations.get(i);
                if (channel.equals(a.getChannel())) {
                    a.cancel();
                    animations.remove(i);
                }
            }
        }
        animations.add(animation);
        isAnimating = true;
    }

    public void cancelAnimationChannel(String channel) {
        if (channel == null) return;
        for (int i = animations.size() - 1; i >= 0; i--) {
            grill24.gelatinui.gui.animation.Animation a = animations.get(i);
            if (channel.equals(a.getChannel())) {
                a.cancel();
                animations.remove(i);
            }
        }
    }

    public void clearAnimations() {
        for (grill24.gelatinui.gui.animation.Animation a : animations) {
            a.cancel();
        }
        animations.clear();
        isAnimating = false;
    }

    /**
     * Convenience: play a click bounce animation on the effectScale channel.
     */
    public void playClickBounce() {
        java.util.List<grill24.gelatinui.gui.animation.Keyframe> keys = new java.util.ArrayList<>();
        keys.add(new grill24.gelatinui.gui.animation.Keyframe(0.0f, 1.0f));
        keys.add(new grill24.gelatinui.gui.animation.Keyframe(0.06f, 0.92f, grill24.gelatinui.gui.animation.Easing.EASE_OUT_CUBIC));
        keys.add(new grill24.gelatinui.gui.animation.Keyframe(0.14f, 1.06f, grill24.gelatinui.gui.animation.Easing.EASE_OUT_BACK));
        keys.add(new grill24.gelatinui.gui.animation.Keyframe(0.22f, 1.0f, grill24.gelatinui.gui.animation.Easing.EASE_IN_OUT_CUBIC));

        grill24.gelatinui.gui.animation.FloatKeyframeAnimation anim = new grill24.gelatinui.gui.animation.FloatKeyframeAnimation(
                "effectScale",
                keys,
                v -> {
                    this.effectScale = v;
                    markDirty(DirtyFlag.SIZE);
                },
                () -> {
                    this.effectScale = 1.0f;
                    markDirty(DirtyFlag.SIZE);
                }
        );
        playAnimation(anim);
    }

    /**
     * Interface for event listeners.
     */
    public interface UIEventListener {
        void onEvent(UIEvent event);
    }

    /**
     * Default layout recalculation hook. Containers override this to perform layout work.
     */
    protected void recalculateLayout() {
        // Default: no-op for leaf elements. Containers should override.
    }

    /**
     * Toggle debug mode to show semi-transparent bounds.
     */
    public static void toggleDebugBounds() {
        debugShowBounds = !debugShowBounds;
    }

    /**
     * Toggle debug mode to show grid lines over bounds.
     */
    public static void toggleDebugGrid() {
        debugShowGrid = !debugShowGrid;
    }

    /**
     * Toggle debug mode to show padding area for containers.
     */
    public static void toggleDebugPadding() {
        debugShowPadding = !debugShowPadding;
    }

    /**
     * Toggle debug mode to show culled elements.
     */
    public static void toggleDebugCulled() {
        debugShowCulled = !debugShowCulled;
    }

    /**
     * Get current debug bounds state.
     */
    public static boolean isDebugBoundsEnabled() {
        return debugShowBounds;
    }

    /**
     * Get current debug grid state.
     */
    public static boolean isDebugGridEnabled() {
        return debugShowGrid;
    }

    /**
     * Get current debug padding state.
     */
    public static boolean isDebugPaddingEnabled() {
        return debugShowPadding;
    }

    /**
     * Get current debug culled state.
     */
    public static boolean isDebugCulledEnabled() {
        return debugShowCulled;
    }

    /**
     * Set the debug name for this element (for debugging purposes).
     */
    public void setDebugName(String name) {
        this.debugName = name;
    }

    /**
     * Get the debug name for this element.
     */
    public String getDebugName() {
        return debugName;
    }

    /**
     * Get a default debug name for this element based on its type and properties.
     * Subclasses can override this to provide more specific debug names.
     */
    protected String getDefaultDebugName() {
        // Default implementation: use class name with position info
        String className = this.getClass().getSimpleName();
        return className + "@(" + (int)position.x + "," + (int)position.y + ")";
    }

    /**
     * Get the list of names for culled elements (static, for debug visualization).
     */
    public static List<String> getCulledElementNames() {
        return culledElementNames;
    }

    /**
     * Track a culled element's name for debug visualization.
     */
    public static void trackCulledElement(String name) {
        if (!culledElementNames.contains(name)) {
            culledElementNames.add(name);
        }
    }

    /**
     * Clear the tracked culled elements (for debug visualization).
     */
    public static void clearCulledElements() {
        culledElementNames.clear();
    }

    /**
     * Get the list of names for rendered elements (static, for debug visualization).
     */
    public static List<String> getRenderedElementNames() {
        return renderedElementNames;
    }

    /**
     * Track a rendered element's name for debug visualization.
     */
    public static void trackRenderedElement(String name) {
        if (!renderedElementNames.contains(name)) {
            renderedElementNames.add(name);
        }
    }

    /**
     * Clear the tracked rendered elements (for debug visualization).
     */
    public static void clearRenderedElements() {
        renderedElementNames.clear();
    }
}
