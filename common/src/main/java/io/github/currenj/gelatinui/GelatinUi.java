package io.github.currenj.gelatinui;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";

    public static void init() {
        // Write common init code here.
        DebugScreenRegistry.register("testui", TestScreen::new);
    }
}
