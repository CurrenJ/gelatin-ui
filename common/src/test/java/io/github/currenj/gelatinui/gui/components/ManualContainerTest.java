package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.IUIElement;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class ManualContainerTest {
    
    private ManualContainer container;
    
    @BeforeEach
    public void setUp() {
        container = new ManualContainer();
        container.setSize(200, 100);
    }
    
    @Test
    public void testDefaultConfiguration() {
        assertEquals(0, container.getMaxChildren(), "Default max children should be 0 (unlimited)");
        assertEquals(0, container.getChildCount(), "Container should start empty");
        assertFalse(container.isFull(), "Empty container should not be full");
        assertEquals(Integer.MAX_VALUE, container.getRemainingSlots(), "Unlimited container should have MAX_VALUE slots");
        assertFalse(container.isAutoSizeToChildren(), "Auto-size to children should be disabled by default");
    }
    
    @Test
    public void testAddChildAtPosition() {
        Panel child = new Panel();
        child.setSize(20, 20);
        
        container.addChildAt(child, 50, 50);
        
        assertEquals(1, container.getChildCount(), "Container should have one child");
        
        // Force layout to calculate positions
        container.forceLayout();
        
        // Child should be centered at (50, 50), so top-left should be at (40, 40)
        Vector2f childPos = child.getPosition();
        assertEquals(40f, childPos.x, 0.01f, "Child X position should center at 50");
        assertEquals(40f, childPos.y, 0.01f, "Child Y position should center at 50");
    }
    
    @Test
    public void testAddChildAtPositionWithVector() {
        Panel child = new Panel();
        child.setSize(30, 30);
        
        container.addChildAt(child, new Vector2f(75, 25));
        
        assertEquals(1, container.getChildCount());
        
        container.forceLayout();
        
        // Child centered at (75, 25), so top-left at (60, 10)
        Vector2f childPos = child.getPosition();
        assertEquals(60f, childPos.x, 0.01f);
        assertEquals(10f, childPos.y, 0.01f);
    }
    
    @Test
    public void testMultipleChildrenAtDifferentPositions() {
        Panel child1 = new Panel();
        child1.setSize(20, 20);
        
        Panel child2 = new Panel();
        child2.setSize(30, 30);
        
        Panel child3 = new Panel();
        child3.setSize(40, 40);
        
        container.addChildAt(child1, 30, 50);
        container.addChildAt(child2, 100, 50);
        container.addChildAt(child3, 170, 50);
        
        assertEquals(3, container.getChildCount());
        
        container.forceLayout();
        
        // Verify each child is centered at its position
        assertEquals(20f, child1.getPosition().x, 0.01f); // 30 - 20/2
        assertEquals(40f, child1.getPosition().y, 0.01f); // 50 - 20/2
        
        assertEquals(85f, child2.getPosition().x, 0.01f); // 100 - 30/2
        assertEquals(35f, child2.getPosition().y, 0.01f); // 50 - 30/2
        
        assertEquals(150f, child3.getPosition().x, 0.01f); // 170 - 40/2
        assertEquals(30f, child3.getPosition().y, 0.01f); // 50 - 40/2
    }
    
    @Test
    public void testMaxChildrenLimit() {
        container.maxChildren(3);
        
        assertEquals(3, container.getMaxChildren());
        assertEquals(3, container.getRemainingSlots());
        
        Panel child1 = new Panel();
        Panel child2 = new Panel();
        Panel child3 = new Panel();
        
        container.addChildAt(child1, 10, 10);
        assertEquals(2, container.getRemainingSlots());
        
        container.addChildAt(child2, 20, 10);
        assertEquals(1, container.getRemainingSlots());
        
        container.addChildAt(child3, 30, 10);
        assertEquals(0, container.getRemainingSlots());
        assertTrue(container.isFull());
        
        // Try to add one more - should throw exception
        Panel child4 = new Panel();
        assertThrows(IllegalStateException.class, () -> {
            container.addChildAt(child4, 40, 10);
        }, "Should throw exception when exceeding max children");
        
        assertEquals(3, container.getChildCount(), "Child count should remain at max");
    }
    
    @Test
    public void testMaxChildrenZeroMeansUnlimited() {
        container.maxChildren(0);
        
        assertFalse(container.isFull());
        assertEquals(Integer.MAX_VALUE, container.getRemainingSlots());
        
        // Should be able to add many children
        for (int i = 0; i < 10; i++) {
            Panel child = new Panel();
            container.addChildAt(child, i * 10, 10);
        }
        
        assertEquals(10, container.getChildCount());
        assertFalse(container.isFull());
    }
    
    @Test
    public void testInvalidMaxChildren() {
        assertThrows(IllegalArgumentException.class, () -> {
            container.maxChildren(-1);
        }, "Should throw exception for negative max children");
    }
    
    @Test
    public void testUpdateChildPosition() {
        Panel child = new Panel();
        child.setSize(20, 20);
        
        container.addChildAt(child, 50, 50);
        container.forceLayout();
        
        // Initial position centered at (50, 50)
        assertEquals(40f, child.getPosition().x, 0.01f);
        assertEquals(40f, child.getPosition().y, 0.01f);
        
        // Update position
        container.updateChildPosition(child, 100, 80);
        container.forceLayout();
        
        // New position centered at (100, 80)
        assertEquals(90f, child.getPosition().x, 0.01f);
        assertEquals(70f, child.getPosition().y, 0.01f);
        
        // Verify stored position
        Vector2f storedPos = container.getChildPosition(child);
        assertEquals(100f, storedPos.x, 0.01f);
        assertEquals(80f, storedPos.y, 0.01f);
    }
    
    @Test
    public void testUpdateChildPositionWithVector() {
        Panel child = new Panel();
        child.setSize(20, 20);
        
        container.addChildAt(child, 50, 50);
        container.updateChildPosition(child, new Vector2f(75, 25));
        container.forceLayout();
        
        assertEquals(65f, child.getPosition().x, 0.01f);
        assertEquals(15f, child.getPosition().y, 0.01f);
    }
    
    @Test
    public void testUpdateChildPositionNotInContainer() {
        Panel child = new Panel();
        
        assertThrows(IllegalArgumentException.class, () -> {
            container.updateChildPosition(child, 50, 50);
        }, "Should throw exception when updating position of child not in container");
    }
    
    @Test
    public void testGetChildPosition() {
        Panel child = new Panel();
        child.setSize(20, 20);
        
        container.addChildAt(child, 75, 60);
        
        Vector2f pos = container.getChildPosition(child);
        assertNotNull(pos);
        assertEquals(75f, pos.x, 0.01f);
        assertEquals(60f, pos.y, 0.01f);
        
        // Verify it returns a copy (modifying returned vector shouldn't affect stored position)
        pos.x = 999f;
        Vector2f pos2 = container.getChildPosition(child);
        assertEquals(75f, pos2.x, 0.01f, "Stored position should not be affected by modifying returned vector");
    }
    
    @Test
    public void testGetChildPositionNotInContainer() {
        Panel child = new Panel();
        
        Vector2f pos = container.getChildPosition(child);
        assertNull(pos, "Should return null for child not in container");
    }
    
    @Test
    public void testChildSizeChangeRepositions() {
        Panel child = new Panel();
        child.setSize(20, 20);
        
        container.addChildAt(child, 50, 50);
        container.forceLayout();
        
        // Initially centered at (50, 50) with size 20x20
        assertEquals(40f, child.getPosition().x, 0.01f);
        assertEquals(40f, child.getPosition().y, 0.01f);
        
        // Change child size
        child.setSize(40, 40);
        container.forceLayout();
        
        // Should be re-centered at (50, 50) with new size 40x40
        assertEquals(30f, child.getPosition().x, 0.01f, "Child should be re-centered after size change");
        assertEquals(30f, child.getPosition().y, 0.01f, "Child should be re-centered after size change");
    }
    
    @Test
    public void testRemoveChild() {
        Panel child1 = new Panel();
        Panel child2 = new Panel();
        
        container.maxChildren(3);
        container.addChildAt(child1, 10, 10);
        container.addChildAt(child2, 20, 20);
        
        assertEquals(2, container.getChildCount());
        assertEquals(1, container.getRemainingSlots());
        assertNotNull(container.getChildPosition(child1));
        assertNotNull(container.getChildPosition(child2));
        
        container.removeChild(child1);
        
        assertEquals(1, container.getChildCount());
        assertEquals(2, container.getRemainingSlots());
        assertNull(container.getChildPosition(child1), "Position should be removed for removed child");
        assertNotNull(container.getChildPosition(child2));
    }
    
    @Test
    public void testClearChildren() {
        Panel child1 = new Panel();
        Panel child2 = new Panel();
        Panel child3 = new Panel();
        
        container.maxChildren(3);
        container.addChildAt(child1, 10, 10);
        container.addChildAt(child2, 20, 20);
        container.addChildAt(child3, 30, 30);
        
        assertTrue(container.isFull());
        
        container.clearChildren();
        
        assertEquals(0, container.getChildCount());
        assertFalse(container.isFull());
        assertEquals(3, container.getRemainingSlots());
        assertNull(container.getChildPosition(child1));
        assertNull(container.getChildPosition(child2));
        assertNull(container.getChildPosition(child3));
    }
    
    @Test
    public void testAddNullChild() {
        assertThrows(IllegalArgumentException.class, () -> {
            container.addChildAt(null, 10, 10);
        }, "Should throw exception when adding null child");
    }
    
    @Test
    public void testContainerSizeDoesNotChangeWithChildren() {
        container.setSize(200, 100);
        
        Panel child1 = new Panel();
        child1.setSize(50, 50);
        container.addChildAt(child1, 100, 50);
        
        Panel child2 = new Panel();
        child2.setSize(60, 60);
        container.addChildAt(child2, 150, 50);
        
        container.forceLayout();
        
        // Container size should remain fixed (auto-size disabled by default)
        assertEquals(200f, container.getSize().x, 0.01f);
        assertEquals(100f, container.getSize().y, 0.01f);
    }
    
    @Test
    public void testChildrenWithDifferentSizes() {
        Panel smallChild = new Panel();
        smallChild.setSize(10, 10);
        
        Panel mediumChild = new Panel();
        mediumChild.setSize(30, 30);
        
        Panel largeChild = new Panel();
        largeChild.setSize(50, 50);
        
        // All centered at the same Y but different X
        container.addChildAt(smallChild, 40, 50);
        container.addChildAt(mediumChild, 100, 50);
        container.addChildAt(largeChild, 160, 50);
        
        container.forceLayout();
        
        // Verify centering for each size
        assertEquals(35f, smallChild.getPosition().x, 0.01f);  // 40 - 10/2
        assertEquals(45f, smallChild.getPosition().y, 0.01f);  // 50 - 10/2
        
        assertEquals(85f, mediumChild.getPosition().x, 0.01f); // 100 - 30/2
        assertEquals(35f, mediumChild.getPosition().y, 0.01f); // 50 - 30/2
        
        assertEquals(135f, largeChild.getPosition().x, 0.01f); // 160 - 50/2
        assertEquals(25f, largeChild.getPosition().y, 0.01f);  // 50 - 50/2
    }
    
    @Test
    public void testInvisibleChildrenNotPositioned() {
        Panel visibleChild = new Panel();
        visibleChild.setSize(20, 20);
        
        Panel invisibleChild = new Panel();
        invisibleChild.setSize(20, 20);
        invisibleChild.setVisible(false);
        
        container.addChildAt(visibleChild, 50, 50);
        container.addChildAt(invisibleChild, 100, 50);
        
        container.forceLayout();
        
        // Visible child should be positioned
        assertEquals(40f, visibleChild.getPosition().x, 0.01f);
        
        // Invisible child position is still set (but won't be rendered)
        // The container still manages its position data
        assertEquals(2, container.getChildCount());
    }
    
    @Test
    public void testFluentAPI() {
        ManualContainer result = container
            .maxChildren(5)
            .setSize(300, 200)
            .backgroundColor(0xFF0000FF);
        
        assertSame(container, result, "Fluent methods should return this");
        assertEquals(5, container.getMaxChildren());
        assertEquals(300f, container.getSize().x, 0.01f);
        assertEquals(200f, container.getSize().y, 0.01f);
    }
}
