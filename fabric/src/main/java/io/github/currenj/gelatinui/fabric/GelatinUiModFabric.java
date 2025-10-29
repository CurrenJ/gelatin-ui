package io.github.currenj.gelatinui.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.OpenTestScreenPacket;
import io.github.currenj.gelatinui.command.CommandUtils;

import java.util.HashMap;
import java.util.Map;

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

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var gelatinCommand = Commands.literal("gelatin");

            // Build a tree structure for nested commands
            Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree = new HashMap<>();

            // Build screen commands using common utility
            CommandUtils.buildScreenCommandTree(commandTree, (player, screenId) ->
                ServerPlayNetworking.send(player, new OpenTestScreenPacket(screenId)));

            // Add tooltip example command
            CommandUtils.addTooltipExampleCommand(commandTree);

            // Add all root-level branches to the gelatin command
            for (LiteralArgumentBuilder<CommandSourceStack> branch : commandTree.values()) {
                gelatinCommand = gelatinCommand.then(branch);
            }

            dispatcher.register(gelatinCommand);
        });
    }
}
