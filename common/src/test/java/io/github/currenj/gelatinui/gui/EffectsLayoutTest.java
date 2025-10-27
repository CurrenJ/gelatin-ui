package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.components.VBox;
import io.github.currenj.gelatinui.gui.components.Panel;
import io.github.currenj.gelatinui.gui.effects.BreatheEffect;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that effects can properly affect layout when enabled.
 */
public class EffectsLayoutTest {

    @Test
    public void testEffectsAffectLayoutDisabledByDefault() {
        Panel panel = new Panel();
        panel.setSize(new Vector2f(100, 100));

        assertFalse(panel.getEffectsAffectLayout(),
            "Effects should not affect layout by default");
    }

    @Test
    public void testEffectsDoNotAffectSizeWhenDisabled() {
        Panel panel = new Panel();
        panel.setSize(new Vector2f(100, 100));

        // Add a breathe effect that scales the element
        panel.addBreatheEffect();

        // Update to let effect kick in
        panel.update(0.5f);

        // Size should remain unchanged since effectsAffectLayout is false
        Vector2f size = panel.getSize();
        assertEquals(100f, size.x, 0.001f, "Width should be unchanged");
        assertEquals(100f, size.y, 0.001f, "Height should be unchanged");
    }

    @Test
    public void testEffectsAffectSizeWhenEnabled() {
        Panel panel = new Panel();
        panel.setSize(new Vector2f(100, 100));

        // Enable effects affecting layout
        panel.setEffectsAffectLayout(true);
        assertTrue(panel.getEffectsAffectLayout(), "Effects should affect layout when enabled");

        // Add a breathe effect
        panel.addBreatheEffect();

        // Update to let effect animate
        panel.update(0.5f);

        // Size should now be affected by the breathe effect's scale multiplier
        Vector2f size = panel.getSize();
        // The breathe effect should have changed the scale multiplier from 1.0
        // So the reported size should be different from base size
        assertNotEquals(100f, size.x, 0.001f, "Width should be affected by effect");
        assertNotEquals(100f, size.y, 0.001f, "Height should be affected by effect");
    }

    @Test
    public void testVBoxLayoutWithEffects() {
        VBox vbox = new VBox();
        vbox.spacing(10);

        Panel panel1 = new Panel();
        panel1.setSize(new Vector2f(50, 50));

        Panel panel2 = new Panel();
        panel2.setSize(new Vector2f(50, 50));
        panel2.setEffectsAffectLayout(true);
        panel2.addBreatheEffect();

        vbox.addChild(panel1);
        vbox.addChild(panel2);

        // Force layout calculation
        vbox.update(0.016f);

        // The VBox should account for panel2's effect-modified size
        // This test verifies that layout system sees the modified size
        Vector2f panel2Size = panel2.getSize();

        // Verify that panel2's size reflects the effect
        assertNotEquals(50f, panel2Size.x, 0.5f,
            "Panel2 width should be affected by breathe effect");
    }

    @Test
    public void testEnableDisableEffectsLayout() {
        Panel panel = new Panel();
        panel.setSize(new Vector2f(100, 100));

        // Start disabled
        assertFalse(panel.getEffectsAffectLayout());

        // Enable
        panel.enableEffectsLayout();
        assertTrue(panel.getEffectsAffectLayout());

        // Disable
        panel.disableEffectsLayout();
        assertFalse(panel.getEffectsAffectLayout());
    }

    @Test
    public void testEffectsPositionOffset() {
        Panel panel = new Panel();
        panel.setPosition(new Vector2f(100, 100));
        panel.setSize(new Vector2f(50, 50));

        // Without effects affecting layout
        Vector2f pos1 = panel.getPosition();
        assertEquals(100f, pos1.x, 0.001f);
        assertEquals(100f, pos1.y, 0.001f);

        // Enable effects and add a wander effect (which adds position offsets)
        panel.setEffectsAffectLayout(true);
        panel.addWanderEffect();

        // Update to let effect kick in
        panel.update(0.5f);

        // Position should now include effect offset
        Vector2f pos2 = panel.getPosition();
        // The wander effect should have added some offset
        // We can't predict exact values, but position should be different
        assertTrue(Math.abs(pos2.x - 100f) > 0.01f || Math.abs(pos2.y - 100f) > 0.01f,
            "Position should be affected by wander effect offset");
    }
}

