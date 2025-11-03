package io.github.currenj.gelatinui.gui;

import io.github.currenj.gelatinui.gui.components.Panel;
import io.github.currenj.gelatinui.gui.components.SpriteData;
import io.github.currenj.gelatinui.gui.components.SpriteRenderMode;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CenterBackgroundSpriteTest {

    @Test
    public void testCenterBackgroundSpriteFlag() {
        Panel panel = new Panel();
        
        // Default should be false
        assertFalse(panel.isCenterBackgroundSprite());
        
        // Set to true
        panel.centerBackgroundSprite(true);
        assertTrue(panel.isCenterBackgroundSprite());
        
        // Set back to false
        panel.centerBackgroundSprite(false);
        assertFalse(panel.isCenterBackgroundSprite());
    }

    @Test
    public void testCenterBackgroundSpriteFluentAPI() {
        Panel panel = new Panel();
        
        // Test fluent API returns self
        Panel result = panel.centerBackgroundSprite(true);
        assertSame(panel, result);
        assertTrue(panel.isCenterBackgroundSprite());
    }

    @Test
    public void testCenterBackgroundSpriteWithSprite() {
        Panel panel = new Panel();
        
        // Create a sprite with specific dimensions
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("test", "sprite");
        SpriteData sprite = new SpriteData(texture)
            .uv(0, 0, 64, 64)  // regionW=64, regionH=64
            .actualSize(50, 50)  // actualW=50, actualH=50
            .renderMode(SpriteRenderMode.STRETCH);
        
        panel.backgroundSprite(sprite);
        panel.centerBackgroundSprite(true);
        
        assertTrue(panel.isCenterBackgroundSprite());
        assertNotNull(panel.getBackgroundSprite());
        assertEquals(50, panel.getBackgroundSprite().actualW());
        assertEquals(50, panel.getBackgroundSprite().actualH());
    }

    @Test
    public void testCenterBackgroundSpriteUsesRegionSizeWhenActualSizeNotSet() {
        Panel panel = new Panel();
        
        // Create a sprite without explicit actualSize
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("test", "sprite");
        SpriteData sprite = new SpriteData(texture)
            .uv(0, 0, 64, 64)  // regionW=64, regionH=64
            .renderMode(SpriteRenderMode.STRETCH);
        
        panel.backgroundSprite(sprite);
        panel.centerBackgroundSprite(true);
        
        // When actualW/actualH are 0, should fall back to regionW/regionH
        assertEquals(0, panel.getBackgroundSprite().actualW());
        assertEquals(0, panel.getBackgroundSprite().actualH());
        assertEquals(64, panel.getBackgroundSprite().regionW());
        assertEquals(64, panel.getBackgroundSprite().regionH());
    }
}
