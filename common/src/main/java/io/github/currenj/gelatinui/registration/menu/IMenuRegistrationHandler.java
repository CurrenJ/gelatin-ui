package io.github.currenj.gelatinui.registration.menu;

/**
 * Platform-specific menu registration handler.
 * Each platform creates and registers {@link net.minecraft.world.inventory.MenuType}
 * instances at the appropriate time (e.g., NeoForge defers creation until
 * registries are bootstrapped).
 */
public interface IMenuRegistrationHandler {
    /**
     * Register a debug menu with the given id.
     * The implementation must create the MenuType and register it
     * with the platform's registry system.
     * @param id The menu id (e.g., "example/test")
     */
    void register(String id);
}
