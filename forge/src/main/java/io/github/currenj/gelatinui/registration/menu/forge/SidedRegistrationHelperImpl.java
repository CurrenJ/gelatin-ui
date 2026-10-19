package io.github.currenj.gelatinui.registration.menu.forge;

import io.github.currenj.gelatinui.registration.menu.IMenuRegistrationHandler;

public class SidedRegistrationHelperImpl {
    public static IMenuRegistrationHandler getMenuRegistrationHandler() {
        return MenuRegistrationHandlerForge.INSTANCE;
    }
}
