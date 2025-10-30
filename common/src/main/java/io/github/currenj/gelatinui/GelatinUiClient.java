package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.example.*;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistration;

public final class GelatinUiClient {

    public static void init() {
        // Write client-specific, platform-agnostic init code here.

        registerExampleScreens();
    }

    private static void registerExampleScreens() {
        ScreenRegistration.register(ExampleViews.EXAMPLE_TEST_VIEW_ID.getPath(), TestScreen::new);
        ScreenRegistration.register(ExampleViews.EXAMPLE_TABS_VIEW_ID.getPath(), TabsTestScreen::new);
        ScreenRegistration.register(ExampleViews.EXAMPLE_INPUT_VIEW_ID.getPath(), InputComponentsTestScreen::new);
        ScreenRegistration.register(ExampleViews.EXAMPLE_SCALE2FIT_VIEW_ID.getPath(), ScaleToFitTestScreen::new);
        ScreenRegistration.register(ExampleViews.EXAMPLE_EFFECTS_VIEW_ID.getPath(), EffectsTestScreen::new);
        ScreenRegistration.register(ExampleViews.EXAMPLE_EXTENSION_VIEW_ID.getPath(), GraphicsExtensionTestScreen::new);
        ScreenRegistration.register(ExampleViews.EXAMPLE_ALIGNMENT_VIEW_ID.getPath(), SizeAlignmentTestScreen::new);
    }
}
