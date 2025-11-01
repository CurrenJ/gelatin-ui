package io.github.currenj.gelatinui.registration.menu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MenuRegistrationEventTest {

    @BeforeEach
    public void setUp() {
        // Clear any existing listeners before each test
        // Note: In a real implementation, you might want to add a clearListeners() method
        // For now, we're testing the additive behavior
    }

    @Test
    public void testMenuRegistrationEventFires() {
        List<String> registeredMenus = new ArrayList<>();
        
        // Register a listener that uses the registrar
        MenuRegistrationEvent.registerListener(registrar -> {
            registrar.registerDebugMenu("test_menu_1");
        });

        // Create a test registrar that records calls
        MenuRegistrationEvent.MenuRegistrar testRegistrar = registeredMenus::add;

        // Fire the event
        MenuRegistrationEvent.fire(testRegistrar);

        // Verify that the listener was called and used the registrar
        assertTrue(registeredMenus.contains("test_menu_1"), "Menu should be registered");
    }

    @Test
    public void testMultipleListeners() {
        List<String> registeredMenus = new ArrayList<>();
        
        // Register multiple listeners
        MenuRegistrationEvent.registerListener(registrar -> {
            registrar.registerDebugMenu("menu_a");
        });
        
        MenuRegistrationEvent.registerListener(registrar -> {
            registrar.registerDebugMenu("menu_b");
        });

        // Create a test registrar
        MenuRegistrationEvent.MenuRegistrar testRegistrar = registeredMenus::add;

        // Fire the event
        MenuRegistrationEvent.fire(testRegistrar);

        // Verify both listeners were called
        assertTrue(registeredMenus.contains("menu_a"), "Menu A should be registered");
        assertTrue(registeredMenus.contains("menu_b"), "Menu B should be registered");
    }
}
