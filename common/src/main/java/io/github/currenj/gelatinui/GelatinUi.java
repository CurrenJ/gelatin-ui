package io.github.currenj.gelatinui;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";

    public static void init() {
        // Write common init code here.
        DebugScreenRegistry.register("example/test", TestScreen::new);
        DebugScreenRegistry.register("example/tabs", TabsTestScreen::new);
        DebugScreenRegistry.register("example/input", InputComponentsTestScreen::new);
        DebugScreenRegistry.register("example/scale2fit", ScaleToFitTestScreen::new);
        DebugScreenRegistry.register("example/effects", EffectsTestScreen::new);
    }
}
