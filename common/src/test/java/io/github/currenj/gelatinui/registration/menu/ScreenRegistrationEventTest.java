package io.github.currenj.gelatinui.registration.menu;

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

        // Register a listener
        ScreenRegistrationEvent.registerListener(registrar -> {
            registeredScreens.add("test_screen_1");
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

        // Verify that the listener was called
        assertTrue(registeredScreens.contains("test_screen_1"), "Screen should be registered");
    }

    @Test
    public void testMultipleScreenListeners() {
        List<String> registeredScreens = new ArrayList<>();

        // Register multiple listeners
        ScreenRegistrationEvent.registerListener(registrar -> {
            registeredScreens.add("screen_a");
        });

        ScreenRegistrationEvent.registerListener(registrar -> {
            registeredScreens.add("screen_b");
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
