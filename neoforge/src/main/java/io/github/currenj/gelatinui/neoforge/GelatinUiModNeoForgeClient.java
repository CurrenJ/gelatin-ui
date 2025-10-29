package io.github.currenj.gelatinui.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.tooltip.ClientItemStacksTooltip;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;

@Mod(value = GelatinUi.MOD_ID, dist = Dist.CLIENT)
public final class GelatinUiModNeoForgeClient {
    public GelatinUiModNeoForgeClient(IEventBus modEventBus) {
        // Register client-side event listeners
        modEventBus.addListener(this::registerTooltipComponents);
    }

    private void registerTooltipComponents(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class, tooltip -> new ClientItemStacksTooltip(
            tooltip.items(),
            tooltip.renderItemDecorations()
        ));
    }
}

