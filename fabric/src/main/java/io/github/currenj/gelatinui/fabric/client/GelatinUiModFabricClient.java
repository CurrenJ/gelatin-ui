package io.github.currenj.gelatinui.fabric.client;

import io.github.currenj.gelatinui.*;
import io.github.currenj.gelatinui.menu.DebugMenuTypes;
import io.github.currenj.gelatinui.menu.DebugScreenMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;

import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

public final class GelatinUiModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        // Register tooltip components
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof ItemStacksTooltip tooltip) {
                return new ClientItemStacksTooltip(
                    tooltip.items(),
                    tooltip.renderItemDecorations()
                );
            }
            return null;
        });

        // Register screen factories for debug menus
        registerScreenFactories();
    }

    private void registerScreenFactories() {
        registerScreenFactory("example/test", TestScreen::new);
        registerScreenFactory("example/tabs", TabsTestScreen::new);
        registerScreenFactory("example/input", InputComponentsTestScreen::new);
        registerScreenFactory("example/scale2fit", ScaleToFitTestScreen::new);
        registerScreenFactory("example/effects", EffectsTestScreen::new);
        registerScreenFactory("example/extension", GraphicsExtensionTestScreen::new);
        registerScreenFactory("example/alignment", SizeAlignmentTestScreen::new);
    }

    private void registerScreenFactory(String screenId, ScreenFactory factory) {
        var menuType = DebugMenuTypes.getMenuType(screenId);
        if (menuType != null) {
            MenuScreens.register(menuType, (menu, inventory, title) -> factory.create());
        }
    }

    @FunctionalInterface
    private interface ScreenFactory {
        Screen create();
    }
}
