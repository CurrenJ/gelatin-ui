# Menu and Screen Registration

Gelatin UI provides an event-based registration system for menus (common/server) and screens (client-only). This allows mods to register their custom menus and screens at the appropriate time during initialization.

## Menu Registration (Common/Server)

Menus are registered on both client and server (common code) and must be registered before screens.

### Listening to the Menu Registration Event

To register menus, add a listener to the `MenuRegistrationEvent` during your mod's initialization:

```java
import io.github.currenj.gelatinui.registration.menu.MenuRegistrationEvent;

public class MyMod {
    public static void init() {
        // Register listener - the event will fire automatically after all mods initialize
        MenuRegistrationEvent.registerListener(registrar -> {
            // Register your menus here
            registrar.registerDebugMenu("my_menu_id");
            registrar.registerDebugMenu("another_menu_id");
        });
    }
}
```

**Note**: The Gelatin UI framework automatically fires the menu registration event during its initialization. You only need to register your listener; you don't need to call `MenuRegistration.fireRegistrationEvent()` yourself.

### When to Register Listeners

- **Register listeners**: During your mod's common initialization (in your mod's `init()` method)
- **Event fires**: Automatically after Gelatin UI's initialization completes
- **Platform-specific**: The actual registration is handled by platform-specific implementations (Fabric/NeoForge)

## Screen Registration (Client-Only)

Screens are registered only on the client side and link menu types to their screen implementations.

### Listening to the Screen Registration Event

To register screens, add a listener to the `ScreenRegistrationEvent` during your client initialization:

```java
import io.github.currenj.gelatinui.registration.menu.ScreenRegistrationEvent;

public class MyModClient {
    public static void init() {
        // Register listener - the event will fire automatically during client initialization
        ScreenRegistrationEvent.registerListener(registrar -> {
            // Register your screens here
            
            // Using simplified constructor (menu, inventory) -> screen
            registrar.register("my_menu_id", MyScreen::new);
            
            // Using full constructor (menu, inventory, title) -> screen
            registrar.register("another_menu_id", 
                (menu, inventory, title) -> new AnotherScreen(menu, inventory, title));
            
            // Using MenuType directly
            registrar.register(MY_MENU_TYPE, MyScreen::new);
        });
    }
}
```

**Note**: The Gelatin UI framework automatically fires the screen registration event during platform-specific client initialization. You only need to register your listener in your client init method.

### Platform-Specific Firing

The screen registration event is automatically fired by the platform-specific client initialization:

- **Fabric**: `GelatinUiModFabricClient.onInitializeClient()` calls `ScreenRegistration.fireRegistrationEvent()`
- **NeoForge**: `GelatinUiModNeoForgeClient.registerMenuScreens()` calls `ScreenRegistration.fireRegistrationEvent()`

You don't need to call these methods yourself - they are handled by the framework.

## Complete Example

Here's a complete example showing how to register both menus and screens:

```java
// Common initialization
public class MyMod {
    public static void init() {
        // Register menu listener - event fires automatically
        MenuRegistrationEvent.registerListener(registrar -> {
            registrar.registerDebugMenu("example_menu");
        });
    }
}

// Client initialization
public class MyModClient {
    public static void init() {
        ScreenRegistrationEvent.registerListener(registrar -> {
            registrar.register("example_menu", ExampleScreen::new);
        });
    }
}

// Your screen implementation
public class ExampleScreen extends GelatinUIScreen {
    public ExampleScreen(GelatinMenu menu, Inventory inventory) {
        super(menu, inventory, Component.literal("Example"));
    }
    
    @Override
    protected void buildUI(UIScreen uiScreen, MinecraftRenderContext context) {
        // Build your UI here
        uiScreen.setRoot(yourRootComponent);
    }
}
```

## Benefits of Event-Based Registration

1. **Decoupling**: Mods don't need to directly access registration APIs at specific lifecycle points
2. **Flexibility**: Multiple mods can listen and register their entries independently
3. **Timing Control**: The framework controls when registration happens, ensuring correct ordering
4. **Clean API**: Simple listener pattern that's familiar to mod developers
5. **Platform Agnostic**: Common code can register listeners without platform-specific concerns

## Migration from Direct Registration

If you were previously calling registration methods directly:

**Before:**
```java
MenuRegistration.registerDebugMenu("my_menu");
ScreenRegistration.register("my_menu", MyScreen::new);
```

**After:**
```java
// In common init
MenuRegistrationEvent.registerListener(registrar -> {
    registrar.registerDebugMenu("my_menu");
});

// In client init
ScreenRegistrationEvent.registerListener(registrar -> {
    registrar.register("my_menu", MyScreen::new);
});
```

The key difference is that you now register a listener that will be called when the appropriate registration event fires, rather than calling the registration methods directly.
