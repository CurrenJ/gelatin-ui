package io.github.currenj.gelatinui.forge;

import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import io.github.currenj.gelatinui.registration.menu.forge.MenuRegistrationHandlerForge;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
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
        // Run our common setup (registers GelatinUI's own example-menu listener).
        GelatinUi.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Firing MenuRegistrationEvent straight from this constructor raced a dependent mod's
        // (e.g. Fishtastic's) own constructor, which is where it registers its listener - FML
        // doesn't guarantee construction order between mods, so the fire could run against zero
        // listeners, silently skipping every one of that mod's registerDebugMenu(id) calls and
        // later crashing MenuScreens.register with a null MenuType when its screens try to
        // resolve them. Firing from this registry's own RegisterEvent instead is safe: FML always
        // finishes constructing every mod before any RegisterEvent fires for any mod, regardless
        // of construction order, and RegisterEvent still fires before MENU_TYPES' own listener
        // (registered afterward on this same bus, so it runs after) closes the registry for new
        // registerDebugMenu -> DeferredRegister entries. (ScreenRegistration doesn't need this:
        // it just defers to FMLClientSetupEvent, since it isn't backed by a DeferredRegister.)
        modEventBus.addListener((final RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.MENU)) {
                GelatinUi.fireMenuRegistrationEvent();
            }
        });

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
