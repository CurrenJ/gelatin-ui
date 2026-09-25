package io.github.currenj.gelatinui.forge;

import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import io.github.currenj.gelatinui.registration.menu.forge.MenuRegistrationHandlerForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.command.CommandUtils;

import java.util.HashMap;
import java.util.Map;

// G2 (docs/backport-pass2/track-b-1.20.1.md, gelatin-ui mc/1.20.1): unlike NeoForge, Forge 47
// on 1.20.1 has one @Mod entrypoint per mod (no constructor-injected IEventBus, no per-Dist
// second @Mod class) - the mod event bus comes from FMLJavaModLoadingContext.get(), and
// client-only registration lives on GelatinUiModForgeClient's static @Mod.EventBusSubscriber
// instead of a second constructor-injected class.
@Mod(GelatinUi.MOD_ID)
public final class GelatinUiModForge {
    public GelatinUiModForge() {
        // Run our common setup.
        GelatinUi.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        MenuRegistrationHandlerForge.MENU_TYPES.register(modEventBus);
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        var gelatinCommand = Commands.literal("gelatin");

        // Build a tree structure for nested commands
        Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree = new HashMap<>();

        // Build screen commands using common utility
        CommandUtils.buildScreenCommandTree(commandTree, MenuRegistration::openMenuById);

        // Add tooltip example command
        CommandUtils.addTooltipExampleCommand(commandTree);

        // Add debug animation speed commands
        CommandUtils.addDebugAnimationCommand(commandTree);

        // Add all root-level branches to the gelatin command
        for (LiteralArgumentBuilder<CommandSourceStack> branch : commandTree.values()) {
            gelatinCommand = gelatinCommand.then(branch);
        }

        event.getDispatcher().register(gelatinCommand);
    }
}
