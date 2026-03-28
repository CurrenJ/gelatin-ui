package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.example.ExampleViews;
import io.github.currenj.gelatinui.item.Items;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import io.github.currenj.gelatinui.registration.menu.MenuRegistrationEvent;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        Items.init();

        registerExampleMenus();
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
