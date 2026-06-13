package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2f;

/**
 * Demo screen showing scale pivot behavior with Label elements.
 * Each row compares a different pivot point on the same label text,
 * so you can see exactly where each element expands from when scaled.
 */
public class LabelScalePivotTestScreen extends GelatinUIScreen<GelatinMenu> {

    // Label instances (one per pivot demo)
    private Label topLeftLabel;
    private Label topCenterLabel;
    private Label topRightLabel;
    private Label centerLeftLabel;
    private Label centerLabel;
    private Label centerRightLabel;
    private Label bottomLeftLabel;
    private Label bottomCenterLabel;
    private Label bottomRightLabel;

    // Scale state
    private float globalScale = 1.0f;

    // Status label
    private Label statusLabel;

    // Demo label text
    private static final String DEMO_TEXT = "Scale";

    public LabelScalePivotTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Label Scale Pivot Test"));
    }

    @Override
    protected void buildUI() {
        MinecraftRenderContext tempContext = new MinecraftRenderContext(null, this.font);

        // ---- Create all demo labels ----
        topLeftLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(255, 100, 100));
        // Default pivot (0,0) — top-left corner. No call needed.

        topCenterLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(255, 160, 60));
        topCenterLabel.setScalePivot(topCenterLabel.getSize().x * 0.5f, 0);

        topRightLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(255, 220, 40));
        topRightLabel.setScalePivot(topRightLabel.getSize().x, 0);

        centerLeftLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(100, 255, 100));
        centerLeftLabel.setScalePivot(0, centerLeftLabel.getSize().y * 0.5f);

        centerLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(60, 200, 255));
        centerLabel.scaleFromCenter();

        centerRightLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(130, 130, 255));
        centerRightLabel.setScalePivot(centerRightLabel.getSize().x, centerRightLabel.getSize().y * 0.5f);

        bottomLeftLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(255, 120, 200));
        bottomLeftLabel.setScalePivot(0, bottomLeftLabel.getSize().y);

        bottomCenterLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(200, 100, 255));
        bottomCenterLabel.setScalePivot(bottomCenterLabel.getSize().x * 0.5f, bottomCenterLabel.getSize().y);

        bottomRightLabel = makeLabel(tempContext, DEMO_TEXT, UI.rgb(180, 180, 180));
        bottomRightLabel.setScalePivot(bottomRightLabel.getSize().x, bottomRightLabel.getSize().y);

        // ---- Build UI tree ----
        VBox mainContainer = UI.vbox()
                .alignment(VBox.Alignment.CENTER)
                .spacing(12)
                .padding(20)
                .scaleToHeight(this.height);

        // Title
        mainContainer.addChild(
                UI.label(tempContext, "Label Scale Pivot Test", 0xFFFFFFFF).scale(1.4f));

        // Description
        mainContainer.addChild(
                UI.label(tempContext,
                        "Each \"Scale\" label uses a different pivot point.  Use keys +/- to scale, R to reset.",
                        0xFFAAAAAA));

        // ---- 3x3 grid of pivot demos ----
        // Top row: top-left, top-center, top-right
        HBox topRow = UI.hbox().spacing(24).alignment(HBox.Alignment.CENTER);
        topRow.addChild(makeDemoCell(tempContext, "Top-Left (0,0)", topLeftLabel));
        topRow.addChild(makeDemoCell(tempContext, "Top-Center (w/2, 0)", topCenterLabel));
        topRow.addChild(makeDemoCell(tempContext, "Top-Right (w, 0)", topRightLabel));
        mainContainer.addChild(topRow);

        // Middle row: center-left, center, center-right
        HBox midRow = UI.hbox().spacing(24).alignment(HBox.Alignment.CENTER);
        midRow.addChild(makeDemoCell(tempContext, "Center-Left (0, h/2)", centerLeftLabel));
        midRow.addChild(makeDemoCell(tempContext, "Center (w/2, h/2)", centerLabel));
        midRow.addChild(makeDemoCell(tempContext, "Center-Right (w, h/2)", centerRightLabel));
        mainContainer.addChild(midRow);

        // Bottom row: bottom-left, bottom-center, bottom-right
        HBox bottomRow = UI.hbox().spacing(24).alignment(HBox.Alignment.CENTER);
        bottomRow.addChild(makeDemoCell(tempContext, "Bottom-Left (0, h)", bottomLeftLabel));
        bottomRow.addChild(makeDemoCell(tempContext, "Bottom-Center (w/2, h)", bottomCenterLabel));
        bottomRow.addChild(makeDemoCell(tempContext, "Bottom-Right (w, h)", bottomRightLabel));
        mainContainer.addChild(bottomRow);

        // ---- Control buttons ----
        mainContainer.addChild(UI.label(tempContext, "Scale Controls:", 0xFFC8C8FF));

        HBox controlRow = UI.hbox().spacing(8);
        controlRow.addChild(makeButton("+0.2", () -> adjustScale(0.2f)));
        controlRow.addChild(makeButton("-0.2", () -> adjustScale(-0.2f)));
        controlRow.addChild(makeButton("+0.5", () -> adjustScale(0.5f)));
        controlRow.addChild(makeButton("-0.5", () -> adjustScale(-0.5f)));
        controlRow.addChild(makeButton("Reset (1.0)", this::resetScale));
        mainContainer.addChild(controlRow);

        // Status
        statusLabel = UI.label(tempContext, "Scale: 1.0x", 0xFF969696);
        mainContainer.addChild(statusLabel);

        // Close button
        mainContainer.addChild(
                UI.spriteButton(100, 22, UI.rgb(180, 60, 60))
                        .text("Close", 0xFFFFFFFF)
                        .onClick(e -> onClose()));

        uiScreen.setRoot(mainContainer);
    }

    /**
     * Create and initialize a label with the given text and color.
     */
    private Label makeLabel(MinecraftRenderContext ctx, String text, int color) {
        return UI.label(ctx, text, color);
    }

    /**
     * Build a labelled demo cell: a label describing the pivot, then the scaled label
     * inside a fixed-size container so the cell doesn't resize as the label scales.
     */
    private VBox makeDemoCell(MinecraftRenderContext ctx, String pivotDescription, Label demoLabel) {
        VBox cell = UI.vbox()
                .alignment(VBox.Alignment.CENTER)
                .spacing(4)
                .padding(10);

        cell.addChild(UI.label(ctx, pivotDescription, 0xFFCCCCCC));

        // Fixed-size container so the cell stays stable as the label scales
        ManualContainer container = new ManualContainer();
        container.setSize(140, 60);
        // Center the label in the container
        Vector2f labelSize = demoLabel.getSize();
        demoLabel.setPosition(new Vector2f(
                (140 - labelSize.x) * 0.5f,
                (60 - labelSize.y) * 0.5f));
        container.addChild(demoLabel);
        cell.addChild(container);

        return cell;
    }

    private SpriteButton makeButton(String label, Runnable action) {
        return UI.spriteButton(label.length() * 9 + 16, 22, UI.rgb(80, 80, 100))
                .text(label, 0xFFFFFFFF)
                .onClick(e -> action.run());
    }

    // ---- Scale helpers ----

    private Label[] allLabels() {
        return new Label[] {
            topLeftLabel, topCenterLabel, topRightLabel,
            centerLeftLabel, centerLabel, centerRightLabel,
            bottomLeftLabel, bottomCenterLabel, bottomRightLabel
        };
    }

    private void adjustScale(float delta) {
        globalScale = clampScale(globalScale + delta);
        for (Label label : allLabels()) {
            label.scale(globalScale);
        }
        updateStatus();
    }

    private void resetScale() {
        globalScale = 1.0f;
        for (Label label : allLabels()) {
            label.scale(1.0f);
        }
        updateStatus();
    }

    private float clampScale(float s) {
        return Math.max(0.5f, Math.min(4.0f, s));
    }

    private void updateStatus() {
        if (statusLabel != null) {
            statusLabel.text(String.format("Scale: %.1fx", globalScale));
        }
    }

    // ---- Keyboard shortcuts ----

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
        return switch (event.key()) {
            case 61 -> { // = / +
                adjustScale(0.2f);
                yield true;
            }
            case 45 -> { // -
                adjustScale(-0.2f);
                yield true;
            }
            case 82 -> { // R
                resetScale();
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
