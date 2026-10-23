package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.example.ExampleViews;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import io.github.currenj.gelatinui.registration.menu.MenuRegistrationEvent;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);



    public static void init() {
        initCommon();
        fireMenuRegistrationEvent();
    }

    /** Register example menu listeners without firing the registry-time event. */
    public static void initCommon() {
        registerExampleMenus();
    }

    /** Fire after every mod that may add a menu listener has been constructed. */
    public static void fireMenuRegistrationEvent() {
        MenuRegistration.fireRegistrationEvent();
    }

    private static void registerExampleMenus() {
        MenuRegistrationEvent.registerListener(registrar -> {
            for (Identifier viewId : ExampleViews.EXAMPLE_VIEWS) {
                registrar.registerDebugMenu(viewId.getPath());
            }
        });
    }
}
