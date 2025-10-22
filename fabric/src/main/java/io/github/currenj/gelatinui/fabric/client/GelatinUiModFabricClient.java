package io.github.currenj.gelatinui.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import io.github.currenj.gelatinui.DebugScreenRegistry;
import io.github.currenj.gelatinui.OpenTestScreenPacket;

public final class GelatinUiModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        // Register packet receiver
        ClientPlayNetworking.registerGlobalReceiver(OpenTestScreenPacket.TYPE, (packet, context) -> {
            context.client().execute(() -> {
                Screen screen = DebugScreenRegistry.createScreen(packet.screenId());
                if (screen != null) {
                    Minecraft.getInstance().setScreen(screen);
                }
            });
        });
    }
}
