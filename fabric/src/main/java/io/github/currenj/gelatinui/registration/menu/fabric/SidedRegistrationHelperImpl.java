package io.github.currenj.gelatinui.registration.menu.fabric;

import io.github.currenj.gelatinui.registration.menu.IMenuRegistrationHandler;

public class SidedRegistrationHelperImpl {
    public static IMenuRegistrationHandler getMenuRegistrationHandler() {
        return MenuRegistrationHandlerFabric.INSTANCE;
    }
}
