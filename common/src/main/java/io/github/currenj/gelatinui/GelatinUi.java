package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.menu.DebugMenuTypes;
import org.slf4j.Logger;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // Initialize server-safe menu types for debug screens
        DebugMenuTypes.init();
    }
}
