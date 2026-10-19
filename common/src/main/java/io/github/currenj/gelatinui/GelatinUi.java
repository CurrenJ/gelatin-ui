package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.example.ExampleViews;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import io.github.currenj.gelatinui.registration.menu.MenuRegistrationEvent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);



    public static void init() {
        initCommon();
        fireMenuRegistrationEvent();
    }

    /**
     * Registers GelatinUI's own listeners without firing the registration event. Forge must fire
     * the event later, from its MENU registry callback, so that dependent mods constructed after
     * GelatinUI still get their listeners invoked.
     */
    public static void initCommon() {
        registerExampleMenus();
    }

    /**
     * Fires MenuRegistrationEvent to every registered listener. Must run after every mod's
     * listener has registered but before the MENU registry closes for new entries.
     */
    public static void fireMenuRegistrationEvent() {
        MenuRegistration.fireRegistrationEvent();
    }

    private static void registerExampleMenus() {
        MenuRegistrationEvent.registerListener(registrar -> {
            for (ResourceLocation viewId : ExampleViews.EXAMPLE_VIEWS) {
                registrar.registerDebugMenu(viewId.getPath());
            }
        });
    }
}
