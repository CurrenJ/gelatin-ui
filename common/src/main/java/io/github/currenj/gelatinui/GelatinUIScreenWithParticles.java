package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.GelatinMenu;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.particles.ParticleSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Extension of GelatinUIScreen that provides a built-in particle system overlay.
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

        if (particleSystem == null) {
            particleSystem = new ParticleSystem(this.width, this.height);
            particleSystem.setMaxParticles(1000);
            particleSystem.setPosition(new org.joml.Vector2f(0, 0));
        } else {
            particleSystem.getSize().set(this.width, this.height);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (particleSystem != null) {
            long nowNanos = System.nanoTime();
            float deltaSeconds = Math.min(0.1f, Math.max(0f, (nowNanos - lastParticleUpdateTime) / 1_000_000_000f));
            lastParticleUpdateTime = nowNanos;

            particleSystem.update(deltaSeconds);
            particleRenderContext = new MinecraftRenderContext(graphics, this.font);

            java.awt.geom.Rectangle2D viewport = new java.awt.geom.Rectangle2D.Float(0, 0, this.width, this.height);
            particleSystem.render(particleRenderContext, viewport);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (particleSystem != null) {
            particleSystem.getSize().set(width, height);
        }
    }

    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    public void clearParticles() {
        if (particleSystem != null) {
            particleSystem.clear();
        }
    }

    public void setMaxParticles(int maxParticles) {
        if (particleSystem != null) {
            particleSystem.setMaxParticles(maxParticles);
        }
    }

    public int getActiveParticleCount() {
        return particleSystem != null ? particleSystem.getActiveParticleCount() : 0;
    }
}
