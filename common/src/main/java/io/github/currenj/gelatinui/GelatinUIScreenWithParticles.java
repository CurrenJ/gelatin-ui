package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.GelatinMenu;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.particles.ParticleSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Extension of GelatinUIScreen that provides a built-in particle system overlay.
 * The particle system covers the entire screen and is rendered independently of the root UI tree.
 *
 * <p>Usage example:
 * <pre>{@code
 * public class MyScreen extends GelatinUIScreenWithParticles<MyMenu> {
 *     public MyScreen(MyMenu menu, Inventory inv) {
 *         super(menu, inv, Component.literal("My Screen"));
 *     }
 *
 *     @Override
 *     protected void buildUI() {
 *         // Build your UI as normal
 *         // Access the particle system with getParticleSystem()
 *
 *         ParticleEmitter emitter = new ParticleEmitter()
 *             .setPosition(100, 100)
 *             .setVelocity(0, -50);
 *         getParticleSystem().emit(emitter, 10);
 *     }
 * }
 * }</pre>
 */
public abstract class GelatinUIScreenWithParticles<M extends GelatinMenu> extends GelatinUIScreen<M> {
    private ParticleSystem particleSystem;
    private MinecraftRenderContext particleRenderContext;
    private long lastParticleUpdateTime = 0L;

    protected GelatinUIScreenWithParticles(M menu, Inventory inv, Component title) {
        super(menu, inv, title);
        lastParticleUpdateTime = System.nanoTime();
    }

    @Override
    protected void init() {
        super.init();

        // Initialize particle system to cover the entire screen
        if (particleSystem == null) {
            particleSystem = new ParticleSystem(this.width, this.height);
            particleSystem.setMaxParticles(1000);
            // Position at screen origin
            particleSystem.setPosition(new org.joml.Vector2f(0, 0));
        } else {
            // Resize existing particle system
            particleSystem.getSize().set(this.width, this.height);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render the base UI first
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Update and render particle system overlay on top of everything
        if (particleSystem != null) {
            // Calculate delta time for particle updates
            long nowNanos = System.nanoTime();
            float deltaSeconds = Math.min(0.1f, Math.max(0f, (nowNanos - lastParticleUpdateTime) / 1_000_000_000f));
            lastParticleUpdateTime = nowNanos;

            // Update particle system (this updates particle positions, lifetimes, etc.)
            particleSystem.update(deltaSeconds);

            if (particleRenderContext == null) {
                particleRenderContext = new MinecraftRenderContext(guiGraphics, this.font);
            } else {
                // Update the graphics object for the current frame
                particleRenderContext = new MinecraftRenderContext(guiGraphics, this.font);
            }

            // Render particle system independently
            // Create a viewport that matches the screen size
            java.awt.geom.Rectangle2D viewport = new java.awt.geom.Rectangle2D.Float(0, 0, this.width, this.height);
            particleSystem.render(particleRenderContext, viewport);
        }
    }

    @Override
    public void resize(net.minecraft.client.Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

        // Resize particle system to match new screen dimensions
        if (particleSystem != null) {
            particleSystem.getSize().set(width, height);
        }
    }

    /**
     * Get the particle system for this screen.
     * Use this to emit particles or configure the particle system.
     *
     * @return The particle system instance
     */
    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    /**
     * Clear all active particles from the particle system.
     */
    public void clearParticles() {
        if (particleSystem != null) {
            particleSystem.clear();
        }
    }

    /**
     * Set the maximum number of particles that can be active at once.
     *
     * @param maxParticles Maximum particle count
     */
    public void setMaxParticles(int maxParticles) {
        if (particleSystem != null) {
            particleSystem.setMaxParticles(maxParticles);
        }
    }

    /**
     * Get the number of currently active particles.
     *
     * @return Active particle count
     */
    public int getActiveParticleCount() {
        return particleSystem != null ? particleSystem.getActiveParticleCount() : 0;
    }
}

