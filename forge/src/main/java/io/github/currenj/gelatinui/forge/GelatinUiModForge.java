package io.github.currenj.gelatinui.forge;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.command.CommandUtils;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import io.github.currenj.gelatinui.registration.menu.forge.MenuRegistrationHandlerForge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.HashMap;
import java.util.Map;

@Mod(GelatinUi.MOD_ID)
public final class GelatinUiModForge {
    public GelatinUiModForge() {
        GelatinUi.initCommon();

        BusGroup modBusGroup = FMLJavaModLoadingContext.get().getModBusGroup();
        RegisterEvent.getBus(modBusGroup).addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.MENU)) {
                GelatinUi.fireMenuRegistrationEvent();
            }
        });

        RegisterCommandsEvent.BUS.addListener(this::registerCommands);
        MenuRegistrationHandlerForge.MENU_TYPES.register(modBusGroup);
    }

    private void registerCommands(RegisterCommandsEvent event) {
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
