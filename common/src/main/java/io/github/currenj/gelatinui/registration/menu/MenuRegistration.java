package io.github.currenj.gelatinui.registration.menu;

import io.github.currenj.gelatinui.GelatinUi;
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

    /**
     * Fires the menu registration event, allowing all listeners to register their menus.
     * This should be called at the appropriate time during mod initialization.
     */
    public static void fireRegistrationEvent() {
        MenuRegistrationEvent.fire(MenuRegistration::registerDebugMenu);
        GelatinUi.LOGGER.info("Fired menu registration event; registered {} debug menus.", DEBUG_MENUS.size());
    }

    /**
     * Register a debug menu. This is typically called by event listeners during the registration event.
     * MenuType creation is delegated to the platform-specific handler so each platform
     * can create it at the correct time (e.g., NeoForge defers until registries are bootstrapped).
     * @param id The menu id
     */
    public static void registerDebugMenu(String id) {
        SidedRegistrationHelper.getMenuRegistrationHandler().register(id);

        String[] parts = id.split("[/:]");
        ID_PARTS.put(id, parts);
    }

    /**
     * Create a MenuType for the given debug menu id.
     * The returned MenuType's factory will look itself up via {@link #getDebugMenuTypeById(String)},
     * so the MenuType must be stored in {@link #DEBUG_MENUS} before any menu is opened with it.
     */
    public static MenuType<GelatinMenu> createDebugMenuType(String id) {
        return new MenuType<>((i, inv) -> new GelatinMenu(getDebugMenuTypeById(id), i), FeatureFlags.VANILLA_SET);
    }

    /**
     * Store a created MenuType so it can be looked up by id.
     * Called by platform-specific handlers after MenuType creation.
     */
    public static void storeDebugMenuType(String id, MenuType<GelatinMenu> menuType) {
        DEBUG_MENUS.put(id, menuType);
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
