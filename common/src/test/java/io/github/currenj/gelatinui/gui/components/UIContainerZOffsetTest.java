package io.github.currenj.gelatinui.gui.components;

import org.junit.jupiter.api.Test;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The container applies each child's z offset (IUIElement#setZOffset) around that child alone,
 * skipping zero, and pops in the reverse order it pushed.
 * <p>That is the seam 1.21.1 needs: its GUI pose is a real 3D PoseStack, so children are painted
 * depth-tested and each leaf renderer bakes in its own depth, which would otherwise order an
 * overlap by depth rather than by paint order. A container that pushed the wrong offsets, leaked
 * a push, or applied an offset to siblings too would silently reintroduce that.
 */
public class UIContainerZOffsetTest {

    /** TestRenderContext plus a record of the z offsets pushed, and a push/pop balance count. */
    private static final class ZRecordingContext extends TestRenderContext {
        final List<Float> pushed = new ArrayList<>();
        int open;

        @Override
        public void pushZOffset(float zOffset) {
            pushed.add(zOffset);
            open++;
        }

        @Override
        public void popZOffset() {
            open--;
        }
    }

    private static Panel panel(float size, float zOffset) {
        Panel panel = new Panel();
        panel.setSize(size, size);
        panel.setZOffset(zOffset);
        return panel;
    }

    private static ZRecordingContext render(ManualContainer container) {
        container.forceLayout();
        ZRecordingContext context = new ZRecordingContext();
        container.render(context, new Rectangle2D.Float(0, 0, 400, 400));
        return context;
    }

    @Test
    public void appliesOnlyNonZeroOffsetsInPaintOrder() {
        ManualContainer container = new ManualContainer();
        container.setSize(100, 100);
        container.addChildAt(panel(10, 0f), 10, 10);
        container.addChildAt(panel(10, 100f), 20, 20);
        container.addChildAt(panel(10, 200f), 30, 30);

        ZRecordingContext context = render(container);

        assertEquals(List.of(100f, 200f), context.pushed,
                "only non-zero offsets push, in child order");
        assertEquals(0, context.open, "every push is popped");
    }

    @Test
    public void offsetOnAContainerWrapsItsWholeSubtree() {
        // A parent's push must stay open while its own children push theirs, so a nested
        // container's offset composes with — rather than replaces — the one under it.
        ManualContainer outer = new ManualContainer();
        outer.setSize(100, 100);
        ManualContainer inner = new ManualContainer();
        inner.setSize(50, 50);
        inner.setZOffset(50f);
        inner.addChildAt(panel(10, 10f), 5, 5);
        outer.addChildAt(inner, 25, 25);

        ZRecordingContext context = render(outer);

        assertEquals(List.of(50f, 10f), context.pushed,
                "the subtree's push happens inside its parent's");
        assertEquals(0, context.open, "every push is popped");
    }
}
