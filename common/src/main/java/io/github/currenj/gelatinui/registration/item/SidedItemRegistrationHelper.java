package io.github.currenj.gelatinui.registration.item;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * Platform-specific helper for accessing item registration handlers.
 * This class uses the ExpectPlatform pattern to delegate to platform-specific implementations.
 */
public class SidedItemRegistrationHelper {
    /**
     * Get the platform-specific item registration handler.
     * @return The item registration handler for the current platform
     */
    @ExpectPlatform
    public static IItemRegistrationHandler getItemRegistrationHandler() {
        throw new AssertionError();
    }
}
