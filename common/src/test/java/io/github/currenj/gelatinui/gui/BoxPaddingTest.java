package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.components.HBox;
import io.github.currenj.gelatinui.gui.components.Label;
import io.github.currenj.gelatinui.gui.components.Panel;
import io.github.currenj.gelatinui.gui.components.VBox;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoxPaddingTest {

    @Test
    public void testVBoxUniformPadding() {
        VBox vbox = new VBox();
        vbox.padding(10);
        
        // Trigger layout with empty container
        vbox.forceLayout();
        
        // Empty VBox should have size equal to padding sum
        assertEquals(20f, vbox.getSize().x, 0.01f);
        assertEquals(20f, vbox.getSize().y, 0.01f);
        
        // Check getters
        assertEquals(10f, vbox.getPaddingTop(), 0.01f);
        assertEquals(10f, vbox.getPaddingBottom(), 0.01f);
        assertEquals(10f, vbox.getPaddingLeft(), 0.01f);
        assertEquals(10f, vbox.getPaddingRight(), 0.01f);
    }

    @Test
    public void testVBoxSplitPadding() {
        VBox vbox = new VBox();
        vbox.padding(5, 10, 15, 20); // top, bottom, left, right
        
        vbox.forceLayout();
        
        // Empty VBox should have size equal to padding sum
        assertEquals(35f, vbox.getSize().x, 0.01f); // left (15) + right (20)
        assertEquals(15f, vbox.getSize().y, 0.01f); // top (5) + bottom (10)
        
        assertEquals(5f, vbox.getPaddingTop(), 0.01f);
        assertEquals(10f, vbox.getPaddingBottom(), 0.01f);
        assertEquals(15f, vbox.getPaddingLeft(), 0.01f);
        assertEquals(20f, vbox.getPaddingRight(), 0.01f);
    }

    @Test
    public void testVBoxIndividualPaddingSetters() {
        VBox vbox = new VBox();
        vbox.paddingTop(5)
            .paddingBottom(10)
            .paddingLeft(15)
            .paddingRight(20);
        
        vbox.forceLayout();
        
        assertEquals(35f, vbox.getSize().x, 0.01f);
        assertEquals(15f, vbox.getSize().y, 0.01f);
        
        assertEquals(5f, vbox.getPaddingTop(), 0.01f);
        assertEquals(10f, vbox.getPaddingBottom(), 0.01f);
        assertEquals(15f, vbox.getPaddingLeft(), 0.01f);
        assertEquals(20f, vbox.getPaddingRight(), 0.01f);
    }

    @Test
    public void testVBoxWithChildrenAndSplitPadding() {
        VBox vbox = new VBox();
        vbox.padding(10, 20, 5, 15); // top, bottom, left, right
        
        Panel child1 = new Panel();
        child1.setSize(100, 50);
        
        Panel child2 = new Panel();
        child2.setSize(80, 30);
        
        vbox.add(child1);
        vbox.add(child2);
        vbox.forceLayout();
        
        // Expected width: max child width (100) + left padding (5) + right padding (15) = 120
        assertEquals(120f, vbox.getSize().x, 0.01f);
        
        // Expected height: top padding (10) + child1 (50) + child2 (30) + bottom padding (20) = 110
        assertEquals(110f, vbox.getSize().y, 0.01f);
        
        // Check child positions
        Vector2f child1Pos = child1.getPosition();
        assertEquals(5f, child1Pos.x, 0.01f); // left padding
        assertEquals(10f, child1Pos.y, 0.01f); // top padding
        
        Vector2f child2Pos = child2.getPosition();
        assertEquals(5f, child2Pos.x, 0.01f); // left padding (LEFT alignment)
        assertEquals(60f, child2Pos.y, 0.01f); // top padding + child1 height
    }

    @Test
    public void testHBoxUniformPadding() {
        HBox hbox = new HBox();
        hbox.padding(10);
        
        hbox.forceLayout();
        
        // Empty HBox should have size equal to padding sum
        assertEquals(20f, hbox.getSize().x, 0.01f);
        assertEquals(20f, hbox.getSize().y, 0.01f);
        
        assertEquals(10f, hbox.getPaddingTop(), 0.01f);
        assertEquals(10f, hbox.getPaddingBottom(), 0.01f);
        assertEquals(10f, hbox.getPaddingLeft(), 0.01f);
        assertEquals(10f, hbox.getPaddingRight(), 0.01f);
    }

    @Test
    public void testHBoxSplitPadding() {
        HBox hbox = new HBox();
        hbox.padding(5, 10, 15, 20); // top, bottom, left, right
        
        hbox.forceLayout();
        
        assertEquals(35f, hbox.getSize().x, 0.01f); // left (15) + right (20)
        assertEquals(15f, hbox.getSize().y, 0.01f); // top (5) + bottom (10)
        
        assertEquals(5f, hbox.getPaddingTop(), 0.01f);
        assertEquals(10f, hbox.getPaddingBottom(), 0.01f);
        assertEquals(15f, hbox.getPaddingLeft(), 0.01f);
        assertEquals(20f, hbox.getPaddingRight(), 0.01f);
    }

    @Test
    public void testHBoxIndividualPaddingSetters() {
        HBox hbox = new HBox();
        hbox.paddingTop(5)
            .paddingBottom(10)
            .paddingLeft(15)
            .paddingRight(20);
        
        hbox.forceLayout();
        
        assertEquals(35f, hbox.getSize().x, 0.01f);
        assertEquals(15f, hbox.getSize().y, 0.01f);
    }

    @Test
    public void testHBoxWithChildrenAndSplitPadding() {
        HBox hbox = new HBox();
        hbox.padding(10, 20, 5, 15); // top, bottom, left, right
        
        Panel child1 = new Panel();
        child1.setSize(50, 100);
        
        Panel child2 = new Panel();
        child2.setSize(30, 80);
        
        hbox.add(child1);
        hbox.add(child2);
        hbox.forceLayout();
        
        // Expected width: left padding (5) + child1 (50) + child2 (30) + right padding (15) = 100
        assertEquals(100f, hbox.getSize().x, 0.01f);
        
        // Expected height: max child height (100) + top padding (10) + bottom padding (20) = 130
        assertEquals(130f, hbox.getSize().y, 0.01f);
        
        // Check child positions
        Vector2f child1Pos = child1.getPosition();
        assertEquals(5f, child1Pos.x, 0.01f); // left padding
        assertEquals(10f, child1Pos.y, 0.01f); // top padding (TOP alignment)
        
        Vector2f child2Pos = child2.getPosition();
        assertEquals(55f, child2Pos.x, 0.01f); // left padding + child1 width
        assertEquals(10f, child2Pos.y, 0.01f); // top padding (TOP alignment)
    }

    @Test
    public void testVBoxCenterAlignmentWithAsymmetricPadding() {
        VBox vbox = new VBox();
        vbox.padding(10, 10, 5, 25) // top, bottom, left, right - asymmetric horizontal
            .alignment(VBox.Alignment.CENTER);
        
        Panel child = new Panel();
        child.setSize(20, 30);
        
        vbox.add(child);
        vbox.forceLayout();
        
        // Total width: left (5) + right (25) + child (20) = 50
        assertEquals(50f, vbox.getSize().x, 0.01f);
        
        // Child should be centered in content area
        // Content area width: 50 - 5 - 25 = 20
        // Child x position: left padding (5) + (content width (20) - child width (20)) / 2 = 5
        Vector2f childPos = child.getPosition();
        assertEquals(15f, childPos.x, 0.01f);
        assertEquals(10f, childPos.y, 0.01f); // top padding
    }

    @Test
    public void testHBoxCenterAlignmentWithAsymmetricPadding() {
        HBox hbox = new HBox();
        hbox.padding(5, 25, 10, 10) // top, bottom, left, right - asymmetric vertical
            .alignment(HBox.Alignment.CENTER);
        
        Panel child = new Panel();
        child.setSize(30, 20);
        
        hbox.add(child);
        hbox.forceLayout();
        
        // Total height: top (5) + bottom (25) + child (20) = 50
        assertEquals(50f, hbox.getSize().y, 0.01f);
        
        // Child should be centered in content area
        // Content area height: 50 - 5 - 25 = 20
        // Child y position: top padding (5) + (content height (20) - child height (20)) / 2 = 5
        Vector2f childPos = child.getPosition();
        assertEquals(10f, childPos.x, 0.01f); // left padding
        assertEquals(15f, childPos.y, 0.01f);
    }

    @Test
    public void testBackwardCompatibility() {
        // Test that old code using padding(float) still works
        VBox vbox = new VBox();
        vbox.padding(10);
        
        assertEquals(10f, vbox.getPadding(), 0.01f);
        assertEquals(10f, vbox.getPaddingTop(), 0.01f);
        assertEquals(10f, vbox.getPaddingBottom(), 0.01f);
        assertEquals(10f, vbox.getPaddingLeft(), 0.01f);
        assertEquals(10f, vbox.getPaddingRight(), 0.01f);
        
        HBox hbox = new HBox();
        hbox.padding(15);
        
        assertEquals(15f, hbox.getPadding(), 0.01f);
        assertEquals(15f, hbox.getPaddingTop(), 0.01f);
        assertEquals(15f, hbox.getPaddingBottom(), 0.01f);
        assertEquals(15f, hbox.getPaddingLeft(), 0.01f);
        assertEquals(15f, hbox.getPaddingRight(), 0.01f);
    }
}
