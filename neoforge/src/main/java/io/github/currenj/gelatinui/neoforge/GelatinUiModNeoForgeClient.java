package io.github.currenj.gelatinui.neoforge;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import io.github.currenj.gelatinui.*;
import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

@Mod(value = GelatinUi.MOD_ID, dist = Dist.CLIENT)
public final class GelatinUiModNeoForgeClient {
    public GelatinUiModNeoForgeClient(IEventBus modEventBus) {
        // Register debug screens (client-only)
        registerDebugScreens();
        
        // Register client-side event listeners
        modEventBus.addListener(this::registerTooltipComponents);
        modEventBus.addListener(this::registerClientPacketHandlers);
    }

    /**
     * Register all debug test screens.
     * This method references client-only Screen classes and should only be called from client code.
     */
    private void registerDebugScreens() {
        DebugScreenRegistry.register("example/test", TestScreen::new);
        DebugScreenRegistry.register("example/tabs", TabsTestScreen::new);
        DebugScreenRegistry.register("example/input", InputComponentsTestScreen::new);
        DebugScreenRegistry.register("example/scale2fit", ScaleToFitTestScreen::new);
        DebugScreenRegistry.register("example/effects", EffectsTestScreen::new);
        DebugScreenRegistry.register("example/extension", GraphicsExtensionTestScreen::new);
        DebugScreenRegistry.register("example/alignment", SizeAlignmentTestScreen::new);
    }

    private void registerTooltipComponents(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class, tooltip -> new ClientItemStacksTooltip(
            tooltip.items(),
            tooltip.renderItemDecorations()
        ));
    }

    /**
     * Register client-side packet handlers.
     * This is where the actual screen opening logic lives, isolated from server code.
     */
    private void registerClientPacketHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(GelatinUi.MOD_ID);
        registrar.playToClient(OpenTestScreenPacket.TYPE, OpenTestScreenPacket.CODEC, this::handleOpenTestScreen);
    }

    /**
     * Handle the OpenTestScreenPacket on the client side.
     * This method references client-only classes and should only be called from client code.
     */
    private void handleOpenTestScreen(final OpenTestScreenPacket packet, final net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            Object screenObj = DebugScreenRegistry.createScreen(packet.screenId());
            if (screenObj instanceof Screen screen) {
                net.minecraft.client.Minecraft.getInstance().setScreen(screen);
            }
        });
    }
}

