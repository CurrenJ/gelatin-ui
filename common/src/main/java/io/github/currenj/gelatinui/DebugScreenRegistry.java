package io.github.currenj.gelatinui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Server-safe registry for debug screen IDs.
 * This class does not reference any client-only classes like Screen.
 * Screen creation is handled on the client side via the packet handler.
 */
public class DebugScreenRegistry {
    private static final Set<String> registeredIds = new HashSet<>();
    private static final Map<String, Supplier<?>> screenSuppliers = new HashMap<>();
    private static final Map<String, String[]> idParts = new HashMap<>();

    /**
     * Register a debug screen ID with a supplier.
     * This method is server-safe and does not reference Screen class.
     * The supplier will be used on the client side only.
     */
    public static void register(String id, Supplier<?> screenSupplier) {
        registeredIds.add(id);
        screenSuppliers.put(id, screenSupplier);
        // Split by either forward slash or colon
        String[] parts = id.split("[/:]");
        idParts.put(id, parts);
    }

    /**
     * Get all registered screen IDs.
     * This is used by the command system to build the command tree.
     */
    public static Set<String> getRegisteredIds() {
        return new HashSet<>(registeredIds);
    }

    /**
     * Get the parsed parts of a screen ID.
     * Used by the command system to build nested command structures.
     */
    public static String[] getIdParts(String id) {
        return idParts.get(id);
    }

    /**
     * Create a screen instance from the registered supplier.
     * This should only be called on the client side.
     * @return The created screen object, or null if the ID is not registered
     */
    public static Object createScreen(String id) {
        Supplier<?> supplier = screenSuppliers.get(id);
        return supplier != null ? supplier.get() : null;
    }
}
