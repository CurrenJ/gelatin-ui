package io.github.currenj.gelatinui.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.gui.screens.Screen;

import io.github.currenj.gelatinui.DebugScreenRegistry;
import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.OpenTestScreenPacket;

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
        for (String id : DebugScreenRegistry.getRegisteredIds()) {
            event.getDispatcher().register(net.minecraft.commands.Commands.literal(id).executes(context -> {
                var source = context.getSource();
                if (source.getPlayer() != null) {
                    PacketDistributor.sendToPlayer(source.getPlayer(), new OpenTestScreenPacket(id));
                }
                return 1;
            }));
        }
    }
}
