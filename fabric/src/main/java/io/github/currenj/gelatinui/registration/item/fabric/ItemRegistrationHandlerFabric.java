package io.github.currenj.gelatinui.registration.item.fabric;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.registration.item.IItemRegistrationHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Fabric implementation of item registration handler.
 * Uses Fabric's registry API to register items.
 */
public class ItemRegistrationHandlerFabric implements IItemRegistrationHandler {
    public static final ItemRegistrationHandlerFabric INSTANCE = new ItemRegistrationHandlerFabric();

    private ItemRegistrationHandlerFabric() {}

    @Override
    public void register(String id, Item item) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, id);
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
    }
}
