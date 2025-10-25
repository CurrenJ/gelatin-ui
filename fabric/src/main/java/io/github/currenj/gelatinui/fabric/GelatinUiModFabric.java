package io.github.currenj.gelatinui.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.github.currenj.gelatinui.DebugScreenRegistry;
import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.OpenTestScreenPacket;

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

            for (String id : DebugScreenRegistry.getRegisteredIds()) {
                String[] parts = DebugScreenRegistry.getIdParts(id);
                final String screenId = id; // Capture the ID properly for lambda

                if (parts.length == 1) {
                    // Simple case: single part ID
                    gelatinCommand = gelatinCommand.then(Commands.literal(parts[0]).executes(context -> {
                        CommandSourceStack source = context.getSource();
                        if (source.getPlayer() != null) {
                            ServerPlayNetworking.send(source.getPlayer(), new OpenTestScreenPacket(screenId));
                        }
                        return 1;
                    }));
                } else {
                    // Multi-part ID: build nested command structure
                    // We need to build the tree from root to leaf, merging as we go
                    buildCommandTree(commandTree, parts, screenId);
                }
            }

            // Add all root-level branches to the gelatin command
            for (LiteralArgumentBuilder<CommandSourceStack> branch : commandTree.values()) {
                gelatinCommand = gelatinCommand.then(branch);
            }

            dispatcher.register(gelatinCommand);
        });
    }

    private void buildCommandTree(Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree,
                                   String[] parts, String screenId) {
        // Get or create the root node
        String rootKey = parts[0];
        LiteralArgumentBuilder<CommandSourceStack> root = commandTree.get(rootKey);
        if (root == null) {
            root = Commands.literal(rootKey);
            commandTree.put(rootKey, root);
        }

        // Navigate/build the tree
        LiteralArgumentBuilder<CommandSourceStack> current = root;
        Map<String, LiteralArgumentBuilder<CommandSourceStack>> currentChildren = getOrCreateChildMap(root);

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];

            if (i == parts.length - 1) {
                // Last part - this is the executable leaf node
                LiteralArgumentBuilder<CommandSourceStack> leaf = Commands.literal(part).executes(context -> {
                    CommandSourceStack source = context.getSource();
                    if (source.getPlayer() != null) {
                        ServerPlayNetworking.send(source.getPlayer(), new OpenTestScreenPacket(screenId));
                    }
                    return 1;
                });
                current.then(leaf);
            } else {
                // Middle node - get or create
                LiteralArgumentBuilder<CommandSourceStack> child = currentChildren.get(part);
                if (child == null) {
                    child = Commands.literal(part);
                    currentChildren.put(part, child);
                    current.then(child);
                }
                current = child;
                currentChildren = getOrCreateChildMap(child);
            }
        }
    }

    // Helper to track children for merging
    private static final Map<LiteralArgumentBuilder<CommandSourceStack>, Map<String, LiteralArgumentBuilder<CommandSourceStack>>> childMaps = new HashMap<>();

    private Map<String, LiteralArgumentBuilder<CommandSourceStack>> getOrCreateChildMap(LiteralArgumentBuilder<CommandSourceStack> node) {
        return childMaps.computeIfAbsent(node, k -> new HashMap<>());
    }
}
