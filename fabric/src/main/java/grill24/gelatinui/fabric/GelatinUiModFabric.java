package grill24.gelatinui.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import grill24.gelatinui.GelatinUi;
import grill24.gelatinui.OpenTestScreenPacket;

public final class GelatinUiModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        GelatinUi.init();

        // Register packet
        PayloadTypeRegistry.playS2C().register(OpenTestScreenPacket.TYPE, OpenTestScreenPacket.CODEC);

        // Register command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("testui").executes(context -> {
                CommandSourceStack source = context.getSource();
                if (source.getPlayer() != null) {
                    ServerPlayNetworking.send(source.getPlayer(), new OpenTestScreenPacket());
                }
                return 1;
            }));
        });
    }
}
