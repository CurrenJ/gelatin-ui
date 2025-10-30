package io.github.currenj.gelatinui.registration.menu;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class SidedRegistrationHelper {
    @ExpectPlatform
    public static IMenuRegistrationHandler getMenuRegistrationHandler() {
        throw new AssertionError();
    }
}
