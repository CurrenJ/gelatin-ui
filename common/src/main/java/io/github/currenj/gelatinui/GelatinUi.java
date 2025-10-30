package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.example.ExampleViews;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);



    public static void init() {
        // Write common, platform-agnostic init code here.

        registerExampleMenus();
    }

    private static void registerExampleMenus()
    {
        for (ResourceLocation viewId : ExampleViews.EXAMPLE_VIEWS) {
            MenuRegistration.registerDebugMenu(viewId.getPath());
        }
    }
}
