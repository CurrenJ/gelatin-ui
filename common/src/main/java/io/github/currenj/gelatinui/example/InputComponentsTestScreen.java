package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Demo screen showcasing the TextInput and Checkbox components.
 */
public class InputComponentsTestScreen extends GelatinUIScreen<GelatinMenu> {

    private TextInput usernameInput;
    private TextInput emailInput;
    private Checkbox enableNotifications;
    private Checkbox agreeToTerms;
    private Label statusLabel;

    public InputComponentsTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Input Components Demo"));
    }

    @Override
    protected void buildUI() {
        // Main container with padding
        VBox mainContainer = new VBox()
                .spacing(15)
                .padding(20);

        // Title
        Label title = new Label("Input Components Demo", 0xFFFFFFFF, true)
                .scale(1.5f);
        mainContainer.addChild(title);

        // Username input section
        VBox usernameSection = new VBox().spacing(5);
        Label usernameLabel = new Label("Username:", 0xFFAAAAAA);
        usernameInput = new TextInput(250, 20)
                .placeholder("Enter your username")
                .maxLength(16)
                .textColor(0xFFFFFFFF)
                .backgroundColor(0xFF1A1A1A)
                .borderColor(0xFF404040)
                .focusedBorderColor(0xFF00AAFF)
                .onTextChange(text -> updateStatus("Username: " + text))
                .registerGlobalClickListener(this)
                .alignment(TextInput.TextAlignment.LEFT);
        usernameSection.addChild(usernameLabel);
        usernameSection.addChild(usernameInput);
        mainContainer.addChild(usernameSection);

        // Email input section
        VBox emailSection = new VBox().spacing(5);
        Label emailLabel = new Label("Email:", 0xFFAAAAAA);
        emailInput = new TextInput(250, 20)
                .placeholder("email@example.com")
                .maxLength(50)
                .textColor(0xFFFFFFFF)
                .backgroundColor(0xFF1A1A1A)
                .borderColor(0xFF404040)
                .focusedBorderColor(0xFF00AAFF)
                .registerGlobalClickListener(this);
        emailSection.addChild(emailLabel);
        emailSection.addChild(emailInput);
        mainContainer.addChild(emailSection);

        // Checkbox section
        VBox checkboxSection = new VBox().spacing(10);
        Label checkboxTitle = new Label("Settings:", 0xFFFFFFFF).scale(1.2f);
        checkboxSection.addChild(checkboxTitle);

        // Notification checkbox with label
        enableNotifications = new Checkbox()
                .label("Enable notifications")
                .labelColor(0xFFFFFFFF)
                .checkColor(0xFF00FF00)
                .onCheckChange(checked ->
                    updateStatus("Notifications " + (checked ? "enabled" : "disabled")));
        checkboxSection.addChild(enableNotifications);

        // Terms checkbox
        agreeToTerms = new Checkbox()
                .label("I agree to the terms and conditions")
                .labelColor(0xFFFFFFFF)
                .checkColor(0xFFFF5555)
                .onCheckChange(checked ->
                    updateStatus("Terms " + (checked ? "accepted" : "not accepted")));
        checkboxSection.addChild(agreeToTerms);

        // Dark mode checkbox
        Checkbox darkModeCheckbox = new Checkbox()
                .label("Dark mode")
                .labelColor(0xFFFFFFFF)
                .checked(true)
                .onCheckChange(checked ->
                    updateStatus("Dark mode " + (checked ? "on" : "off")));
        checkboxSection.addChild(darkModeCheckbox);

        mainContainer.addChild(checkboxSection);

        // Status label
        statusLabel = new Label("Ready", 0xFF00FFAA, false);
        mainContainer.addChild(statusLabel);

        // Submit button
        SpriteButton submitButton = new SpriteButton(150, 25, 0xFF0066CC)
                .text("Submit", 0xFFFFFFFF)
                .onClick(event -> handleSubmit());
        mainContainer.addChild(submitButton);

        uiScreen.setRoot(mainContainer);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Dark background
        graphics.fill(0, 0, this.width, this.height, 0xFF0A0A0A);
    }

    @Override
    protected void updateComponentSizes(MinecraftRenderContext context) {
        // Update text input sizes if needed
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.text(message).color(0xFF00FFAA);
        }
    }

    private void handleSubmit() {
        String username = usernameInput.getText();
        boolean terms = agreeToTerms.isChecked();

        if (username.isEmpty()) {
            statusLabel.text("Error: Username required").color(0xFFFF5555);
        } else if (!terms) {
            statusLabel.text("Error: Must accept terms").color(0xFFFF5555);
        } else {
            statusLabel.text("✓ Form submitted successfully!").color(0xFF00FF00);
        }
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        // Forward character input to the focused text input
        if (usernameInput.isFocused()) {
            usernameInput.charTyped(character);
            return true;
        }
        if (emailInput.isFocused()) {
            emailInput.charTyped(character);
            return true;
        }
        return super.charTyped(character, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Forward key presses to the focused text input
        if (usernameInput.isFocused()) {
            usernameInput.keyPressed(keyCode);
            return true;
        }
        if (emailInput.isFocused()) {
            emailInput.keyPressed(keyCode);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
