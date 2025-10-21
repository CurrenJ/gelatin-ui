package grill24.gelatinui.gui;

import grill24.gelatinui.gui.components.Label;
import grill24.gelatinui.gui.components.VBox;
import grill24.gelatinui.gui.DirtyFlag;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Non-visual integration test for UIScreen auto-centering behavior.
 * Simulates label size changes and verifies the root VBox recenters.
 */
public class UIScreenCenteringTest {

    private static final float EPS = 0.01f;

    @Test
    public void testAutoCenteringOnInitialLayout() {
        int screenW = 800;
        int screenH = 600;

        UIScreen ui = new UIScreen(screenW, screenH);
        ui.setAutoCenterRoot(true);

        VBox root = new VBox().spacing(5).padding(10).alignment(VBox.Alignment.CENTER);

        Label a = new Label("A", 0xFFFFFFFF);
        Label b = new Label("B", 0xFFFFFFFF);
        Label c = new Label("C", 0xFFFFFFFF);

        // Set explicit sizes (simulate measured text sizes)
        a.setSize(new Vector2f(200f, 20f));
        b.setSize(new Vector2f(180f, 18f));
        c.setSize(new Vector2f(220f, 22f));

        root.addChild(a);
        root.addChild(b);
        root.addChild(c);

        ui.setRoot(root);

        // Ensure layout is recomputed by marking layout dirty (defensive)
        root.markDirty(DirtyFlag.LAYOUT);

        // Run update: should calculate layout and then auto-center root
        ui.update(0f);

        Vector2f vboxSize = root.getSize();
        Vector2f rootPos = root.getPosition();

        float expectedX = screenW / 2f - vboxSize.x / 2f;
        float expectedY = screenH / 2f - vboxSize.y / 2f;

        assertEquals(expectedX, rootPos.x, EPS, "Root X should be centered after initial layout");
        assertEquals(expectedY, rootPos.y, EPS, "Root Y should be centered after initial layout");
    }

    @Test
    public void testRecenteringWhenChildSizeChanges() {
        int screenW = 800;
        int screenH = 600;

        UIScreen ui = new UIScreen(screenW, screenH);
        ui.setAutoCenterRoot(true);

        VBox root = new VBox().spacing(5).padding(10).alignment(VBox.Alignment.CENTER);

        Label a = new Label("A", 0xFFFFFFFF);
        Label b = new Label("B", 0xFFFFFFFF);

        a.setSize(new Vector2f(100f, 20f));
        b.setSize(new Vector2f(100f, 20f));

        root.addChild(a);
        root.addChild(b);

        ui.setRoot(root);

        // Initial layout + center
        root.markDirty(DirtyFlag.LAYOUT);
        ui.update(0f);
        Vector2f initialSize = root.getSize();
        Vector2f initialPos = root.getPosition();

        float expectedInitialX = screenW / 2f - initialSize.x / 2f;
        float expectedInitialY = screenH / 2f - initialSize.y / 2f;

        assertEquals(expectedInitialX, initialPos.x, EPS, "Initial centered X");
        assertEquals(expectedInitialY, initialPos.y, EPS, "Initial centered Y");

        // Simulate hover scaling: increase size of label 'a'
        a.setSize(new Vector2f(160f, 32f)); // scaled up
        // Ensure parent layout recalculates
        a.markDirty(DirtyFlag.SIZE);

        ui.update(0f);

        Vector2f newSize = root.getSize();
        Vector2f newPos = root.getPosition();

        float expectedNewX = screenW / 2f - newSize.x / 2f;
        float expectedNewY = screenH / 2f - newSize.y / 2f;

        assertEquals(expectedNewX, newPos.x, EPS, "Re-centered X after child size change");
        assertEquals(expectedNewY, newPos.y, EPS, "Re-centered Y after child size change");

        // Ensure size actually changed
        assertEquals(true, Math.abs(newSize.x - initialSize.x) > EPS, "Size should have changed after scaling child");
    }
}
