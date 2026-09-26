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
        registerExampleMenus();
    }

    /**
     * Fires MenuRegistrationEvent to every registered listener. Must run after every mod's
     * listener has registered (each mod registers its own listener from its own constructor -
     * cross-mod construction order isn't guaranteed) but before this registry's RegisterEvent
     * is done processing (registerDebugMenu(id) ultimately calls a DeferredRegister, which only
     * accepts entries up to that point). See callers for the platform-specific hook that
     * satisfies both.
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
