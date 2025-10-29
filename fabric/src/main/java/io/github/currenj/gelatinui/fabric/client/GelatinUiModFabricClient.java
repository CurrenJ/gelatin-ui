package io.github.currenj.gelatinui.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import io.github.currenj.gelatinui.*;
import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

public final class GelatinUiModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        // Register debug screens (client-only)
        registerDebugScreens();

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

        // Register packet receiver
        ClientPlayNetworking.registerGlobalReceiver(OpenTestScreenPacket.TYPE, (packet, context) -> {
            context.client().execute(() -> {
                Object screenObj = DebugScreenRegistry.createScreen(packet.screenId());
                if (screenObj instanceof Screen screen) {
                    Minecraft.getInstance().setScreen(screen);
                }
            });
        });
    }

    /**
     * Register all debug test screens.
     * This method references client-only Screen classes and should only be called from client code.
     */
    private void registerDebugScreens() {
        DebugScreenRegistry.register("example/test", TestScreen::new);
        DebugScreenRegistry.register("example/tabs", TabsTestScreen::new);
        DebugScreenRegistry.register("example/input", InputComponentsTestScreen::new);
        DebugScreenRegistry.register("example/scale2fit", ScaleToFitTestScreen::new);
        DebugScreenRegistry.register("example/effects", EffectsTestScreen::new);
        DebugScreenRegistry.register("example/extension", GraphicsExtensionTestScreen::new);
        DebugScreenRegistry.register("example/alignment", SizeAlignmentTestScreen::new);
    }
}
