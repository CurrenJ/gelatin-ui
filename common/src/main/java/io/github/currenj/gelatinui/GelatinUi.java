package io.github.currenj.gelatinui;

import org.slf4j.Logger;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // Write common init code here.
        // Screen registration has been moved to client-only initialization
        // to avoid loading client-only Screen class on the server.
        // See GelatinUiModFabricClient and GelatinUiModNeoForgeClient.
    }
}
