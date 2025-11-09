package io.github.currenj.gelatinui.gui.particles;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the particle system.
 */
public class ParticleSystemTest {

    @Test
    public void testParticleCreation() {
        Particle particle = new Particle();
        assertTrue(particle.isAlive());
        assertEquals(1.0f, particle.getLifetime());
        assertEquals(0.0f, particle.getAge());
    }

    @Test
    public void testParticleUpdate() {
        Particle particle = new Particle();
        particle.setPosition(0, 0);
        particle.setVelocity(10, 20);
        particle.setLifetime(2.0f);
        
        // Update for 0.1 seconds
        particle.update(0.1f);
        
        // Position should have moved
        assertEquals(1.0f, particle.getPosition().x, 0.01f);
        assertEquals(2.0f, particle.getPosition().y, 0.01f);
        
        // Age should have increased
        assertEquals(0.1f, particle.getAge(), 0.01f);
        
        // Should still be alive
        assertTrue(particle.isAlive());
    }

    @Test
    public void testParticleGravity() {
        Particle particle = new Particle();
        particle.setVelocity(0, 0);
        particle.setGravity(0, 100);
        particle.setLifetime(2.0f);
        
        // Update for 0.1 seconds
        particle.update(0.1f);
        
        // Velocity should have changed due to gravity
        assertEquals(10.0f, particle.getVelocity().y, 0.01f);
    }

    @Test
    public void testParticleLifetime() {
        Particle particle = new Particle();
        particle.setLifetime(1.0f);
        
        // Update for 0.5 seconds
        particle.update(0.5f);
        assertTrue(particle.isAlive());
        
        // Update for another 0.6 seconds (total 1.1 seconds, exceeds lifetime)
        particle.update(0.6f);
        assertFalse(particle.isAlive());
    }

    @Test
    public void testParticleSystemEmit() {
        ParticleSystem system = new ParticleSystem();
        
        ParticleEmitter emitter = new ParticleEmitter()
            .setPosition(0, 0)
            .setLifetime(1.0f);
        
        assertEquals(0, system.getActiveParticleCount());
        
        system.emit(emitter, 10);
        assertEquals(10, system.getActiveParticleCount());
    }

    @Test
    public void testParticleSystemClear() {
        ParticleSystem system = new ParticleSystem();
        
        ParticleEmitter emitter = new ParticleEmitter()
            .setPosition(0, 0)
            .setLifetime(10.0f);
        
        system.emit(emitter, 20);
        assertEquals(20, system.getActiveParticleCount());
        
        system.clear();
        assertEquals(0, system.getActiveParticleCount());
    }
}
