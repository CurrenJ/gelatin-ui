package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.components.Panel;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SizeAlignmentTest {

    @Test
    public void testNoAlignment() {
        Panel panel = new Panel();
        panel.setSize(100f, 50f);

        assertEquals(100f, panel.getSize().x, 0.01f);
        assertEquals(50f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testWidthAlignment() {
        Panel panel = new Panel();
        panel.alignWidthToMultiple(3);
        panel.setSize(100f, 50f);

        // Width should round up to nearest multiple of 3: 102
        assertEquals(102f, panel.getSize().x, 0.01f);
        assertEquals(50f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testHeightAlignment() {
        Panel panel = new Panel();
        panel.alignHeightToMultiple(3);
        panel.setSize(100f, 50f);

        assertEquals(100f, panel.getSize().x, 0.01f);
        // Height should round up to nearest multiple of 3: 51
        assertEquals(51f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testBothAlignment() {
        Panel panel = new Panel();
        panel.alignSizeToMultiple(3);
        panel.setSize(100f, 50f);

        // Both should round up to nearest multiple of 3: 102 x 51
        assertEquals(102f, panel.getSize().x, 0.01f);
        assertEquals(51f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testAlignmentWithExactMultiple() {
        Panel panel = new Panel();
        panel.alignSizeToMultiple(3);
        panel.setSize(99f, 51f);

        // 99 is already a multiple of 3, should stay 99
        // 51 is already a multiple of 3, should stay 51
        assertEquals(99f, panel.getSize().x, 0.01f);
        assertEquals(51f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testAlignmentWithDifferentMultiples() {
        Panel panel = new Panel();
        panel.alignWidthToMultiple(5);
        panel.alignHeightToMultiple(7);
        panel.setSize(23f, 20f);

        // Width rounds to 25 (nearest multiple of 5)
        // Height rounds to 21 (nearest multiple of 7)
        assertEquals(25f, panel.getSize().x, 0.01f);
        assertEquals(21f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testAlignmentDisabled() {
        Panel panel = new Panel();
        panel.alignSizeToMultiple(3);
        panel.setSize(100f, 50f);

        assertEquals(102f, panel.getSize().x, 0.01f);
        assertEquals(51f, panel.getSize().y, 0.01f);

        // Disable alignment
        panel.alignSizeToMultiple(0);
        panel.setSize(100f, 50f);

        // Should now use exact size
        assertEquals(100f, panel.getSize().x, 0.01f);
        assertEquals(50f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testGettersReturnCorrectValues() {
        Panel panel = new Panel();

        assertEquals(0, panel.getAlignWidthToMultiple());
        assertEquals(0, panel.getAlignHeightToMultiple());
        assertEquals(0, panel.getAlignWidthOffset());
        assertEquals(0, panel.getAlignHeightOffset());

        panel.alignWidthToMultiple(3);
        panel.alignHeightToMultiple(5);

        assertEquals(3, panel.getAlignWidthToMultiple());
        assertEquals(5, panel.getAlignHeightToMultiple());
        assertEquals(0, panel.getAlignWidthOffset());
        assertEquals(0, panel.getAlignHeightOffset());
    }

    @Test
    public void testInvalidAlignmentThrowsException() {
        Panel panel = new Panel();

        assertThrows(IllegalArgumentException.class, () -> {
            panel.alignWidthToMultiple(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            panel.alignHeightToMultiple(-5);
        });
    }

    // Tests for offset functionality (y-intercept)

    @Test
    public void testAlignmentWithOffset() {
        Panel panel = new Panel();
        panel.alignSizeToMultiple(7, 8);  // Pattern: 8 + 7*x
        panel.setSize(100f, 50f);

        // Width: 100 -> 8 + 7*ceil((100-8)/7) = 8 + 7*14 = 106
        // Height: 50 -> 8 + 7*ceil((50-8)/7) = 8 + 7*6 = 50 (exact match!)
        assertEquals(106f, panel.getSize().x, 0.01f);
        assertEquals(50f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testAlignmentWithOffsetExactMatch() {
        Panel panel = new Panel();
        panel.alignSizeToMultiple(7, 8);
        panel.setSize(50f, 36f);  // 8 + 7*6 = 50, 8 + 7*4 = 36

        // Both are exact matches, should stay unchanged
        assertEquals(50f, panel.getSize().x, 0.01f);
        assertEquals(36f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testWidthOnlyAlignmentWithOffset() {
        Panel panel = new Panel();
        panel.alignWidthToMultiple(7, 8);
        panel.setSize(85f, 40f);

        // Width: 85 -> 8 + 7*ceil((85-8)/7) = 8 + 7*11 = 85 (exact!)
        // Height: no alignment, stays 40
        assertEquals(85f, panel.getSize().x, 0.01f);
        assertEquals(40f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testHeightOnlyAlignmentWithOffset() {
        Panel panel = new Panel();
        panel.alignHeightToMultiple(7, 8);
        panel.setSize(100f, 40f);

        // Width: no alignment, stays 100
        // Height: 40 -> 8 + 7*ceil((40-8)/7) = 8 + 7*5 = 43
        assertEquals(100f, panel.getSize().x, 0.01f);
        assertEquals(43f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testOffsetGetters() {
        Panel panel = new Panel();
        panel.alignWidthToMultiple(7, 8);
        panel.alignHeightToMultiple(5, 10);

        assertEquals(7, panel.getAlignWidthToMultiple());
        assertEquals(5, panel.getAlignHeightToMultiple());
        assertEquals(8, panel.getAlignWidthOffset());
        assertEquals(10, panel.getAlignHeightOffset());
    }

    @Test
    public void testInvalidOffsetThrowsException() {
        Panel panel = new Panel();

        assertThrows(IllegalArgumentException.class, () -> {
            panel.alignWidthToMultiple(7, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            panel.alignHeightToMultiple(5, -10);
        });
    }

    @Test
    public void testSizeSmallerThanOffset() {
        Panel panel = new Panel();
        panel.alignSizeToMultiple(7, 20);  // Pattern: 20 + 7*x
        panel.setSize(10f, 15f);

        // Both are smaller than offset, should round to offset itself (20 + 7*0 = 20)
        assertEquals(20f, panel.getSize().x, 0.01f);
        assertEquals(20f, panel.getSize().y, 0.01f);
    }

    @Test
    public void testDifferentOffsetsForWidthAndHeight() {
        Panel panel = new Panel();
        panel.alignWidthToMultiple(5, 10);   // Pattern: 10 + 5*x
        panel.alignHeightToMultiple(3, 7);   // Pattern: 7 + 3*x
        panel.setSize(27f, 20f);

        // Width: 27 -> 10 + 5*ceil((27-10)/5) = 10 + 5*4 = 30
        // Height: 20 -> 7 + 3*ceil((20-7)/3) = 7 + 3*5 = 22
        assertEquals(30f, panel.getSize().x, 0.01f);
        assertEquals(22f, panel.getSize().y, 0.01f);
    }
}
