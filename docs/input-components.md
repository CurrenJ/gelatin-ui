# New Input Components

This document describes the TextInput and Checkbox components added to the gelatin-ui library.

## TextInput Component

A text input field component for user text entry with support for placeholder text, character limits, focus states, and validation.

### Features

- **Text Entry**: Full keyboard input support with cursor positioning
- **Placeholder Text**: Grayed-out text shown when field is empty
- **Character Limits**: Configurable maximum length
- **Focus Management**: Visual feedback for focused state with customizable border colors
- **Cursor Blinking**: Animated cursor with proper positioning
- **Keyboard Navigation**: Support for arrow keys, Home, End, Backspace, and Delete
- **Event Callbacks**: Text change listener for real-time validation
- **Customizable Appearance**: Colors for text, placeholder, background, and borders

### Basic Usage

```java
TextInput usernameInput = new TextInput(200, 20)
    .placeholder("Enter username")
    .maxLength(16)
    .textColor(0xFFFFFFFF)
    .backgroundColor(0xFF1A1A1A)
    .borderColor(0xFF404040)
    .focusedBorderColor(0xFF00AAFF)
    .onTextChange(text -> System.out.println("Text changed: " + text));
```

### Configuration Methods

| Method | Description |
|--------|-------------|
| `text(String)` | Set the current text value |
| `placeholder(String)` | Set placeholder text shown when empty |
| `maxLength(int)` | Set maximum character limit |
| `textColor(int)` | Set text color (ARGB) |
| `placeholderColor(int)` | Set placeholder text color (ARGB) |
| `backgroundColor(int)` | Set background color (ARGB) |
| `borderColor(int)` | Set normal border color (ARGB) |
| `focusedBorderColor(int)` | Set focused border color (ARGB) |
| `alignment(TextAlignment)` | Set text alignment (LEFT, CENTER, RIGHT) |
| `onTextChange(listener)` | Set text change callback |

### Getters

| Method | Description |
|--------|-------------|
| `getText()` | Get current text value |
| `isFocused()` | Check if input is focused |
| `setFocused(boolean)` | Programmatically set focus state |

### Input Handling

To properly handle keyboard input, you need to forward events from your screen:

```java
@Override
public boolean charTyped(char character, int modifiers) {
    if (textInput.isFocused()) {
        textInput.charTyped(character);
        return true;
    }
    return super.charTyped(character, modifiers);
}

@Override
public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (textInput.isFocused()) {
        textInput.keyPressed(keyCode);
        return true;
    }
    return super.keyPressed(keyCode, scanCode, modifiers);
}
```

### Supported Key Codes

- **259**: Backspace - Delete character before cursor
- **261**: Delete - Delete character after cursor
- **263**: Left Arrow - Move cursor left
- **262**: Right Arrow - Move cursor right
- **268**: Home - Move cursor to start
- **269**: End - Move cursor to end

## Checkbox Component

A toggleable checkbox component with support for labels, custom colors, and optional textures.

### Features

- **Toggle State**: Simple checked/unchecked state management
- **Text Labels**: Optional label text displayed next to the checkbox
- **Hover Effects**: Different border color on hover
- **Click Animation**: Bounce effect on click
- **Custom Appearance**: Configurable colors for box, checkmark, border, and label
- **Texture Support**: Optional custom textures for checked/unchecked states
- **Event Callbacks**: State change listener

### Basic Usage

```java
Checkbox enableSound = new Checkbox()
    .label("Enable sound effects")
    .labelColor(0xFFFFFFFF)
    .checkColor(0xFF00FF00)
    .checked(true)
    .onCheckChange(checked -> 
        System.out.println("Sound " + (checked ? "enabled" : "disabled")));
```

### Configuration Methods

| Method | Description |
|--------|-------------|
| `checked(boolean)` | Set checked state |
| `toggle()` | Toggle the checked state |
| `label(String)` | Set label text next to checkbox |
| `labelColor(int)` | Set label text color (ARGB) |
| `labelSpacing(int)` | Set spacing between checkbox and label (pixels) |
| `boxColor(int)` | Set checkbox background color (ARGB) |
| `checkColor(int)` | Set checkmark color (ARGB) |
| `borderColor(int)` | Set normal border color (ARGB) |
| `hoverBorderColor(int)` | Set hover border color (ARGB) |
| `onCheckChange(listener)` | Set state change callback |

### Custom Textures

For a more polished look, you can use custom textures:

```java
Checkbox customCheckbox = new Checkbox()
    .uncheckedTexture(ResourceLocation.parse("mymod:textures/gui/checkbox_unchecked.png"))
    .checkedTexture(ResourceLocation.parse("mymod:textures/gui/checkbox_checked.png"))
    .uncheckedHoverTexture(ResourceLocation.parse("mymod:textures/gui/checkbox_unchecked_hover.png"))
    .checkedHoverTexture(ResourceLocation.parse("mymod:textures/gui/checkbox_checked_hover.png"));
```

### Getters

| Method | Description |
|--------|-------------|
| `isChecked()` | Get current checked state |

## Example: Form with Validation

Here's a complete example showing both components working together:

```java
public class SettingsScreen extends GelatinUIScreen {
    private TextInput usernameInput;
    private Checkbox agreeToTerms;
    private Label errorLabel;
    
    @Override
    protected void buildUI() {
        VBox container = new VBox().spacing(10).padding(20);
        
        // Username input
        container.addChild(new Label("Username:", 0xFFAAAAAA));
        usernameInput = new TextInput(200, 20)
            .placeholder("Enter username")
            .maxLength(16)
            .onTextChange(text -> validateForm());
        container.addChild(usernameInput);
        
        // Terms checkbox
        agreeToTerms = new Checkbox()
            .label("I agree to the terms")
            .onCheckChange(checked -> validateForm());
        container.addChild(agreeToTerms);
        
        // Error label
        errorLabel = new Label("", 0xFFFF5555);
        container.addChild(errorLabel);
        
        // Submit button
        SpriteButton submitBtn = new SpriteButton(100, 25, 0xFF0066CC)
            .text("Submit", 0xFFFFFFFF)
            .onClick(e -> submitForm());
        container.addChild(submitBtn);
        
        uiScreen.setRoot(container);
    }
    
    private void validateForm() {
        if (usernameInput.getText().isEmpty()) {
            errorLabel.text("Username required").color(0xFFFF5555);
        } else if (!agreeToTerms.isChecked()) {
            errorLabel.text("Must accept terms").color(0xFFFF5555);
        } else {
            errorLabel.text("✓ Form valid").color(0xFF00FF00);
        }
    }
    
    private void submitForm() {
        if (!usernameInput.getText().isEmpty() && agreeToTerms.isChecked()) {
            // Process form...
        }
    }
}
```

## Design Notes

### TextInput Implementation

- Uses a blinking cursor with 0.53-second intervals for visibility
- Cursor position tracked separately from text for proper keyboard navigation
- Text rendering uses context-aware positioning for proper alignment
- Focus is managed through click events on the component bounds
- Character filtering removes control characters (ASCII < 32)

### Checkbox Implementation

- Checkmark rendered as two connected lines forming a check symbol
- Label automatically included in component size calculation
- Supports both texture-based and procedural rendering
- Hover state updates immediately through event system
- Click animation uses the built-in `playClickBounce()` effect

## Integration Tips

1. **Focus Management**: Only one TextInput should be focused at a time. Consider implementing a focus manager if you have multiple inputs.

2. **Tab Navigation**: To add Tab key support for moving between inputs, track inputs in a list and handle the Tab key (keyCode 258) in your screen's `keyPressed` method.

3. **Form Validation**: Use the `onTextChange` and `onCheckChange` callbacks for real-time validation feedback.

4. **Styling Consistency**: Define color constants for your theme to maintain consistent appearance across components.

5. **Accessibility**: The label text in Checkbox makes it more user-friendly than a standalone checkbox.

## Future Enhancements

Potential improvements for these components:

- **TextInput**: Password masking, input validation patterns, copy/paste support, text selection
- **Checkbox**: Indeterminate state, radio button group variant, toggle switch style
- **Both**: Disabled state, tooltip support, animation customization

## Focus Management with Global Click Listeners

The TextInput component now uses a global click listener system for focus management. This allows clicks anywhere on the screen to affect focus state.

### Global Click Event System

GelatinUIScreen now exposes a global click event system:

```java
// Functional interface for global click listeners
@FunctionalInterface
public interface GlobalClickListener {
    void onGlobalClick(double mouseX, double mouseY, int button);
}

// Methods to manage global listeners
public void addGlobalClickListener(GlobalClickListener listener);
public void removeGlobalClickListener(GlobalClickListener listener);
```

### TextInput Focus Management

Each TextInput registers a global click listener that checks if clicks are inside or outside its bounds:

```java
public TextInput registerGlobalClickListener(GelatinUIScreen screen) {
    screen.addGlobalClickListener((mouseX, mouseY, button) -> {
        java.awt.geom.Rectangle2D bounds = getBounds();
        if (bounds != null && bounds.contains(mouseX, mouseY)) {
            setFocused(true);  // Click inside - gain focus
        } else {
            setFocused(false); // Click outside - lose focus
        }
    });
    return this;
}
```

### Usage in Screens

In your GelatinUIScreen subclass, register the global click listener for each TextInput:

```java
TextInput input = new TextInput(200, 20)
    .placeholder("Enter text")
    .registerGlobalClickListener(this); // Register global listener
```

This ensures that **any click outside the TextInput's bounds will deselect it**, even if the click is on other UI elements or empty space.

### Benefits

- **Global Focus Management**: Clicks anywhere on the screen can affect TextInput focus
- **Multiple TextInputs**: Each manages its own focus independently
- **Clean Architecture**: Separates UI event handling from focus logic
- **Flexible**: Can be extended for other components that need global click awareness
