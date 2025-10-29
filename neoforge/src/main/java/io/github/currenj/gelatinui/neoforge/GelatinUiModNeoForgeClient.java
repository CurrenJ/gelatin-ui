package io.github.currenj.gelatinui.neoforge;

import io.github.currenj.gelatinui.*;
import io.github.currenj.gelatinui.menu.DebugMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

@Mod(value = GelatinUi.MOD_ID, dist = Dist.CLIENT)
public final class GelatinUiModNeoForgeClient {
    public GelatinUiModNeoForgeClient(IEventBus modEventBus) {
        // Register client-side event listeners
        modEventBus.addListener(this::registerTooltipComponents);
        modEventBus.addListener(this::onClientSetup);
    }

    private void registerTooltipComponents(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class, tooltip -> new ClientItemStacksTooltip(
            tooltip.items(),
            tooltip.renderItemDecorations()
        ));
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(this::registerScreenFactories);
    }

    private void registerScreenFactories() {
        registerScreenFactory("example/test", TestScreen::new);
        registerScreenFactory("example/tabs", TabsTestScreen::new);
        registerScreenFactory("example/input", InputComponentsTestScreen::new);
        registerScreenFactory("example/scale2fit", ScaleToFitTestScreen::new);
        registerScreenFactory("example/effects", EffectsTestScreen::new);
        registerScreenFactory("example/extension", GraphicsExtensionTestScreen::new);
        registerScreenFactory("example/alignment", SizeAlignmentTestScreen::new);
    }

    private void registerScreenFactory(String screenId, ScreenFactory factory) {
        var menuType = DebugMenuTypes.getMenuType(screenId);
        if (menuType != null) {
            MenuScreens.register(menuType, (menu, inventory, title) -> factory.create());
        }
    }

    @FunctionalInterface
    private interface ScreenFactory {
        Screen create();
    }
}

