package io.github.currenj.gelatinui.registration.menu;

import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuRegistration {
    private static final Map<String, MenuType<GelatinMenu>> DEBUG_MENUS = new HashMap<>();
    private static final Map<String, String[]> ID_PARTS = new HashMap<>();

    public static void registerDebugMenu(String id) {
        MenuType<GelatinMenu> menuType = createDebugMenuType(id);
        SidedRegistrationHelper.getMenuRegistrationHandler().register(id, menuType);

        DEBUG_MENUS.put(id, menuType);
        String[] parts = id.split("[/:]");
        ID_PARTS.put(id, parts);
    }

    private static MenuType<GelatinMenu> createDebugMenuType(String id) {
        MenuType.MenuSupplier<GelatinMenu> menuSupplier = (i, inv) -> new GelatinMenu(getDebugMenuTypeById(id), i);
        return new MenuType<>(menuSupplier, FeatureFlags.VANILLA_SET);
    }

    public static MenuType<GelatinMenu> getDebugMenuTypeById(String id) {
        return DEBUG_MENUS.get(id);
    }

    public static List<String> getRegisteredDebugMenuIds() {
        return new ArrayList<>(DEBUG_MENUS.keySet());
    }

    public static String[] getIdParts(String id) {
        return ID_PARTS.get(id);
    }

    public static void openMenuById(ServerPlayer player, String id) {
        player.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, p) -> {
                    MenuType<GelatinMenu> menuType = MenuRegistration.getDebugMenuTypeById(id);
                    return new GelatinMenu(menuType, containerId);
                },
                Component.literal("Debug Screen: " + id)
        ));
    }
}
