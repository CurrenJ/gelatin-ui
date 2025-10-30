package io.github.currenj.gelatinui.registration.menu.neoforge;

import io.github.currenj.gelatinui.registration.menu.IMenuRegistrationHandler;

public class SidedRegistrationHelperImpl {
    public static IMenuRegistrationHandler getMenuRegistrationHandler() {
        return MenuRegistrationHandlerNeoForge.INSTANCE;
    }
}
