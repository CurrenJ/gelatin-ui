package io.github.currenj.gelatinui.gui.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Quaternionf;

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
        
        GuiGraphics graphics = mcContext.getGraphics();
        PoseStack poseStack = graphics.pose();
        
        // Render each particle
        for (Particle particle : activeParticles) {
            renderParticle(graphics, particle);
        }
    }
    
    /**
     * Render a single particle.
     */
    private void renderParticle(GuiGraphics graphics, Particle particle) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        
        // Translate to particle position
        poseStack.translate(particle.getPosition().x, particle.getPosition().y, 0);
        
        // Apply rotation
        if (particle.getRenderMode() == ParticleRenderMode.ITEMSTACK) {
            // 3D rotation for items
            applyItemRotation(poseStack, particle);
            
            // Scale
            float scale = particle.getScale();
            poseStack.scale(scale, scale, scale);
            
            // Render itemstack
            if (!particle.getItemStack().isEmpty()) {
                // Apply alpha through color multiplier if possible
                // Note: Full alpha support would require custom rendering, so we skip it for items
                graphics.renderItem(particle.getItemStack(), 0, 0);
            }
        } else {
            // 2D rotation for sprites (Z-axis only)
            float rotZ = particle.getRotation().z;
            if (Math.abs(rotZ) > 0.001f) {
                poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(rotZ)));
            }
            
            // Scale
            float scale = particle.getScale();
            poseStack.scale(scale, scale, 1.0f);
            
            // Render sprite
            if (particle.getSpriteTexture() != null) {
                // Apply alpha and color
                float alpha = particle.getAlpha();
                int color = particle.getColor();
                
                // Extract RGBA components
                int a = (int)((color >> 24 & 0xFF) * alpha);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                int colorWithAlpha = (a << 24) | (r << 16) | (g << 8) | b;
                
                // Enable blending for alpha
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                
                // Render sprite (16x16 by default, centered)
                graphics.setColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
                graphics.blit(particle.getSpriteTexture(), -8, -8, 0, 0, 16, 16, 16, 16);
                graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                
                RenderSystem.disableBlend();
            }
        }
        
        poseStack.popPose();
    }
    
    /**
     * Apply 3D rotation to itemstack rendering.
     */
    private void applyItemRotation(PoseStack poseStack, Particle particle) {
        float rotX = particle.getRotation().x;
        float rotY = particle.getRotation().y;
        float rotZ = particle.getRotation().z;
        
        // Apply rotations in order: Y, X, Z (similar to ItemRenderer effects)
        if (Math.abs(rotY) > 0.001f) {
            poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(rotY)));
        }
        if (Math.abs(rotX) > 0.001f) {
            poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(rotX)));
        }
        if (Math.abs(rotZ) > 0.001f) {
            poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(rotZ)));
        }
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
