package io.github.currenj.gelatinui.registration.menu.fabric;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.registration.menu.IMenuRegistrationHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class MenuRegistrationHandlerFabric implements IMenuRegistrationHandler {
    public static final MenuRegistrationHandlerFabric INSTANCE = new MenuRegistrationHandlerFabric();

    private MenuRegistrationHandlerFabric() {}

    @Override
    public void register(String id, MenuType<?> menuType) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, id);
        Registry.register(BuiltInRegistries.MENU, resourceLocation, menuType);
    }
}
