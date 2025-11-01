package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.example.*;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistration;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistrationEvent;

public final class GelatinUiClient {

    public static void init() {
        // Write client-specific, platform-agnostic init code here.

        registerScreenRegistrationListener();
    }

    private static void registerScreenRegistrationListener() {
        ScreenRegistrationEvent.registerListener(registrar -> {
            registrar.register(ExampleViews.EXAMPLE_TEST_VIEW_ID.getPath(), TestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_TABS_VIEW_ID.getPath(), TabsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_INPUT_VIEW_ID.getPath(), InputComponentsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_SCALE2FIT_VIEW_ID.getPath(), ScaleToFitTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_EFFECTS_VIEW_ID.getPath(), EffectsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_EXTENSION_VIEW_ID.getPath(), GraphicsExtensionTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_ALIGNMENT_VIEW_ID.getPath(), SizeAlignmentTestScreen::new);
        });
    }
}
