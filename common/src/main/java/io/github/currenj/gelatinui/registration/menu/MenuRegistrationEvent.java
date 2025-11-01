package io.github.currenj.gelatinui.registration.menu;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Event fired when menus are being registered.
 * Listeners can register their custom menus by calling methods on the registrar.
 */
public class MenuRegistrationEvent {
    private static final List<Consumer<MenuRegistrar>> listeners = new CopyOnWriteArrayList<>();

    /**
     * Register a listener to be called when menu registration happens.
     * @param listener The listener to register
     */
    public static void registerListener(Consumer<MenuRegistrar> listener) {
        listeners.add(listener);
    }

    /**
     * Fire the event to all registered listeners.
     * @param registrar The registrar to use for menu registration
     */
    public static void fire(MenuRegistrar registrar) {
        for (Consumer<MenuRegistrar> listener : listeners) {
            listener.accept(registrar);
        }
    }

    /**
     * Interface for registering menus.
     */
    @FunctionalInterface
    public interface MenuRegistrar {
        /**
         * Register a debug menu with the given id.
         * @param id The menu id
         */
        void registerDebugMenu(String id);
    }
}
