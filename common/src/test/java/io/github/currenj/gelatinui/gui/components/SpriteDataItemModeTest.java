package io.github.currenj.gelatinui.gui.components;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ITEM render mode of SpriteData.
 */
public class SpriteDataItemModeTest {

    @Test
    public void testItemModeCreation() {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath("minecraft", "diamond");
        SpriteData sprite = SpriteData.item(itemId);
        
        assertNotNull(sprite);
        assertEquals(SpriteRenderMode.ITEM, sprite.renderMode());
        assertEquals(itemId, sprite.itemId());
        assertEquals(0, sprite.itemRotationY());
        assertEquals(0, sprite.itemRotationZ());
    }

    @Test
    public void testItemModeWithRotation() {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath("minecraft", "gold_ingot");
        SpriteData sprite = SpriteData.item(itemId, 45.0f, 90.0f);
        
        assertNotNull(sprite);
        assertEquals(SpriteRenderMode.ITEM, sprite.renderMode());
        assertEquals(itemId, sprite.itemId());
        assertEquals(45.0f, sprite.itemRotationY());
        assertEquals(90.0f, sprite.itemRotationZ());
    }

    @Test
    public void testItemModeFluentApi() {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath("minecraft", "emerald");
        SpriteData sprite = SpriteData.item(itemId)
            .itemRotation(30.0f, 60.0f)
            .uv(10, 20, 32, 32);
        
        assertNotNull(sprite);
        assertEquals(SpriteRenderMode.ITEM, sprite.renderMode());
        assertEquals(itemId, sprite.itemId());
        assertEquals(30.0f, sprite.itemRotationY());
        assertEquals(60.0f, sprite.itemRotationZ());
        assertEquals(10, sprite.u());
        assertEquals(20, sprite.v());
        assertEquals(32, sprite.regionW());
        assertEquals(32, sprite.regionH());
    }

    @Test
    public void testItemModeValidation() {
        // Creating an ITEM mode sprite without itemId should throw
        assertThrows(IllegalArgumentException.class, () -> {
            new SpriteData(
                null,  // texture
                0, 0, 0, 0, 0, 0, 256, 256,
                SpriteRenderMode.ITEM,  // ITEM mode
                0, 0, 0, 0,
                1.0f,
                null,  // itemId is null - should fail validation
                0, 0,
                0  // zOffset
            );
        });
    }

    @Test
    public void testItemRotationSetter() {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath("minecraft", "netherite_ingot");
        SpriteData sprite = SpriteData.item(itemId)
            .itemRotation(180.0f, 270.0f);
        
        assertEquals(180.0f, sprite.itemRotationY());
        assertEquals(270.0f, sprite.itemRotationZ());
    }

    @Test
    public void testZOffsetSetter() {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath("minecraft", "diamond");
        SpriteData sprite = SpriteData.item(itemId)
            .zOffset(100.0f);
        
        assertEquals(100.0f, sprite.zOffset());
    }

    @Test
    public void testZOffsetWithRegularSprite() {
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites.png");
        SpriteData sprite = new SpriteData(texture)
            .uv(0, 0, 32, 32)
            .zOffset(50.0f);
        
        assertEquals(50.0f, sprite.zOffset());
    }
}
