package io.github.currenj.gelatinui.gui.particles;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Configuration for emitting particles.
 * Defines the properties of particles that will be spawned.
 */
public class ParticleEmitter {
    private static final Random RANDOM = new Random();
    
    // Position range
    private float positionX = 0;
    private float positionY = 0;
    private float positionRangeX = 0;
    private float positionRangeY = 0;
    
    // Velocity range
    private float velocityX = 0;
    private float velocityY = 0;
    private float velocityRangeX = 0;
    private float velocityRangeY = 0;
    
    // Angular velocity range (degrees per second)
    private float angularVelocityX = 0;
    private float angularVelocityY = 0;
    private float angularVelocityZ = 0;
    private float angularVelocityRangeX = 0;
    private float angularVelocityRangeY = 0;
    private float angularVelocityRangeZ = 0;
    
    // Scale range
    private float startScale = 1.0f;
    private float endScale = 1.0f;
    private float scaleRange = 0;
    
    // Alpha range
    private float startAlpha = 1.0f;
    private float endAlpha = 0.0f;
    
    // Color
    private int startColor = 0xFFFFFFFF;
    private int endColor = 0xFFFFFFFF;
    
    // Lifetime range
    private float lifetime = 1.0f;
    private float lifetimeRange = 0;
    
    // Gravity
    private float gravityX = 0;
    private float gravityY = 0;
    
    // Rendering
    private ParticleRenderMode renderMode = ParticleRenderMode.SPRITE;
    private ResourceLocation spriteTexture = null;
    private ItemStack itemStack = ItemStack.EMPTY;
    
    // Custom initialization
    private Consumer<Particle> customInitializer = null;
    
    /**
     * Create and configure a particle based on this emitter's settings.
     */
    public void configureParticle(Particle particle) {
        // Position with randomization
        float px = positionX + (RANDOM.nextFloat() * 2 - 1) * positionRangeX;
        float py = positionY + (RANDOM.nextFloat() * 2 - 1) * positionRangeY;
        particle.setPosition(px, py);
        
        // Velocity with randomization
        float vx = velocityX + (RANDOM.nextFloat() * 2 - 1) * velocityRangeX;
        float vy = velocityY + (RANDOM.nextFloat() * 2 - 1) * velocityRangeY;
        particle.setVelocity(vx, vy);
        
        // Angular velocity with randomization
        float avx = angularVelocityX + (RANDOM.nextFloat() * 2 - 1) * angularVelocityRangeX;
        float avy = angularVelocityY + (RANDOM.nextFloat() * 2 - 1) * angularVelocityRangeY;
        float avz = angularVelocityZ + (RANDOM.nextFloat() * 2 - 1) * angularVelocityRangeZ;
        particle.setAngularVelocity(avx, avy, avz);
        
        // Scale with randomization
        float scaleVariation = (RANDOM.nextFloat() * 2 - 1) * scaleRange;
        particle.setScaleOverLifetime(startScale + scaleVariation, endScale + scaleVariation);
        
        // Alpha
        particle.setAlphaOverLifetime(startAlpha, endAlpha);
        
        // Color
        particle.setColorOverLifetime(startColor, endColor);
        
        // Lifetime with randomization
        float lt = lifetime + (RANDOM.nextFloat() * 2 - 1) * lifetimeRange;
        particle.setLifetime(Math.max(0.1f, lt));
        
        // Gravity
        particle.setGravity(gravityX, gravityY);
        
        // Rendering
        particle.setRenderMode(renderMode);
        if (renderMode == ParticleRenderMode.SPRITE && spriteTexture != null) {
            particle.setSpriteTexture(spriteTexture);
        } else if (renderMode == ParticleRenderMode.ITEMSTACK && !itemStack.isEmpty()) {
            particle.setItemStack(itemStack.copy());
        }
        
        // Custom initialization
        if (customInitializer != null) {
            customInitializer.accept(particle);
        }
    }
    
    // Builder-style setters
    public ParticleEmitter setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        return this;
    }
    
    public ParticleEmitter setPositionRange(float rangeX, float rangeY) {
        this.positionRangeX = rangeX;
        this.positionRangeY = rangeY;
        return this;
    }
    
    public ParticleEmitter setVelocity(float x, float y) {
        this.velocityX = x;
        this.velocityY = y;
        return this;
    }
    
    public ParticleEmitter setVelocityRange(float rangeX, float rangeY) {
        this.velocityRangeX = rangeX;
        this.velocityRangeY = rangeY;
        return this;
    }
    
    public ParticleEmitter setAngularVelocity(float x, float y, float z) {
        this.angularVelocityX = x;
        this.angularVelocityY = y;
        this.angularVelocityZ = z;
        return this;
    }
    
    public ParticleEmitter setAngularVelocityRange(float rangeX, float rangeY, float rangeZ) {
        this.angularVelocityRangeX = rangeX;
        this.angularVelocityRangeY = rangeY;
        this.angularVelocityRangeZ = rangeZ;
        return this;
    }
    
    public ParticleEmitter setScale(float start, float end) {
        this.startScale = start;
        this.endScale = end;
        return this;
    }
    
    public ParticleEmitter setScaleRange(float range) {
        this.scaleRange = range;
        return this;
    }
    
    public ParticleEmitter setAlpha(float start, float end) {
        this.startAlpha = start;
        this.endAlpha = end;
        return this;
    }
    
    public ParticleEmitter setColor(int start, int end) {
        this.startColor = start;
        this.endColor = end;
        return this;
    }
    
    public ParticleEmitter setColor(int color) {
        this.startColor = color;
        this.endColor = color;
        return this;
    }
    
    public ParticleEmitter setLifetime(float lifetime) {
        this.lifetime = lifetime;
        return this;
    }
    
    public ParticleEmitter setLifetimeRange(float range) {
        this.lifetimeRange = range;
        return this;
    }
    
    public ParticleEmitter setGravity(float x, float y) {
        this.gravityX = x;
        this.gravityY = y;
        return this;
    }
    
    public ParticleEmitter setRenderMode(ParticleRenderMode mode) {
        this.renderMode = mode;
        return this;
    }
    
    public ParticleEmitter setSpriteTexture(ResourceLocation texture) {
        this.spriteTexture = texture;
        this.renderMode = ParticleRenderMode.SPRITE;
        return this;
    }
    
    public ParticleEmitter setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.renderMode = ParticleRenderMode.ITEMSTACK;
        return this;
    }
    
    public ParticleEmitter setCustomInitializer(Consumer<Particle> initializer) {
        this.customInitializer = initializer;
        return this;
    }
    
    /**
     * Create a burst emitter that emits particles in all directions.
     */
    public static ParticleEmitter burst(float x, float y, float speed, float speedRange) {
        return new ParticleEmitter()
                .setPosition(x, y)
                .setVelocity(0, 0)
                .setVelocityRange(speed + speedRange, speed + speedRange);
    }
    
    /**
     * Create a gravity-affected burst emitter.
     */
    public static ParticleEmitter gravityBurst(float x, float y, float upSpeed, float sideSpeed, float gravity) {
        return new ParticleEmitter()
                .setPosition(x, y)
                .setVelocity(0, -upSpeed)
                .setVelocityRange(sideSpeed, upSpeed * 0.5f)
                .setGravity(0, gravity);
    }
}
