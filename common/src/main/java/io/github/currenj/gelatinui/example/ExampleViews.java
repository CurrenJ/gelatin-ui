package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUi;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ExampleViews {
    public static final ResourceLocation EXAMPLE_TEST_VIEW_ID = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "example/test");
    public static final ResourceLocation EXAMPLE_TABS_VIEW_ID = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/tabs");
    public static final ResourceLocation EXAMPLE_INPUT_VIEW_ID = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/input");
    public static final ResourceLocation EXAMPLE_SCALE2FIT_VIEW_ID = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/scale2fit");
    public static final ResourceLocation EXAMPLE_EFFECTS_VIEW_ID = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/effects");
    public static final ResourceLocation EXAMPLE_EXTENSION_VIEW_ID = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/extension");
    public static final ResourceLocation EXAMPLE_ALIGNMENT_VIEW_ID = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID,  "example/alignment");

    public static final List<ResourceLocation> EXAMPLE_VIEWS = List.of(
            EXAMPLE_TEST_VIEW_ID, EXAMPLE_TABS_VIEW_ID, EXAMPLE_INPUT_VIEW_ID, EXAMPLE_SCALE2FIT_VIEW_ID,
            EXAMPLE_EFFECTS_VIEW_ID, EXAMPLE_EXTENSION_VIEW_ID, EXAMPLE_ALIGNMENT_VIEW_ID);
}
