package io.github.currenj.gelatinui.neoforge;

import io.github.currenj.gelatinui.GelatinUiClient;
import io.github.currenj.gelatinui.gui.minecraft.hires.HiResItemPipRenderer;
import io.github.currenj.gelatinui.gui.minecraft.hires.HiResItemRenderState;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = GelatinUi.MOD_ID, dist = Dist.CLIENT)
public final class GelatinUiModNeoForgeClient {
    public GelatinUiModNeoForgeClient(IEventBus modEventBus) {
        GelatinUiClient.init();

        // Register client-side event listeners
        modEventBus.addListener(this::registerTooltipComponents);
        modEventBus.addListener(this::registerMenuScreens);
        modEventBus.addListener(this::registerPictureInPictureRenderers);
    }

    private void registerTooltipComponents(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class, tooltip -> new ClientItemStacksTooltip(
            tooltip.items(),
            tooltip.renderItemDecorations()
        ));
    }

    private void registerMenuScreens(final RegisterMenuScreensEvent event) {
        ScreenRegistration.fireRegistrationEvent(event::register);
    }

    private void registerPictureInPictureRenderers(final RegisterPictureInPictureRenderersEvent event) {
        event.register(HiResItemRenderState.class, HiResItemPipRenderer::getOrCreate);
    }
}


