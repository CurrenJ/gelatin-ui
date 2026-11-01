package io.github.currenj.gelatinui.forge;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.GelatinUiClient;
import io.github.currenj.gelatinui.registration.menu.ScreenRegistration;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GelatinUi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class GelatinUiModForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        GelatinUiClient.init();
        event.enqueueWork(() -> ScreenRegistration.fireRegistrationEvent(MenuScreens::register));
    }

}
