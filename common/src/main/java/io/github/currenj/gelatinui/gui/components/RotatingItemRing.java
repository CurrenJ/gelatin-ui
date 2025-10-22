package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IUIElement;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIContainer;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.UIEvent;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2f;

import java.util.List;

/**
 * A container that renders a variable number of items arranged on a rotating ring.
 * - The ring rotates slowly by default.
 * - Hovering any item eases the rotation speed to a halt and enlarges the hovered item.
 * - Clicking an item selects it: it eases to the center; the rest smoothly close the gap and re-space.
 * - Clicking the selected item again unselects it and it eases back into the ring.
 *
 * All position and scale transitions are continuous (exponential smoothing via target positions/scales),
 * avoiding any jumps or snaps.
 */
public class RotatingItemRing extends UIContainer {
    // Geometry
    private float radius = 64f;                 // ring radius in local coords
    private float defaultItemScale = 1.0f;      // base element scale for items
    private float hoverItemScale = 1.25f;       // scale when hovered
    private float selectedItemScale = 1.35f;    // scale when selected at center

    // Rotation dynamics
    private float baseAngle = 0f;               // current ring rotation angle (radians)
    private float angularSpeed = 0.5f;          // current angular speed (rad/s)
    private float targetAngularSpeed = 0.5f;    // target angular speed (rad/s)
    private float defaultAngularSpeed = 0.5f;   // default spin when idle

    // Ring spacing
    private float currentStep = (float) (Math.PI * 2f / Math.max(1, 6)); // radians between items
    private float targetStep = currentStep;

    // State
    private int hoveredIndex = -1;
    private int selectedIndex = -1;

    // Smoothing constants for internal interpolation (per-second rates)
    private static final float SPEED_SMOOTH = 3.0f;      // for angular speed changes
    private static final float STEP_SMOOTH = 6.0f;       // for step/gap changes

    // Internal: track if layout/positions should be recomputed
    private boolean layoutDirty = true;

    public RotatingItemRing() {
        // Provide a sensible default container size so it can render standalone
        this.size.set(160, 160);
    }

    // Fluent configuration
    public RotatingItemRing radius(float radius) {
        if (this.radius != radius) {
            this.radius = Math.max(0f, radius);
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public RotatingItemRing defaultAngularSpeed(float radPerSec) {
        this.defaultAngularSpeed = Math.max(0f, radPerSec);
        this.targetAngularSpeed = this.defaultAngularSpeed;
        return this;
    }

    public RotatingItemRing defaultItemScale(float scale) {
        this.defaultItemScale = Math.max(0.01f, scale);
        return this;
    }

    public RotatingItemRing hoverItemScale(float scale) {
        this.hoverItemScale = Math.max(0.01f, scale);
        return this;
    }

    public RotatingItemRing selectedItemScale(float scale) {
        this.selectedItemScale = Math.max(0.01f, scale);
        return this;
    }

    /**
     * Replace ring contents with the provided item stacks. Existing children are cleared.
     */
    public RotatingItemRing setItems(List<ItemStack> stacks) {
        clearChildren();
        if (stacks != null) {
            for (ItemStack s : stacks) {
                addItem(s);
            }
        }
        selectedIndex = -1;
        hoveredIndex = -1;
        updateTargetStep();
        baseAngle = 0f;
        return this;
    }

    /**
     * Add a single item to the ring (at the end).
     */
    public RotatingItemRing addItem(ItemStack stack) {
        ItemRenderer child = new ItemRenderer(stack);
        // Start with base scale
        child.setTargetScale(defaultItemScale, false);
        addChild(child);
        // When children change, we should re-space
        updateTargetStep();
        return this;
    }

    /**
     * Remove the child at the given index (if valid).
     */
    public void removeAt(int index) {
        if (index < 0 || index >= children.size()) return;
        IUIElement child = children.get(index);
        removeChild(child);
        if (selectedIndex == index) {
            selectedIndex = -1;
        } else if (selectedIndex > index) {
            selectedIndex -= 1; // maintain selection index alignment
        }
        if (hoveredIndex == index) hoveredIndex = -1;
        if (hoveredIndex > index) hoveredIndex -= 1;
        updateTargetStep();
    }

    /**
     * Programmatically select an item; -1 clears selection.
     */
    public void select(int index) {
        if (index < -1 || index >= children.size()) return;
        if (selectedIndex == index) return;

        if (index == -1) {
            // Unselect: open a gap of 0 progressively (reverse of close)
            selectedIndex = -1;
            // returning to full ring size
            updateTargetStep();
        } else {
            // Select this index
            int previousSelected = selectedIndex;
            selectedIndex = index;
            // New target step excludes the selected item
            updateTargetStep();

            // A small click bounce for feedback
            IUIElement elem = children.get(index);
            if (elem instanceof UIElement uie) {
                uie.playClickBounce();
            }

            // If we had a previously selected item, ensure it smoothly returns to ring
            if (previousSelected >= 0 && previousSelected < children.size()) {
                IUIElement prev = children.get(previousSelected);
                if (prev instanceof UIElement uip) {
                    uip.setTargetScale(defaultItemScale, true);
                }
            }
        }
        markDirty(DirtyFlag.LAYOUT);
    }

    public int getSelectedIndex() { return selectedIndex; }

    // ----- Layout/Update -----

    @Override
    protected void performLayout() {
        // No fixed child sizing here; positions are driven in onUpdate.
        // Ensure our step reflects current counts.
        updateTargetStep();
        layoutDirty = false;
    }

    private void updateTargetStep() {
        int ringCount = getRingCount();
        float newTargetStep = ringCount > 0 ? (float) (Math.PI * 2.0 / ringCount) : (float) (Math.PI * 2.0);
        // If selection is active, ringCount excludes the selected, which is desired
        this.targetStep = newTargetStep;
    }

    private int getRingCount() {
        return selectedIndex >= 0 ? Math.max(0, children.size() - 1) : children.size();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (layoutDirty) {
            performLayout();
        }

        float dt = Math.max(0f, deltaTime);

        // Smooth angular speed towards target
        if (Math.abs(angularSpeed - targetAngularSpeed) > 1e-4f) {
            float t = 1.0f - (float) Math.exp(-SPEED_SMOOTH * dt);
            angularSpeed += (targetAngularSpeed - angularSpeed) * t;
        }

        // Advance base angle
        baseAngle += angularSpeed * dt;
        // keep bounded
        if (baseAngle > Math.PI * 2f) baseAngle -= (float) (Math.PI * 2f);
        if (baseAngle < -Math.PI * 2f) baseAngle += (float) (Math.PI * 2f);

        // Ease step towards target
        if (Math.abs(currentStep - targetStep) > 1e-4f) {
            float t = 1.0f - (float) Math.exp(-STEP_SMOOTH * dt);
            currentStep += (targetStep - currentStep) * t;
        }

        // Determine center within this container
        float cx = size.x * 0.5f;
        float cy = size.y * 0.5f;
        float r = Math.max(0f, radius);
        if (r <= 0f) {
            // Auto radius from size and an estimated item size (16px at scale)
            float est = 16f * defaultItemScale;
            r = Math.max(0f, Math.min(size.x, size.y) * 0.5f - est);
        }

        // Place children
        int N = children.size();

        for (int i = 0; i < N; i++) {
            IUIElement child = children.get(i);
            boolean isSelected = (i == selectedIndex);

            if (!(child instanceof UIElement uiChild)) {
                continue;
            }

            Vector2f childSize = child.getSize();

            if (isSelected) {
                // Ease towards center and selected scale
                float s = selectedItemScale;
                Vector2f targetPos = new Vector2f(cx - 0.5f * childSize.x * s, cy - 0.5f * childSize.y * s);
                uiChild.setTargetPosition(targetPos, true);
                uiChild.setTargetScale(s, true);
                continue;
            }

            // Compute ring slot for this child
            int slotIndex = i;
            if (selectedIndex >= 0 && i > selectedIndex) {
                slotIndex = i - 1;
            }

            // Angle for this slot
            float angle = baseAngle + slotIndex * currentStep;

            float s = (hoveredIndex == i) ? hoverItemScale : defaultItemScale;

            // Target local position: center plus polar offset minus half-size to align to top-left origin
            float px = cx + (float) Math.cos(angle) * r - 0.5f * childSize.x * s;
            float py = cy + (float) Math.sin(angle) * r - 0.5f * childSize.y * s;
            uiChild.setTargetPosition(new Vector2f(px, py), true);

            // Target scale for hover vs normal
            uiChild.setTargetScale(s, true);
        }

        // No explicit markDirty here; children's own position/scale dirties will bubble as needed.
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        // The ring itself has no direct rendering; children do the drawing.
        // Optional: could render a subtle circle/guide if desired.
    }

    // ----- Event handling -----

    @Override
    protected boolean onEvent(UIEvent event) {
        // We receive events targeted at children. Determine which direct child (if any) this is for.
        int childIndex = resolveDirectChildIndex(event.getTarget());
        if (childIndex == -1) {
            return false;
        }

        switch (event.getType()) {
            case HOVER_ENTER -> {
                hoveredIndex = childIndex;
                targetAngularSpeed = 0f; // slow to a halt
                // hover scale is handled in onUpdate via hoveredIndex
                return false; // don't consume; allow other listeners if any
            }
            case HOVER_EXIT -> {
                if (hoveredIndex == childIndex) {
                    hoveredIndex = -1;
                }
                targetAngularSpeed = defaultAngularSpeed; // resume
                return false;
            }
            case CLICK -> {
                if (selectedIndex == childIndex) {
                    // Unselect
                    select(-1);
                } else {
                    select(childIndex);
                }
                return true; // consume click
            }
            default -> {
                return false;
            }
        }
    }

    private int resolveDirectChildIndex(IUIElement target) {
        if (target == null) return -1;
        // Walk up to find which of our direct children contains the target
        IUIElement cur = target;
        while (cur != null && cur.getParent() != null && cur.getParent() != this) {
            cur = cur.getParent();
        }
        if (cur != null && cur.getParent() == this) {
            for (int i = 0; i < children.size(); i++) {
                if (children.get(i) == cur) return i;
            }
        }
        return -1;
    }

    /** Ensure any added child is wired for hover/click events that affect the ring. */
    @Override
    public void addChild(IUIElement child) {
        super.addChild(child);
        wireChildEvents(child);
    }

    private void wireChildEvents(IUIElement child) {
        if (!(child instanceof UIElement uiElem)) return;
        uiElem.addEventListener(event -> {
            int idx = indexOfChild(child);
            if (idx == -1) return;
            switch (event.getType()) {
                case HOVER_ENTER -> {
                    hoveredIndex = idx;
                    targetAngularSpeed = 0f;
                }
                case HOVER_EXIT -> {
                    if (hoveredIndex == idx) hoveredIndex = -1;
                    targetAngularSpeed = defaultAngularSpeed;
                }
                case CLICK -> {
                    if (selectedIndex == idx) {
                        select(-1);
                    } else {
                        select(idx);
                    }
                    event.consume();
                }
                default -> {
                }
            }
        });
    }

    private int indexOfChild(IUIElement child) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) == child) return i;
        }
        return -1;
    }

    @Override
    public boolean needsUpdate() {
        return super.needsUpdate()
                || Math.abs(angularSpeed - targetAngularSpeed) > 1e-4f
                || Math.abs(currentStep - targetStep) > 1e-4f
                || Math.abs(angularSpeed) > 1e-6f; // keep ticking while spinning
    }
}
