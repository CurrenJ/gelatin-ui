package io.github.currenj.gelatinui;

import org.slf4j.Logger;

public final class GelatinUi {
    public static final String MOD_ID = "gelatinui";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        // Write common init code here.
        DebugScreenRegistry.register("example/test", TestScreen::new);
        DebugScreenRegistry.register("example/tabs", TabsTestScreen::new);
        DebugScreenRegistry.register("example/input", InputComponentsTestScreen::new);
        DebugScreenRegistry.register("example/scale2fit", ScaleToFitTestScreen::new);
        DebugScreenRegistry.register("example/effects", EffectsTestScreen::new);
        DebugScreenRegistry.register("example/extension", GraphicsExtensionTestScreen::new);
        DebugScreenRegistry.register("example/alignment", SizeAlignmentTestScreen::new);
    }
}
