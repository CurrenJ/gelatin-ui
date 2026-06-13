package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.example.*;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistrationEvent;

public final class GelatinUiClient {

    public static void init() {
        // Write client-specific, platform-agnostic init code here.

        registerExampleScreens();
    }

    private static void registerExampleScreens() {
        ScreenRegistrationEvent.registerListener(registrar -> {
            registrar.register(ExampleViews.EXAMPLE_TEST_VIEW_ID.getPath(), TestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_TABS_VIEW_ID.getPath(), TabsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_INPUT_VIEW_ID.getPath(), InputComponentsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_SCALE2FIT_VIEW_ID.getPath(), ScaleToFitTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_EFFECTS_VIEW_ID.getPath(), EffectsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_ITEM_ANIMATIONS_VIEW_ID.getPath(), ItemAnimationsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_EXTENSION_VIEW_ID.getPath(), GraphicsExtensionTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_ALIGNMENT_VIEW_ID.getPath(), SizeAlignmentTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_MANUAL_CONTAINER_VIEW_ID.getPath(), ManualContainerTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_NESTED_TABS_VIEW_ID.getPath(), NestedTabsTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_TEXT_WRAPPING_VIEW_ID.getPath(), TextWrappingTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_SCALE_PIVOT_VIEW_ID.getPath(), ScalePivotTestScreen::new);
            registrar.register(ExampleViews.EXAMPLE_LABEL_SCALE_PIVOT_VIEW_ID.getPath(), LabelScalePivotTestScreen::new);
        });
    }
}
