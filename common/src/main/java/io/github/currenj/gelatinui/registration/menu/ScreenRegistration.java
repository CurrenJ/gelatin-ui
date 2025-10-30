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


    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(String id, MenuScreens.ScreenConstructor<M, U> screenFactory) {
        pendingRegistrations.add(registerer -> {
            MenuType<M> menu = (MenuType<M>) MenuRegistration.getDebugMenuTypeById(id);
            registerer.register(menu, screenFactory);
        });
    }

    public static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
        pendingRegistrations.add(registerer -> {
            registerer.register(menuType, screenConstructor);
        });
    }

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
