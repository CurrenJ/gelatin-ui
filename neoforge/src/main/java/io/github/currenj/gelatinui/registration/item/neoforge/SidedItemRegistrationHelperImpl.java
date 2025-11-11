package io.github.currenj.gelatinui.registration.item.neoforge;

import io.github.currenj.gelatinui.registration.item.IItemRegistrationHandler;

/**
 * NeoForge implementation bridge for SidedItemRegistrationHelper.
 * This class is called via the ExpectPlatform mechanism.
 */
public class SidedItemRegistrationHelperImpl {
    public static IItemRegistrationHandler getItemRegistrationHandler() {
        return ItemRegistrationHandlerNeoForge.INSTANCE;
    }
}
