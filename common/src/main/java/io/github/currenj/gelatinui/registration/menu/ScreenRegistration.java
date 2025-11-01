package io.github.currenj.gelatinui.registration.menu;

import io.github.currenj.gelatinui.GelatinUIScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.List;
import java.util.function.Consumer;

public class ScreenRegistration {
    public static final ScreenRegistration INSTANCE = new ScreenRegistration();
    private static final List<Consumer<MenuScreenRegisterer>> pendingRegistrations = new java.util.ArrayList<>();

    private ScreenRegistration() {}

    /**
     * Fires the screen registration event, allowing all listeners to register their screens.
     * This should be called at the appropriate time during client-side initialization.
     * @param registerer The platform-specific registerer to use
     */
    public static void fireRegistrationEvent(MenuScreenRegisterer registerer) {
        // Create a registrar that wraps the platform-specific registerer
        ScreenRegistrationEvent.ScreenRegistrar registrar = new ScreenRegistrationEvent.ScreenRegistrar() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    String id, MenuScreens.ScreenConstructor<M, U> screenFactory) {
                ScreenRegistration.register(id, screenFactory);
            }

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
                ScreenRegistration.register(menuType, screenConstructor);
            }

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    String id, ScreenConstructor<M, U> screenFactory) {
                ScreenRegistration.register(id, screenFactory);
            }
        };

        // Fire the event to all listeners
        ScreenRegistrationEvent.fire(registrar);

        // Apply all pending registrations to the platform-specific registerer
        applyRegistrations(registerer);
    }

    /**
     * Register a screen with the given menu id and screen factory.
     * This is typically called by event listeners during the registration event.
     * @param id The menu id
     * @param screenFactory The screen factory
     */
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(String id, MenuScreens.ScreenConstructor<M, U> screenFactory) {
        pendingRegistrations.add(registerer -> {
            MenuType<M> menu = (MenuType<M>) MenuRegistration.getDebugMenuTypeById(id);
            registerer.register(menu, screenFactory);
        });
    }

    /**
     * Register a screen with the given menu type and screen constructor.
     * This is typically called by event listeners during the registration event.
     * @param menuType The menu type
     * @param screenConstructor The screen constructor
     */
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
        pendingRegistrations.add(registerer -> {
            registerer.register(menuType, screenConstructor);
        });
    }

    /**
     * Register a screen with the given menu id and simplified screen factory.
     * This is typically called by event listeners during the registration event.
     * @param id The menu id
     * @param screenFactory The simplified screen factory
     */
    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(String id, ScreenConstructor<M, U> screenFactory) {
        MenuScreens.ScreenConstructor<M, U> wrappedFactory = (menu, inventory, component) -> screenFactory.create(menu, inventory);
        register(id, wrappedFactory);
    }

    @FunctionalInterface
    public interface ScreenConstructor<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> {
        U create(M menu, net.minecraft.world.entity.player.Inventory inventory);
    }

    @FunctionalInterface
    public interface MenuScreenRegisterer {
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor);
    }

    public static void applyRegistrations(MenuScreenRegisterer registerer) {
        for (Consumer<MenuScreenRegisterer> registration : pendingRegistrations) {
            registration.accept(registerer);
        }
    }
}
