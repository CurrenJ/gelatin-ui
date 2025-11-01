package io.github.currenj.gelatinui.registration.menu;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScreenRegistrationEventTest {

    @Test
    public void testScreenRegistrationEventFires() {
        List<String> registeredScreens = new ArrayList<>();

        // Register a listener that uses the registrar
        ScreenRegistrationEvent.registerListener(registrar -> {
            registrar.register("test_screen_1", (MenuScreens.ScreenConstructor<GelatinMenu, GelatinUIScreen<GelatinMenu>>) null);
        });

        // Create a test registrar that records registrations
        ScreenRegistrationEvent.ScreenRegistrar testRegistrar = new ScreenRegistrationEvent.ScreenRegistrar() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    String id, MenuScreens.ScreenConstructor<M, U> screenFactory) {
                registeredScreens.add(id);
            }

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
                registeredScreens.add("menuType:" + menuType.toString());
            }

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    String id, ScreenRegistration.ScreenConstructor<M, U> screenFactory) {
                registeredScreens.add(id);
            }
        };

        // Fire the event
        ScreenRegistrationEvent.fire(testRegistrar);

        // Verify that the listener was called and used the registrar
        assertTrue(registeredScreens.contains("test_screen_1"), "Screen should be registered");
    }

    @Test
    public void testMultipleScreenListeners() {
        List<String> registeredScreens = new ArrayList<>();

        // Register multiple listeners that use the registrar
        ScreenRegistrationEvent.registerListener(registrar -> {
            registrar.register("screen_a", (MenuScreens.ScreenConstructor<GelatinMenu, GelatinUIScreen<GelatinMenu>>) null);
        });

        ScreenRegistrationEvent.registerListener(registrar -> {
            registrar.register("screen_b", (MenuScreens.ScreenConstructor<GelatinMenu, GelatinUIScreen<GelatinMenu>>) null);
        });

        // Create a test registrar
        ScreenRegistrationEvent.ScreenRegistrar testRegistrar = new ScreenRegistrationEvent.ScreenRegistrar() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    String id, MenuScreens.ScreenConstructor<M, U> screenFactory) {
                registeredScreens.add(id);
            }

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    MenuType<M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {
                registeredScreens.add("menuType");
            }

            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(
                    String id, ScreenRegistration.ScreenConstructor<M, U> screenFactory) {
                registeredScreens.add(id);
            }
        };

        // Fire the event
        ScreenRegistrationEvent.fire(testRegistrar);

        // Verify both listeners were called
        assertTrue(registeredScreens.contains("screen_a"), "Screen A should be registered");
        assertTrue(registeredScreens.contains("screen_b"), "Screen B should be registered");
    }
}
