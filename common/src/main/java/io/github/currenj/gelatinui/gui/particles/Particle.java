package io.github.currenj.gelatinui.gui.particles;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a single particle in the particle system.
 * Each particle has position, velocity, rotation, scale, color, and lifetime properties.
 */
public class Particle {
    // Position and velocity
    private final Vector2f position = new Vector2f();
    private final Vector2f velocity = new Vector2f();
    
    // Rotation (3D for itemstacks, Z for sprites)
    private final Vector3f rotation = new Vector3f();
    private final Vector3f angularVelocity = new Vector3f();
    
    // Visual properties
    private float scale = 1.0f;
    private float alpha = 1.0f;
    private int color = 0xFFFFFFFF;
    
    // Lifetime management
    private float lifetime = 1.0f; // seconds
    private float age = 0.0f; // seconds
    private boolean alive = true;
    
    // Physics
    private final Vector2f gravity = new Vector2f(0, 0);
    
    // Rendering mode
    private ParticleRenderMode renderMode = ParticleRenderMode.SPRITE;
    private Identifier spriteTexture = null;
    private ItemStack itemStack = ItemStack.EMPTY;
    
    // Property interpolation
    private float startScale = 1.0f;
    private float endScale = 1.0f;
    private float startAlpha = 1.0f;
    private float endAlpha = 0.0f;
    private int startColor = 0xFFFFFFFF;
    private int endColor = 0xFFFFFFFF;
    
    /**
     * Update the particle for one frame.
     * @param deltaTime time elapsed since last frame in seconds
     */
    public void update(float deltaTime) {
        if (!alive) return;
        
        age += deltaTime;
        
        // Check if particle has exceeded lifetime
        if (age >= lifetime) {
            alive = false;
            return;
        }
        
        // Apply velocity
        position.x += velocity.x * deltaTime;
        position.y += velocity.y * deltaTime;
        
        // Apply gravity
        velocity.x += gravity.x * deltaTime;
        velocity.y += gravity.y * deltaTime;
        
        // Apply angular velocity
        rotation.x += angularVelocity.x * deltaTime;
        rotation.y += angularVelocity.y * deltaTime;
        rotation.z += angularVelocity.z * deltaTime;
        
        // Interpolate properties based on lifetime progress
        float t = age / lifetime;
        scale = startScale + (endScale - startScale) * t;
        alpha = startAlpha + (endAlpha - startAlpha) * t;
        
        // Interpolate color (ARGB)
        if (startColor != endColor) {
            int startA = (startColor >> 24) & 0xFF;
            int startR = (startColor >> 16) & 0xFF;
            int startG = (startColor >> 8) & 0xFF;
            int startB = startColor & 0xFF;
            
            int endA = (endColor >> 24) & 0xFF;
            int endR = (endColor >> 16) & 0xFF;
            int endG = (endColor >> 8) & 0xFF;
            int endB = endColor & 0xFF;
            
            int a = (int)(startA + (endA - startA) * t);
            int r = (int)(startR + (endR - startR) * t);
            int g = (int)(startG + (endG - startG) * t);
            int b = (int)(startB + (endB - startB) * t);
            
            color = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
    
    /**
     * Check if the particle is still alive.
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Kill the particle immediately.
     */
    public void kill() {
        alive = false;
    }
    
    // Getters
    public Vector2f getPosition() { return position; }
    public Vector2f getVelocity() { return velocity; }
    public Vector3f getRotation() { return rotation; }
    public Vector3f getAngularVelocity() { return angularVelocity; }
    public float getScale() { return scale; }
    public float getAlpha() { return alpha; }
    public int getColor() { return color; }
    public float getLifetime() { return lifetime; }
    public float getAge() { return age; }
    public Vector2f getGravity() { return gravity; }
    public ParticleRenderMode getRenderMode() { return renderMode; }
    public Identifier getSpriteTexture() { return spriteTexture; }
    public ItemStack getItemStack() { return itemStack; }
    
    // Setters for initialization
    public Particle setPosition(float x, float y) {
        position.set(x, y);
        return this;
    }
    
    public Particle setVelocity(float x, float y) {
        velocity.set(x, y);
        return this;
    }
    
    public Particle setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
        return this;
    }
    
    public Particle setAngularVelocity(float x, float y, float z) {
        angularVelocity.set(x, y, z);
        return this;
    }
    
    public Particle setScale(float scale) {
        this.scale = scale;
        this.startScale = scale;
        this.endScale = scale;
        return this;
    }
    
    public Particle setScaleOverLifetime(float start, float end) {
        this.startScale = start;
        this.endScale = end;
        this.scale = start;
        return this;
    }
    
    public Particle setAlpha(float alpha) {
        this.alpha = alpha;
        this.startAlpha = alpha;
        this.endAlpha = alpha;
        return this;
    }
    
    public Particle setAlphaOverLifetime(float start, float end) {
        this.startAlpha = start;
        this.endAlpha = end;
        this.alpha = start;
        return this;
    }
    
    public Particle setColor(int color) {
        this.color = color;
        this.startColor = color;
        this.endColor = color;
        return this;
    }
    
    public Particle setColorOverLifetime(int start, int end) {
        this.startColor = start;
        this.endColor = end;
        this.color = start;
        return this;
    }
    
    public Particle setLifetime(float lifetime) {
        this.lifetime = lifetime;
        return this;
    }
    
    public Particle setGravity(float x, float y) {
        gravity.set(x, y);
        return this;
    }
    
    public Particle setRenderMode(ParticleRenderMode mode) {
        this.renderMode = mode;
        return this;
    }
    
    public Particle setSpriteTexture(Identifier texture) {
        this.spriteTexture = texture;
        this.renderMode = ParticleRenderMode.SPRITE;
        return this;
    }
    
    public Particle setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.renderMode = ParticleRenderMode.ITEMSTACK;
        return this;
    }
    
    /**
     * Reset this particle for reuse in object pooling.
     */
    public void reset() {
        position.zero();
        velocity.zero();
        rotation.zero();
        angularVelocity.zero();
        gravity.zero();
        scale = 1.0f;
        alpha = 1.0f;
        color = 0xFFFFFFFF;
        lifetime = 1.0f;
        age = 0.0f;
        alive = true;
        renderMode = ParticleRenderMode.SPRITE;
        spriteTexture = null;
        itemStack = ItemStack.EMPTY;
        startScale = 1.0f;
        endScale = 1.0f;
        startAlpha = 1.0f;
        endAlpha = 0.0f;
        startColor = 0xFFFFFFFF;
        endColor = 0xFFFFFFFF;
    }
}
