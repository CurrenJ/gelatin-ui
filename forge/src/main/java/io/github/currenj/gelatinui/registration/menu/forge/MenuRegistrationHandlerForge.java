package io.github.currenj.gelatinui.registration.menu.forge;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import io.github.currenj.gelatinui.registration.menu.IMenuRegistrationHandler;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;

public class MenuRegistrationHandlerForge implements IMenuRegistrationHandler {
    public static final MenuRegistrationHandlerForge INSTANCE = new MenuRegistrationHandlerForge();
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, GelatinUi.MOD_ID);

    private MenuRegistrationHandlerForge() {}

    @Override
    public void register(String id) {
        MENU_TYPES.register(id, () -> {
            MenuType<GelatinMenu> menuType = MenuRegistration.createDebugMenuType(id);
            MenuRegistration.storeDebugMenuType(id, menuType);
            return menuType;
        });
    }
}
