package io.github.currenj.gelatinui.fabric.client;

import io.github.currenj.gelatinui.GelatinUiClient;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.MenuScreens;

import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

public final class GelatinUiModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        GelatinUiClient.init();

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

        ScreenRegistration.applyRegistrations(MenuScreens::register);
    }
}
