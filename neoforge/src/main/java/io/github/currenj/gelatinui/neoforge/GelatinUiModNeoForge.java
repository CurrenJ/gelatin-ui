package io.github.currenj.gelatinui.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.OpenTestScreenPacket;
import io.github.currenj.gelatinui.TestScreen;

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
        context.enqueueWork(() -> net.minecraft.client.Minecraft.getInstance().setScreen(new TestScreen()));
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        event.getDispatcher().register(net.minecraft.commands.Commands.literal("testui").executes(context -> {
            var source = context.getSource();
            if (source.getPlayer() != null) {
                PacketDistributor.sendToPlayer(source.getPlayer(), new OpenTestScreenPacket());
            }
            return 1;
        }));
    }
}
