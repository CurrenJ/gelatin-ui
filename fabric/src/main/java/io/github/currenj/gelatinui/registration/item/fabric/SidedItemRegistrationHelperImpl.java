package io.github.currenj.gelatinui.registration.item.fabric;

import io.github.currenj.gelatinui.registration.item.IItemRegistrationHandler;

/**
 * Fabric implementation bridge for SidedItemRegistrationHelper.
 * This class is called via the ExpectPlatform mechanism.
 */
public class SidedItemRegistrationHelperImpl {
    public static IItemRegistrationHandler getItemRegistrationHandler() {
        return ItemRegistrationHandlerFabric.INSTANCE;
    }
}
