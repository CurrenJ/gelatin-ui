package io.github.currenj.gelatinui.registration.menu;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Event fired when screens are being registered (client-side only).
 * Listeners can register their custom screens by calling methods on the registrar.
 */
public class ScreenRegistrationEvent {
    private static final List<Consumer<ScreenRegistrar>> listeners = new CopyOnWriteArrayList<>();

    /**
     * Register a listener to be called when screen registration happens.
     * @param listener The listener to register
     */
    public static void registerListener(Consumer<ScreenRegistrar> listener) {
        listeners.add(listener);
    }

    /**
     * Fire the event to all registered listeners.
     * @param registrar The registrar to use for screen registration
     */
    public static void fire(ScreenRegistrar registrar) {
        for (Consumer<ScreenRegistrar> listener : listeners) {
            listener.accept(registrar);
        }
    }

    /**
     * Interface for registering screens.
     */
    public interface ScreenRegistrar {
        /**
         * Register a screen with the given menu id and screen factory.
         * @param id The menu id
         * @param screenFactory The screen factory
         */
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                String id, MenuScreens.ScreenConstructor<M, U> screenFactory);

        /**
         * Register a screen with the given menu type and screen constructor.
         * @param menuType The menu type
         * @param screenConstructor The screen constructor
         */
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor);

        /**
         * Register a screen with the given menu id and simplified screen factory.
         * @param id The menu id
         * @param screenFactory The simplified screen factory
         */
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                String id, ScreenRegistration.ScreenConstructor<M, U> screenFactory);
    }
}
