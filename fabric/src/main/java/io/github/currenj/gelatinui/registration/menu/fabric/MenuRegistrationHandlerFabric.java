package io.github.currenj.gelatinui.registration.menu.fabric;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import io.github.currenj.gelatinui.registration.menu.IMenuRegistrationHandler;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

public class MenuRegistrationHandlerFabric implements IMenuRegistrationHandler {
    public static final MenuRegistrationHandlerFabric INSTANCE = new MenuRegistrationHandlerFabric();

    private MenuRegistrationHandlerFabric() {}

    @Override
    public void register(String id) {
        MenuType<GelatinMenu> menuType = MenuRegistration.createDebugMenuType(id);
        Identifier resourceLocation = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, id);
        Registry.register(BuiltInRegistries.MENU, resourceLocation, menuType);
        MenuRegistration.storeDebugMenuType(id, menuType);
    }
}
