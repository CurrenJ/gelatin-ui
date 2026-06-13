package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUi;
import net.minecraft.resources.Identifier;

import java.util.List;

public class ExampleViews {
    public static final Identifier EXAMPLE_TEST_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, "example/test");
    public static final Identifier EXAMPLE_TABS_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/tabs");
    public static final Identifier EXAMPLE_INPUT_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/input");
    public static final Identifier EXAMPLE_SCALE2FIT_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/scale2fit");
    public static final Identifier EXAMPLE_EFFECTS_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/effects");
    public static final Identifier EXAMPLE_ITEM_ANIMATIONS_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/item_animations");
    public static final Identifier EXAMPLE_EXTENSION_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/extension");
    public static final Identifier EXAMPLE_ALIGNMENT_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/alignment");
    public static final Identifier EXAMPLE_MANUAL_CONTAINER_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, "example/manual_container");
    public static final Identifier EXAMPLE_NESTED_TABS_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, "example/nested_tabs");
    public static final Identifier EXAMPLE_TEXT_WRAPPING_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, "example/text_wrapping");
    public static final Identifier EXAMPLE_SCALE_PIVOT_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, "example/scale_pivot");
    public static final Identifier EXAMPLE_LABEL_SCALE_PIVOT_VIEW_ID = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, "example/label_scale_pivot");

    public static final List<Identifier> EXAMPLE_VIEWS = List.of(
            EXAMPLE_TEST_VIEW_ID, EXAMPLE_TABS_VIEW_ID, EXAMPLE_INPUT_VIEW_ID, EXAMPLE_SCALE2FIT_VIEW_ID,
            EXAMPLE_EFFECTS_VIEW_ID, EXAMPLE_ITEM_ANIMATIONS_VIEW_ID, EXAMPLE_EXTENSION_VIEW_ID, EXAMPLE_ALIGNMENT_VIEW_ID,
            EXAMPLE_MANUAL_CONTAINER_VIEW_ID, EXAMPLE_NESTED_TABS_VIEW_ID, EXAMPLE_TEXT_WRAPPING_VIEW_ID,
            EXAMPLE_SCALE_PIVOT_VIEW_ID, EXAMPLE_LABEL_SCALE_PIVOT_VIEW_ID);
}
