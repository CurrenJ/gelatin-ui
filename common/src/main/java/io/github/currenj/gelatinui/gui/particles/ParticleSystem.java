package io.github.currenj.gelatinui.gui.particles;

import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2fStack;
import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/**
 * A UI component that manages and renders a particle system.
 * Particles are updated and rendered each frame.
 */
public class ParticleSystem extends UIElement<ParticleSystem> {
    private final List<Particle> activeParticles = new ArrayList<>();
    private final List<Particle> particlePool = new ArrayList<>();
    private int maxParticles = 1000;

    /**
     * Create a particle system with default max particles.
     */
    public ParticleSystem() {
        // Initialize with a reasonable size
        this.size.set(400, 400);
    }

    /**
     * Create a particle system with custom size.
     */
    public ParticleSystem(float width, float height) {
        this.size.set(width, height);
    }

    /**
     * Set the maximum number of particles that can be active at once.
     */
    public ParticleSystem setMaxParticles(int max) {
        this.maxParticles = max;
        return this;
    }

    /**
     * Emit a single particle using the given emitter configuration.
     */
    public void emit(ParticleEmitter emitter) {
        emit(emitter, 1);
    }

    /**
     * Emit multiple particles using the given emitter configuration.
     */
    public void emit(ParticleEmitter emitter, int count) {
        for (int i = 0; i < count && activeParticles.size() < maxParticles; i++) {
            Particle particle = getOrCreateParticle();
            emitter.configureParticle(particle);
            activeParticles.add(particle);
        }
        markDirty(DirtyFlag.CONTENT);
    }

    /**
     * Emit a burst of particles from a point.
     */
    public void emitBurst(float x, float y, ParticleEmitter emitter, int count) {
        emitter.setPosition(x, y);
        emit(emitter, count);
    }

    /**
     * Clear all active particles.
     */
    public void clear() {
        for (Particle particle : activeParticles) {
            particle.reset();
            particlePool.add(particle);
        }
        activeParticles.clear();
        markDirty(DirtyFlag.CONTENT);
    }

    /**
     * Get the number of active particles.
     */
    public int getActiveParticleCount() {
        return activeParticles.size();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Update all particles
        for (int i = activeParticles.size() - 1; i >= 0; i--) {
            Particle particle = activeParticles.get(i);
            particle.update(deltaTime);

            // Remove dead particles
            if (!particle.isAlive()) {
                activeParticles.remove(i);
                particle.reset();
                particlePool.add(particle);
            }
        }

        // Mark dirty if particles are active (they're always animating)
        if (!activeParticles.isEmpty()) {
            markDirty(DirtyFlag.CONTENT);
        }
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        if (activeParticles.isEmpty()) {
            return;
        }

        // We need MinecraftRenderContext for rendering items and sprites
        if (!(context instanceof MinecraftRenderContext mcContext)) {
            return;
        }

        GuiGraphicsExtractor graphics = mcContext.getGraphics();

        // Render each particle
        for (Particle particle : activeParticles) {
            renderParticle(graphics, particle);
        }
    }

    /**
     * Render a single particle.
     */
    private void renderParticle(GuiGraphicsExtractor graphics, Particle particle) {
        Matrix3x2fStack poseStack = graphics.pose();
        poseStack.pushMatrix();

        // Translate to particle position
        poseStack.translate(particle.getPosition().x, particle.getPosition().y);

        if (particle.getRenderMode() == ParticleRenderMode.ITEMSTACK) {
            float scale = particle.getScale();
            poseStack.scale(scale, scale);

            // Only Z rotation is supported in 2D GUI rendering
            float rotZ = particle.getRotation().z;
            if (Math.abs(rotZ) > 0.001f) {
                // Rotate around the center of the 16x16 item
                float pivotX = 8f;
                float pivotY = 8f;
                poseStack.translate(pivotX, pivotY);
                poseStack.rotate((float) Math.toRadians(rotZ));
                poseStack.translate(-pivotX, -pivotY);
            }

            if (!particle.getItemStack().isEmpty()) {
                graphics.item(particle.getItemStack(), 0, 0);
            }
        } else {
            // 2D rotation for sprites — rotate around the sprite center (origin after translate)
            float rotZ = particle.getRotation().z;
            if (Math.abs(rotZ) > 0.001f) {
                poseStack.rotate((float) Math.toRadians(rotZ));
            }

            float scale = particle.getScale();
            poseStack.scale(scale, scale);

            if (particle.getSpriteTexture() != null) {
                float alpha = particle.getAlpha();
                int color = particle.getColor();

                // Combine particle alpha with color alpha channel
                int a = (int)(((color >> 24) & 0xFF) * alpha);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                int colorWithAlpha = (a << 24) | (r << 16) | (g << 8) | b;

                // Render sprite centered at origin (-8,-8 offset for 16x16)
                graphics.blit(RenderPipelines.GUI_TEXTURED, particle.getSpriteTexture(), -8, -8, 0f, 0f, 16, 16, 16, 16, colorWithAlpha);
            }
        }

        poseStack.popMatrix();
    }

    /**
     * Get a particle from the pool or create a new one.
     */
    private Particle getOrCreateParticle() {
        if (!particlePool.isEmpty()) {
            return particlePool.remove(particlePool.size() - 1);
        }
        return new Particle();
    }

    @Override
    protected ParticleSystem self() {
        return this;
    }

    @Override
    protected String getDefaultDebugName() {
        return "ParticleSystem(active=" + activeParticles.size() + ")";
    }
}
