package io.github.currenj.gelatinui.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.currenj.gelatinui.menu.DebugMenuTypes;
import io.github.currenj.gelatinui.tooltip.ItemStacksInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common command registration utilities for Gelatin UI.
 * Handles building the command tree and registering common commands.
 */
public final class CommandUtils {
    private CommandUtils() {
        // Utility class
    }

    // List of all registered screen IDs
    private static final String[] SCREEN_IDS = {
        "example/test",
        "example/tabs",
        "example/input",
        "example/scale2fit",
        "example/effects",
        "example/extension",
        "example/alignment"
    };

    /**
     * Builds the command tree for screen registration commands.
     * @param commandTree The map to populate with command branches
     */
    public static void buildScreenCommandTree(
            Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree) {

        for (String screenId : SCREEN_IDS) {
            String[] parts = screenId.split("[/:]");

            if (parts.length == 1) {
                // Simple case: single part ID
                LiteralArgumentBuilder<CommandSourceStack> command = LiteralArgumentBuilder.<CommandSourceStack>literal(parts[0])
                    .executes(context -> {
                        var source = context.getSource();
                        ServerPlayer player = source.getPlayer();
                        if (player != null) {
                            openDebugScreen(player, screenId);
                        }
                        return 1;
                    });
                commandTree.put(parts[0], command);
            } else {
                // Multi-part ID: build nested command structure
                buildCommandTree(commandTree, parts, screenId);
            }
        }
    }

    /**
     * Opens a debug screen for a player using the menu system.
     * @param player The player to open the screen for
     * @param screenId The screen ID to open
     */
    private static void openDebugScreen(ServerPlayer player, String screenId) {
        var menuType = DebugMenuTypes.getMenuType(screenId);
        if (menuType != null) {
            player.openMenu(new SimpleMenuProvider(
                (containerId, inventory, p) -> new io.github.currenj.gelatinui.menu.DebugScreenMenu(menuType, containerId, screenId),
                Component.literal("Debug Screen")
            ));
        }
    }

    /**
     * Adds the tooltip example command to the example branch if it exists.
     * @param commandTree The command tree map
     */
    public static void addTooltipExampleCommand(Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree) {
        if (commandTree.containsKey("example")) {
            LiteralArgumentBuilder<CommandSourceStack> exampleBranch = commandTree.get("example");
            exampleBranch.then(LiteralArgumentBuilder.<CommandSourceStack>literal("tooltip").executes(context -> {
                var source = context.getSource();
                if (source.getPlayer() != null) {
                    // Create ItemStacksTooltip with test items
                    List<ItemStack> tooltipItems = Arrays.asList(
                        new ItemStack(Items.DIAMOND, 3),
                        new ItemStack(Items.EMERALD, 2),
                        new ItemStack(Items.GOLD_INGOT, 5),
                        ItemStack.EMPTY,
                        new ItemStack(Items.IRON_INGOT, 10)
                    );
                    ItemStacksInfo info = new ItemStacksInfo(tooltipItems);

                    // Send chat message with hover tooltip
                    Component message = Component.literal("Hover over this message to see item stacks tooltip!")
                        .withStyle(style -> style.withHoverEvent(new HoverEvent(ItemStacksTooltip.SHOW_ITEM_STACKS, info)));

                    source.getPlayer().sendSystemMessage(message);
                }
                return 1;
            }));
        }
    }

    private static void buildCommandTree(
            Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree,
            String[] parts,
            String screenId) {

        // Get or create the root node
        String rootKey = parts[0];
        LiteralArgumentBuilder<CommandSourceStack> root = commandTree.get(rootKey);
        if (root == null) {
            root = LiteralArgumentBuilder.<CommandSourceStack>literal(rootKey);
            commandTree.put(rootKey, root);
        }

        // Navigate/build the tree
        LiteralArgumentBuilder<CommandSourceStack> current = root;
        Map<String, LiteralArgumentBuilder<CommandSourceStack>> currentChildren = getOrCreateChildMap(root);

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];

            if (i == parts.length - 1) {
                // Last part - this is the executable leaf node
                LiteralArgumentBuilder<CommandSourceStack> leaf = LiteralArgumentBuilder.<CommandSourceStack>literal(part)
                    .executes(context -> {
                        var source = context.getSource();
                        ServerPlayer player = source.getPlayer();
                        if (player != null) {
                            openDebugScreen(player, screenId);
                        }
                        return 1;
                    });
                current.then(leaf);
            } else {
                // Middle node - get or create
                LiteralArgumentBuilder<CommandSourceStack> child = currentChildren.get(part);
                if (child == null) {
                    child = LiteralArgumentBuilder.<CommandSourceStack>literal(part);
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

    private static Map<String, LiteralArgumentBuilder<CommandSourceStack>> getOrCreateChildMap(LiteralArgumentBuilder<CommandSourceStack> node) {
        return childMaps.computeIfAbsent(node, k -> new HashMap<>());
    }
}
