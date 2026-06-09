package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.IUIElement;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.UIContainer;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * Demo screen showcasing text wrapping in Label components.
 * Demonstrates wrapping at different widths, centered wrapping,
 * line spacing, and interaction with container layouts.
 */
public class TextWrappingTestScreen extends GelatinUIScreen<GelatinMenu> {

    public TextWrappingTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Text Wrapping Demo"));
    }

    @Override
    protected void buildUI() {
        String sampleText = "The quick brown fox jumps over the lazy dog. "
            + "This sentence is long enough to demonstrate word wrapping "
            + "at various maximum widths.";

        String shortText = "This short text won't wrap at all since it fits within even the narrowest column.";

        String longWordText = "Supercalifragilisticexpialidocious is a very long word that demonstrates "
            + "character-level fallback when a single word exceeds the maximum width.";

        // ── Main container ──
        VBox root = UI.vbox()
            .spacing(16)
            .padding(20)
            .fillWidth(true)
            .scaleToWidth(this.width);

        // ── Title ──
        Label title = new Label("Text Wrapping Demo", 0xFFFFFFFF, true)
            .scale(1.5f);
        root.addChild(title);

        // ── Section 1: Same text at different max widths ──
        Label section1Header = new Label("Same text, different max widths:", 0xFFAAAAFF);
        root.addChild(section1Header);

        HBox widthComparison = UI.hbox()
            .spacing(12)
            .alignment(HBox.Alignment.TOP)
            .padding(8)
            .backgroundColor(0x20FFFFFF);

        // Column at 100px
        VBox col100 = UI.vbox()
            .spacing(4)
            .padding(6);
        Label col100Header = new Label("100px", 0xFFFFAA00, true).maxWidth(100);
        Label col100Text = new Label(sampleText, 0xFFFFFFFF).maxWidth(100);
        col100.addChild(col100Header);
        col100.addChild(col100Text);
        widthComparison.addChild(col100);

        // Column at 150px
        VBox col150 = UI.vbox()
            .spacing(4)
            .padding(6);
        Label col150Header = new Label("150px", 0xFFFFAA00, true).maxWidth(150);
        Label col150Text = new Label(sampleText, 0xFFFFFFFF).maxWidth(150);
        col150.addChild(col150Header);
        col150.addChild(col150Text);
        widthComparison.addChild(col150);

        // Column at 200px
        VBox col200 = UI.vbox()
            .spacing(4)
            .padding(6);
        Label col200Header = new Label("200px", 0xFFFFAA00, true).maxWidth(200);
        Label col200Text = new Label(sampleText, 0xFFFFFFFF).maxWidth(200);
        col200.addChild(col200Header);
        col200.addChild(col200Text);
        widthComparison.addChild(col200);

        // Column at 300px
        VBox col300 = UI.vbox()
            .spacing(4)
            .padding(6);
        Label col300Header = new Label("300px", 0xFFFFAA00, true).maxWidth(300);
        Label col300Text = new Label(sampleText, 0xFFFFFFFF).maxWidth(300);
        col300.addChild(col300Header);
        col300.addChild(col300Text);
        widthComparison.addChild(col300);

        root.addChild(widthComparison);

        // ── Section 2: Centered wrapping with line spacing ──
        Label section2Header = new Label("Centered with line spacing:", 0xFFAAAAFF);
        root.addChild(section2Header);

        HBox centeredSection = UI.hbox()
            .spacing(16)
            .alignment(HBox.Alignment.TOP)
            .padding(8)
            .backgroundColor(0x20FFFFFF);

        // Centered, default line spacing
        VBox centeredDefault = UI.vbox()
            .spacing(4)
            .padding(6);
        Label centeredHeader1 = new Label("Centered, default spacing", 0xFF88FF88, true).maxWidth(180);
        Label centeredText1 = new Label(sampleText, 0xFFFFFFFF, true)
            .maxWidth(180);
        centeredDefault.addChild(centeredHeader1);
        centeredDefault.addChild(centeredText1);
        centeredSection.addChild(centeredDefault);

        // Centered, with extra line spacing
        VBox centeredSpaced = UI.vbox()
            .spacing(4)
            .padding(6);
        Label centeredHeader2 = new Label("Centered, spacing=4px", 0xFF88FF88, true).maxWidth(180);
        Label centeredText2 = new Label(sampleText, 0xFFFFFFFF, true)
            .maxWidth(180)
            .lineSpacing(4);
        centeredSpaced.addChild(centeredHeader2);
        centeredSpaced.addChild(centeredText2);
        centeredSection.addChild(centeredSpaced);

        root.addChild(centeredSection);

        // ── Section 3: Short text (no wrapping needed) ──
        Label section3Header = new Label("Short text (fits within max width):", 0xFFAAAAFF);
        root.addChild(section3Header);

        HBox shortSection = UI.hbox()
            .spacing(12)
            .alignment(HBox.Alignment.TOP)
            .padding(8)
            .backgroundColor(0x20FFFFFF);

        Label shortWrapped = new Label(shortText, 0xFFFFFFFF)
            .maxWidth(200);
        Label shortUnwrapped = new Label(shortText + " (no maxWidth set)", 0xFF888888);
        shortSection.addChild(shortWrapped);
        shortSection.addChild(shortUnwrapped);

        root.addChild(shortSection);

        // ── Section 4: Long word fallback ──
        Label section4Header = new Label("Long-word character fallback:", 0xFFAAAAFF);
        root.addChild(section4Header);

        VBox longWordSection = UI.vbox()
            .spacing(4)
            .padding(8)
            .backgroundColor(0x20FFFFFF);

        Label longWordLabel1 = new Label(longWordText, 0xFFFFFFFF)
            .maxWidth(150);
        Label longWordLabel2 = new Label(longWordText, 0xFFFF8888)
            .maxWidth(100)
            .lineSpacing(2);
        longWordSection.addChild(longWordLabel1);
        longWordSection.addChild(longWordLabel2);

        root.addChild(longWordSection);

        // ── Section 5: Wrapping inside a constrained VBox ──
        Label section5Header = new Label("Layout interaction (bounds test):", 0xFFAAAAFF);
        root.addChild(section5Header);

        // Fixed-width container with background to show that the wrapped label
        // correctly sizes the container height
        VBox constrainedBox = UI.vbox()
            .spacing(4)
            .padding(10)
            .backgroundColor(0x30FF8844);

        // Set a fixed width on the container; labels inside wrap to it
        constrainedBox.setSize(250, 0); // height will be computed by layout

        Label constrainedLabel1 = new Label("This container has a fixed width of 250px. "
            + "The labels inside wrap to fit, and the container's height grows "
            + "to accommodate all wrapped lines.", 0xFFFFFFFF)
            .maxWidth(230); // 250 - 20 padding
        Label constrainedLabel2 = new Label("This second label also wraps. "
            + "The VBox layout stacks them vertically with correct spacing, "
            + "demonstrating that bounds accurately reflect the wrapped text box.", 0xFF88CCFF)
            .maxWidth(230);

        constrainedBox.addChild(constrainedLabel1);
        constrainedBox.addChild(constrainedLabel2);
        root.addChild(constrainedBox);

        // ── Section 6: Mixed alignment ──
        Label section6Header = new Label("Left vs Center aligned wrapping:", 0xFFAAAAFF);
        root.addChild(section6Header);

        HBox mixedAlign = UI.hbox()
            .spacing(16)
            .alignment(HBox.Alignment.TOP)
            .padding(8)
            .backgroundColor(0x20FFFFFF);

        // Left-aligned wrapping
        VBox leftBox = UI.vbox()
            .spacing(4)
            .padding(6);
        Label leftHeader = new Label("Left-aligned, maxWidth=160", 0xFFCCCCCC, true).maxWidth(160);
        Label leftText = new Label(sampleText, 0xFFFFFFFF)
            .maxWidth(160);
        leftBox.addChild(leftHeader);
        leftBox.addChild(leftText);
        mixedAlign.addChild(leftBox);

        // Center-aligned wrapping
        VBox centerBox = UI.vbox()
            .spacing(4)
            .padding(6);
        Label centerHeader = new Label("Center-aligned, maxWidth=160", 0xFFCCCCCC, true).maxWidth(160);
        Label centerText = new Label(sampleText, 0xFFFFFFFF, true)
            .maxWidth(160);
        centerBox.addChild(centerHeader);
        centerBox.addChild(centerText);
        mixedAlign.addChild(centerBox);

        root.addChild(mixedAlign);

        // ── Close button ──
        SpriteButton closeBtn = UI.spriteButton(100, 20, UI.rgb(200, 50, 50))
            .text("Close", UI.rgb(255, 255, 255))
            .onClick(e -> onClose());

        root.addChild(closeBtn);

        uiScreen.setRoot(root);
    }

    /**
     * Initialize Label sizes from the render context before the first layout pass,
     * so that VBox/HBox containers see correct child dimensions immediately.
     * Without this, Labels would render at wrong positions on frame 1 and jump
     * on frame 2 after updateSize runs during renderSelf.
     */
    @Override
    protected void updateComponentSizes(MinecraftRenderContext context) {
        initializeChildSizes(uiScreen.getRoot(), context);
    }

    private void initializeChildSizes(IUIElement element, MinecraftRenderContext context) {
        if (element instanceof Label label) {
            label.updateSize(context);
        }
        if (element instanceof UIContainer<?> container) {
            for (IUIElement child : container.getChildren()) {
                initializeChildSizes(child, context);
            }
        }
    }

    @Override
    protected void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xFF0A0A0A);
    }
}
