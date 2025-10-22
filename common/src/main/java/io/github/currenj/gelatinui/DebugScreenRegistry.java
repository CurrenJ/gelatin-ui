package io.github.currenj.gelatinui;

import net.minecraft.client.gui.screens.Screen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class DebugScreenRegistry {
    private static final Set<String> registeredIds = new HashSet<>();
    private static final Map<String, Supplier<Screen>> screens = new HashMap<>();

    public static void register(String id, Supplier<Screen> screenSupplier) {
        registeredIds.add(id);
        screens.put(id, screenSupplier);
    }

    public static Set<String> getRegisteredIds() {
        return new HashSet<>(registeredIds);
    }

    public static Screen createScreen(String id) {
        Supplier<Screen> supplier = screens.get(id);
        return supplier != null ? supplier.get() : null;
    }
}
