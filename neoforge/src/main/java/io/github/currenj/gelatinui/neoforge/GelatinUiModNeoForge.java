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
import io.github.currenj.gelatinui.command.CommandUtils;

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

        // Build screen commands using common utility
        CommandUtils.buildScreenCommandTree(commandTree, (player, screenId) ->
            PacketDistributor.sendToPlayer(player, new OpenTestScreenPacket(screenId)));

        // Add tooltip example command
        CommandUtils.addTooltipExampleCommand(commandTree);

        // Add all root-level branches to the gelatin command
        for (LiteralArgumentBuilder<CommandSourceStack> branch : commandTree.values()) {
            gelatinCommand = gelatinCommand.then(branch);
        }

        event.getDispatcher().register(gelatinCommand);
    }
}
