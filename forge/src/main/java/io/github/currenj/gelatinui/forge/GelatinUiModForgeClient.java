package io.github.currenj.gelatinui.forge;

import io.github.currenj.gelatinui.GelatinUiClient;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistration;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

@Mod.EventBusSubscriber(modid = GelatinUi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GelatinUiModForgeClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        GelatinUiClient.init();
        event.enqueueWork(() -> ScreenRegistration.fireRegistrationEvent(MenuScreens::register));
    }

    @SubscribeEvent
    public static void registerTooltipComponents(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class, tooltip -> new ClientItemStacksTooltip(
            tooltip.items(),
            tooltip.renderItemDecorations()
        ));
    }
}
