package io.github.currenj.gelatinui.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import io.github.currenj.gelatinui.DebugScreenRegistry;
import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.OpenTestScreenPacket;

import java.util.HashMap;
import java.util.Map;

@Mod(GelatinUi.MOD_ID)
public final class GelatinUiModNeoForge {
    public GelatinUiModNeoForge(IEventBus modEventBus) {
        // Run our common setup.
        GelatinUi.init();

        // Register ourselves for server and other game events we are interested in
        modEventBus.addListener(this::registerPackets);
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(GelatinUi.MOD_ID);
        registrar.playToClient(OpenTestScreenPacket.TYPE, OpenTestScreenPacket.CODEC, this::handleOpenTestScreen);
    }

    private void handleOpenTestScreen(final OpenTestScreenPacket packet, final net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            Screen screen = DebugScreenRegistry.createScreen(packet.screenId());
            if (screen != null) {
                net.minecraft.client.Minecraft.getInstance().setScreen(screen);
            }
        });
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        var gelatinCommand = net.minecraft.commands.Commands.literal("gelatin");

        // Build a tree structure for nested commands
        Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree = new HashMap<>();

        for (String id : DebugScreenRegistry.getRegisteredIds()) {
            String[] parts = DebugScreenRegistry.getIdParts(id);
            final String screenId = id; // Capture the ID properly for lambda

            if (parts.length == 1) {
                // Simple case: single part ID
                gelatinCommand = gelatinCommand.then(net.minecraft.commands.Commands.literal(parts[0]).executes(context -> {
                    var source = context.getSource();
                    if (source.getPlayer() != null) {
                        PacketDistributor.sendToPlayer(source.getPlayer(), new OpenTestScreenPacket(screenId));
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

        event.getDispatcher().register(gelatinCommand);
    }

    private void buildCommandTree(Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree,
                                   String[] parts, String screenId) {
        // Get or create the root node
        String rootKey = parts[0];
        LiteralArgumentBuilder<CommandSourceStack> root = commandTree.get(rootKey);
        if (root == null) {
            root = net.minecraft.commands.Commands.literal(rootKey);
            commandTree.put(rootKey, root);
        }

        // Navigate/build the tree
        LiteralArgumentBuilder<CommandSourceStack> current = root;
        Map<String, LiteralArgumentBuilder<CommandSourceStack>> currentChildren = getOrCreateChildMap(root);

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];

            if (i == parts.length - 1) {
                // Last part - this is the executable leaf node
                LiteralArgumentBuilder<CommandSourceStack> leaf = net.minecraft.commands.Commands.literal(part).executes(context -> {
                    var source = context.getSource();
                    if (source.getPlayer() != null) {
                        PacketDistributor.sendToPlayer(source.getPlayer(), new OpenTestScreenPacket(screenId));
                    }
                    return 1;
                });
                current.then(leaf);
            } else {
                // Middle node - get or create
                LiteralArgumentBuilder<CommandSourceStack> child = currentChildren.get(part);
                if (child == null) {
                    child = net.minecraft.commands.Commands.literal(part);
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
