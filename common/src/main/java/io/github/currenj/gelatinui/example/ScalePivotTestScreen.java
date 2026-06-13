package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2f;

/**
 * Demo screen showcasing the scale pivot system.
 * Compares corner-pivot (default) vs center-pivot scaling side by side,
 * plus a custom pivot example.
 */
public class ScalePivotTestScreen extends GelatinUIScreen<GelatinMenu> {

    // Demo rectangles
    private Rectangle cornerRect;
    private Rectangle centerRect;
    private Rectangle customRect;

    // Scale state
    private float cornerScale = 1.0f;
    private float centerScale = 1.0f;
    private float customScale = 1.0f;

    // Status label
    private Label statusLabel;

    // Shared rectangle size
    private static final float RECT_SIZE = 60f;

    public ScalePivotTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Scale Pivot Test"));
    }

    @Override
    protected void buildUI() {
        MinecraftRenderContext tempContext = new MinecraftRenderContext(null, this.font);

        // ---- Demo rectangles (created early so fields are set for button callbacks) ----
        cornerRect = new Rectangle(RECT_SIZE, RECT_SIZE, UI.rgb(220, 80, 80));
        // Default pivot is (0,0) — top-left corner. No call needed.

        centerRect = new Rectangle(RECT_SIZE, RECT_SIZE, UI.rgb(80, 220, 80));
        centerRect.scaleFromCenter(); // Pivot at (size/2, size/2) — scales from center.

        customRect = new Rectangle(RECT_SIZE, RECT_SIZE, UI.rgb(80, 80, 220));
        // Pivot at bottom-center: scales upward from the bottom edge center
        customRect.setScalePivot(RECT_SIZE * 0.5f, RECT_SIZE);

        // ---- Build UI tree ----
        VBox mainContainer = UI.vbox()
                .alignment(VBox.Alignment.CENTER)
                .spacing(16)
                .padding(24)
                .scaleToHeight(this.height);

        // Title
        mainContainer.addChild(
                UI.label(tempContext, "Scale Pivot Test", 0xFFFFFFFF).scale(1.4f));

        // Description
        mainContainer.addChild(
                UI.label(tempContext,
                        "Demonstrates how the scale pivot affects where an element expands from.\n"
                                + "Use keys +/- to scale, R to reset. Buttons below control individual rectangles.",
                        0xFFB4B4B4));

        // ---- Row 1: Corner vs Center ----
        mainContainer.addChild(
                UI.label(tempContext, "Corner Pivot (default) vs Center Pivot", 0xFFFFD700));

        HBox compareRow = UI.hbox().spacing(40).alignment(HBox.Alignment.CENTER).padding(8);

        // Corner pivot panel
        VBox cornerPanel = makeDemoPanel(tempContext, "Corner Pivot (0, 0)",
                "Scales from top-left.\nExpands right + down.", 0xFFCC6666, cornerRect);
        compareRow.addChild(cornerPanel);

        // Center pivot panel
        VBox centerPanel = makeDemoPanel(tempContext, "Center Pivot (size/2, size/2)",
                "Scales from center.\nExpands evenly in all directions.", 0xFF66CC66, centerRect);
        compareRow.addChild(centerPanel);

        mainContainer.addChild(compareRow);

        // ---- Row 2: Custom Pivot ----
        mainContainer.addChild(
                UI.label(tempContext, "Custom Pivot (bottom-center)", 0xFFFFD700));

        VBox customPanel = makeDemoPanel(tempContext, "Custom Pivot (w/2, h)",
                "Pivot at bottom-center.\nScales upward + sideways.", 0xFF6688CC, customRect);
        mainContainer.addChild(customPanel);

        // ---- Control buttons ----
        mainContainer.addChild(UI.label(tempContext, "Scale Controls:", 0xFFC8C8FF));

        HBox controlRow1 = UI.hbox().spacing(8);
        controlRow1.addChild(makeScaleButton("Corner +", () -> adjustCornerScale(0.3f)));
        controlRow1.addChild(makeScaleButton("Corner -", () -> adjustCornerScale(-0.3f)));
        controlRow1.addChild(makeScaleButton("Center +", () -> adjustCenterScale(0.3f)));
        controlRow1.addChild(makeScaleButton("Center -", () -> adjustCenterScale(-0.3f)));
        controlRow1.addChild(makeScaleButton("Custom +", () -> adjustCustomScale(0.3f)));
        controlRow1.addChild(makeScaleButton("Custom -", () -> adjustCustomScale(-0.3f)));
        mainContainer.addChild(controlRow1);

        HBox controlRow2 = UI.hbox().spacing(8);
        controlRow2.addChild(makeScaleButton("Reset All", this::resetAllScales));
        controlRow2.addChild(makeScaleButton("Scale All Up", () -> adjustAllScales(0.3f)));
        controlRow2.addChild(makeScaleButton("Scale All Down", () -> adjustAllScales(-0.3f)));
        mainContainer.addChild(controlRow2);

        // Status label
        statusLabel = UI.label(tempContext, "All scales: 1.0  |  Press +/- to scale, R to reset", 0xFF969696);
        mainContainer.addChild(statusLabel);

        // Close button
        mainContainer.addChild(
                UI.spriteButton(100, 22, UI.rgb(180, 60, 60))
                        .text("Close", 0xFFFFFFFF)
                        .onClick(e -> onClose()));

        uiScreen.setRoot(mainContainer);
    }

    /**
     * Build a labelled panel containing a demo rectangle.
     */
    private VBox makeDemoPanel(MinecraftRenderContext ctx, String title, String description,
                               int labelColor, Rectangle rect) {
        VBox panel = UI.vbox()
                .alignment(VBox.Alignment.CENTER)
                .spacing(6)
                .padding(12);

        panel.addChild(UI.label(ctx, title, labelColor));
        panel.addChild(UI.label(ctx, description, 0xFFAAAAAA));

        // Wrap the rectangle in a fixed-size container so the panel doesn't resize as the rect scales
        ManualContainer rectContainer = new ManualContainer();
        rectContainer.setSize(180, 180);
        // Center the rect in the container (accounting for pivot-induced offset at scale=1)
        rect.setPosition(new Vector2f(60, 60));
        rectContainer.addChild(rect);
        panel.addChild(rectContainer);

        return panel;
    }

    private SpriteButton makeScaleButton(String label, Runnable action) {
        return UI.spriteButton(label.length() * 8 + 16, 22, UI.rgb(80, 80, 100))
                .text(label, 0xFFFFFFFF)
                .onClick(e -> action.run());
    }

    // ---- Scale adjustment helpers ----

    private void adjustCornerScale(float delta) {
        cornerScale = clampScale(cornerScale + delta);
        cornerRect.scale(cornerScale);
        updateStatus();
    }

    private void adjustCenterScale(float delta) {
        centerScale = clampScale(centerScale + delta);
        centerRect.scale(centerScale);
        updateStatus();
    }

    private void adjustCustomScale(float delta) {
        customScale = clampScale(customScale + delta);
        customRect.scale(customScale);
        updateStatus();
    }

    private void adjustAllScales(float delta) {
        adjustCornerScale(delta);
        adjustCenterScale(delta);
        adjustCustomScale(delta);
    }

    private void resetAllScales() {
        cornerScale = 1.0f;
        centerScale = 1.0f;
        customScale = 1.0f;
        cornerRect.scale(1.0f);
        centerRect.scale(1.0f);
        customRect.scale(1.0f);
        updateStatus();
    }

    private float clampScale(float s) {
        return Math.max(0.3f, Math.min(3.0f, s));
    }

    private void updateStatus() {
        if (statusLabel != null) {
            statusLabel.text(String.format(
                    "Corner: %.1fx  |  Center: %.1fx  |  Custom: %.1fx",
                    cornerScale, centerScale, customScale));
        }
    }

    // ---- Keyboard shortcuts ----

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return switch (event.key()) {
            case 61 -> { // = / +
                adjustAllScales(0.3f);
                yield true;
            }
            case 45 -> { // -
                adjustAllScales(-0.3f);
                yield true;
            }
            case 82 -> { // R
                resetAllScales();
                yield true;
            }
            default -> super.keyPressed(event);
        };
    }

    @Override
    protected void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Dark background
    }
}
