package io.github.currenj.gelatinui.fabric;

import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import io.github.currenj.gelatinui.registration.menu.SidedRegistrationHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.command.CommandUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GelatinUiModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup (registers GelatinUI's own menu listeners)

        // Fire menu registration after iterating all custom entrypoints
        // This allows dependent mods to register listeners in their "gelatinui:menu_registration" entrypoint
        List<EntrypointContainer<Runnable>> menuRegEntrypoints =
            FabricLoader.getInstance().getEntrypointContainers("gelatinui:menu_registration", Runnable.class);

        // Invoke all dependent mods' menu registration entrypoints
        for (EntrypointContainer<Runnable> container : menuRegEntrypoints) {
            try {
                container.getEntrypoint().run();
            } catch (Throwable t) {
                GelatinUi.LOGGER.error("Failed to invoke menu registration entrypoint from {}", container.getProvider().getMetadata().getId(), t);
            }
        }

        // Initialize GelatinUI common code
        GelatinUi.init();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var gelatinCommand = Commands.literal("gelatin");

            // Build a tree structure for nested commands
            Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree = new HashMap<>();

            // Build screen commands using common utility
            CommandUtils.buildScreenCommandTree(commandTree, MenuRegistration::openMenuById);

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
