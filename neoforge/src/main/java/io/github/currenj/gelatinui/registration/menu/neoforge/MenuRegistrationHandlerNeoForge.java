package io.github.currenj.gelatinui.registration.menu.neoforge;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import io.github.currenj.gelatinui.registration.menu.IMenuRegistrationHandler;
import io.github.currenj.gelatinui.registration.menu.MenuRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuRegistrationHandlerNeoForge implements IMenuRegistrationHandler {
    public static final MenuRegistrationHandlerNeoForge INSTANCE = new MenuRegistrationHandlerNeoForge();
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, GelatinUi.MOD_ID);

    private MenuRegistrationHandlerNeoForge() {}

    @Override
    public void register(String id) {
        // Defer MenuType creation via DeferredRegister supplier.
        // The supplier fires during RegisterEvent, after BuiltInRegistries is bootstrapped.
        MENU_TYPES.register(id, () -> {
            MenuType<GelatinMenu> menuType = MenuRegistration.createDebugMenuType(id);
            MenuRegistration.storeDebugMenuType(id, menuType);
            return menuType;
        });
    }
}
