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

@Mod(GelatinUi.MOD_ID)
public final class GelatinUiModForge {
    public GelatinUiModForge() {
        // Register our own listeners, but do NOT fire the menu event yet: a dependent mod may be
        // constructed after us and would otherwise miss it.
        GelatinUi.initCommon();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Fire from the MENU registry callback: every mod is constructed before RegisterEvent,
        // and this listener is registered before MENU_TYPES' own DeferredRegister listener, so it
        // runs before the registry closes.
        modEventBus.addListener((final RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.MENU)) {
                GelatinUi.fireMenuRegistrationEvent();
            }
        });

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        MenuRegistrationHandlerForge.MENU_TYPES.register(modEventBus);
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        var gelatinCommand = Commands.literal("gelatin");

        Map<String, LiteralArgumentBuilder<CommandSourceStack>> commandTree = new HashMap<>();

        CommandUtils.buildScreenCommandTree(commandTree, MenuRegistration::openMenuById);
        CommandUtils.addTooltipExampleCommand(commandTree);
        CommandUtils.addDebugAnimationCommand(commandTree);

        for (LiteralArgumentBuilder<CommandSourceStack> branch : commandTree.values()) {
            gelatinCommand = gelatinCommand.then(branch);
        }

        event.getDispatcher().register(gelatinCommand);
    }
}
