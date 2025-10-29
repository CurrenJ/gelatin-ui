package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SizeAlignmentTestScreen extends GelatinUIScreen {
    public SizeAlignmentTestScreen() {
        super(Component.literal("Size Alignment Test"));
    }

    private UIElement<?> uiElement;

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void buildUI() {
        // Create a temporary render context to measure text
        MinecraftRenderContext tempContext = new MinecraftRenderContext(
            new GuiGraphics(this.minecraft, this.minecraft.renderBuffers().bufferSource()),
            this.font
        );

        // Panel2 texture - has 9-pixel repeating segments in the tiled area
        ResourceLocation panelTex = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/panel2.png");
        SpriteData panel2 = SpriteData.texture(panelTex)
            .uv(0, 0, 23, 23)
            .tileScale(1f)
            .slice(7, 7, 7, 7)
            .textureSize(32, 32)
            .renderMode(SpriteRenderMode.TILE);

        // Main container
        VBox mainVBox = UI.vbox()
            .spacing(10)
            .padding(20)
            .alignment(VBox.Alignment.CENTER)
            .fillWidth(true);

        Label titleLabel = UI.label(tempContext, "Size Alignment Demo - Panel2 (14 + 9*x pattern)", UI.rgb(255, 255, 255));
        mainVBox.addChild(titleLabel);

        // Example 1: Without alignment - size 100x50 (not aligned to 14 + 9*x)
        VBox unalignedBox = UI.vbox()
            .spacing(3)
            .padding(5f)
            .alignment(VBox.Alignment.CENTER)
            .backgroundSprite(panel2);

        // Set a size that doesn't match the 14 + 9*x pattern - this will look wonky
        unalignedBox.setSize(100f, 50f);

        Label unalignedLabel = UI.label(tempContext, "No Alignment: 100x50 (wonky)", UI.rgb(255, 100, 100));
        unalignedBox.addChild(unalignedLabel);

        mainVBox.addChild(unalignedBox);

        // Example 2: With alignment - requested 100x50, aligned to pattern 14 + 9*x
        VBox alignedBox = UI.vbox()
            .spacing(3)
            .padding(5f)
            .alignment(VBox.Alignment.CENTER)
            .backgroundSprite(panel2)
            .alignWidthToMultiple(9, 14);  // Align to pattern: 14 + 9*x
        uiElement = alignedBox;

        // Request 100x50, will round to 104x50 (14 + 9*10 = 104, 50)
        alignedBox.setSize(100f, 50f);

        Label alignedLabel = UI.label(tempContext, "Aligned 14+9x: 104x50 (clean)", UI.rgb(100, 255, 100));
        alignedBox.addChild(alignedLabel);

        mainVBox.addChild(alignedBox);

        // Example 3: Another unaligned example with different size
        VBox unalignedBox2 = UI.vbox()
            .spacing(3)
            .padding(5f)
            .alignment(VBox.Alignment.CENTER)
            .backgroundSprite(panel2);

        unalignedBox2.setSize(85f, 40f);

        Label unalignedLabel2 = UI.label(tempContext, "No Alignment: 85x40", UI.rgb(255, 100, 100));
        unalignedBox2.addChild(unalignedLabel2);

        mainVBox.addChild(unalignedBox2);

        // Example 4: With alignment to 14 + 9*x pattern
        VBox alignedBox2 = UI.vbox()
            .spacing(3)
            .padding(5f)
            .alignment(VBox.Alignment.CENTER)
            .backgroundSprite(panel2)
            .alignWidthToMultiple(9, 14);

        alignedBox2.setSize(85f, 40f);  // Will become 86x41 (14 + 9*8 = 86, 14 + 9*3 = 41)

        Label alignedLabel2 = UI.label(tempContext, "Aligned 14+9x: 86x41", UI.rgb(100, 255, 100));
        alignedBox2.addChild(alignedLabel2);

        mainVBox.addChild(alignedBox2);

        // Example 5: HBox with width-only alignment with 14 + 9*x pattern
        HBox hbox = UI.hbox()
            .spacing(5)
            .padding(8)
            .alignment(HBox.Alignment.CENTER)
            .backgroundSprite(panel2)
            .alignWidthToMultiple(9, 14);  // Align width to 14 + 9*x

        hbox.setSize(95f, 30f);  // Width becomes 95 (14 + 9*9), height stays 30

        Label hboxLabel = UI.label(tempContext, "Width 14+9x only: 95x30", UI.rgb(255, 255, 255));
        hbox.addChild(hboxLabel);

        mainVBox.addChild(hbox);

        // Example 6: Demonstrating exact matches
        HBox exactBox = UI.hbox()
            .spacing(5)
            .padding(8)
            .alignment(HBox.Alignment.CENTER)
            .backgroundSprite(panel2)
            .alignWidthToMultiple(9, 14);

        exactBox.setSize(59f, 44f);  // Will become 59x44 (14 + 9*5 = 59, 44) - already exact!

        Label exactLabel = UI.label(tempContext, "Exact match: 59x44 = 14+9*5 x 44", UI.rgb(100, 255, 255));
        exactBox.addChild(exactLabel);

        mainVBox.addChild(exactBox);

        // Info text
        Label infoLabel = UI.label(tempContext, "Green = aligned, Red = unaligned", UI.rgb(200, 200, 200));
        mainVBox.addChild(infoLabel);

        // Close button
        SpriteButton closeBtn = UI.spriteButton(100, 20, UI.rgb(200, 50, 50))
            .text("Close", UI.rgb(255, 255, 255))
            .onClick(e -> onClose());

        mainVBox.addChild(closeBtn);

        // Set screen root
        uiScreen.setRoot(mainVBox);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // No additional content to render
        UIElement<?> element = uiElement;
    }
}
