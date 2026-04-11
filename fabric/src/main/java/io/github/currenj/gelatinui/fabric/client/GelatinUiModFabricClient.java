package io.github.currenj.gelatinui.fabric.client;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.GelatinUiClient;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.gui.screens.MenuScreens;

import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

import java.util.List;

public final class GelatinUiModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Fire screen registration after iterating all custom entrypoints
        // This allows dependent mods to register listeners in their "gelatinui:screen_registration" entrypoint
        List<EntrypointContainer<Runnable>> screenRegEntrypoints =
            FabricLoader.getInstance().getEntrypointContainers("gelatinui:screen_registration", Runnable.class);

        // Invoke all dependent mods' screen registration entrypoints
        for (EntrypointContainer<Runnable> container : screenRegEntrypoints) {
            try {
                container.getEntrypoint().run();
            } catch (Throwable t) {
                GelatinUi.LOGGER.error("Failed to invoke screen registration entrypoint from {}", container.getProvider().getMetadata().getId(), t);
            }
        }

        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        GelatinUiClient.init();

        // Register tooltip components
        ClientTooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof ItemStacksTooltip tooltip) {
                return new ClientItemStacksTooltip(
                    tooltip.items(),
                    tooltip.renderItemDecorations()
                );
            }
            return null;
        });

        // Fire screen registration after all client entrypoints have been initialized
        // This ensures dependent mods have had a chance to register their listeners
        // Note: Menu registration happens during onInitialize (main entrypoint) before registries freeze
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ScreenRegistration.fireRegistrationEvent(MenuScreens::register);
        });
    }
}
