package io.github.currenj.gelatinui.menu;

import io.github.currenj.gelatinui.GelatinUi;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for debug screen menu types.
 * Provides server-safe menu registration that can be used to open client-side screens.
 */
public class DebugMenuTypes {
    private static final Map<String, MenuType<DebugScreenMenu>> MENU_TYPES = new HashMap<>();
    
    /**
     * Register a menu type for a debug screen.
     * @param screenId The screen ID (e.g., "example/test")
     * @return The registered MenuType
     */
    public static MenuType<DebugScreenMenu> register(String screenId) {
        // Convert screenId to a valid resource location path (replace '/' with '_')
        String registryName = screenId.replace('/', '_').replace(':', '_');
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "debug_" + registryName);
        
        // Create menu type with a factory lambda. The lambda captures screenId but is NOT executed now.
        // It will only execute later when Minecraft needs to create a menu instance (client-side reconstruction).
        // By that time, line 48 below will have already executed, so MENU_TYPES will contain this entry.
        MenuType<DebugScreenMenu> menuType = new MenuType<>(
            (containerId, inventory) -> {
                // This lambda executes LATER, after registration completes.
                // Look up the already-registered menu type for this screen.
                MenuType<DebugScreenMenu> registeredType = MENU_TYPES.get(screenId);
                if (registeredType == null) {
                    throw new IllegalStateException("Menu type not yet registered for screen: " + screenId);
                }
                return new DebugScreenMenu(registeredType, containerId, screenId);
            },
            FeatureFlags.DEFAULT_FLAGS
        );
        
        // Register in Minecraft's registry
        Registry.register(BuiltInRegistries.MENU, id, menuType);
        
        // Store in our map for lookups
        MENU_TYPES.put(screenId, menuType);
        
        return menuType;
    }
    
    /**
     * Get the MenuType for a screen ID.
     * @param screenId The screen ID
     * @return The MenuType, or null if not registered
     */
    public static MenuType<DebugScreenMenu> getMenuType(String screenId) {
        return MENU_TYPES.get(screenId);
    }
    
    /**
     * Initialize all debug screen menu types.
     * This should be called during mod initialization.
     */
    public static void init() {
        // Register menu types for all debug screens
        register("example/test");
        register("example/tabs");
        register("example/input");
        register("example/scale2fit");
        register("example/effects");
        register("example/extension");
        register("example/alignment");
    }
}
