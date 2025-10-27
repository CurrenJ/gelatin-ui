package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.UIEvent;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScaleToFitTestScreen extends GelatinUIScreen {
    public ScaleToFitTestScreen() {
        super(Component.literal("Scale To Fit Test"));
    }

    @Override
    protected void init() {
        super.init();
    }

    private VBox outerVBox;

    @Override
    protected void buildUI() {
        // Create a temporary render context to measure text
        MinecraftRenderContext tempContext = new MinecraftRenderContext(
            new GuiGraphics(this.minecraft, this.minecraft.renderBuffers().bufferSource()),
            this.font
        );

        // Panel texture
        ResourceLocation panelTex = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/panel.png");

        // Create a VBox that will scale its children to fit a larger area
        VBox smallTextVBox = UI.vbox()
            .spacing(3)
            .padding(3f)
            .alignment(VBox.Alignment.CENTER)
            .backgroundSprite(SpriteData.texture(panelTex)
                    .uv(0, 0, 16, 16)
                    .tileScale(1f)
                    .slice(6, 6, 6, 6)
                    .textureSize(16, 16));

        // Add small labels that should be scaled up
        Label smallLabel1 = UI.label(tempContext, "Small Text 1", UI.rgb(255, 0, 0));
        Label smallLabel2 = UI.label(tempContext, "Small Text 2", UI.rgb(0, 255, 0));
        Label smallLabel3 = UI.label(tempContext, "Small Text 3", UI.rgb(0, 0, 255));

        smallTextVBox.addChild(smallLabel1);
        smallTextVBox.addChild(smallLabel2);
        smallTextVBox.addChild(smallLabel3);

        HBox hTextHBox = UI.hbox()
            .spacing(5)
            .padding(8)
            .alignment(HBox.Alignment.CENTER)
            .backgroundSprite(SpriteData.texture(panelTex)
                    .uv(0, 0, 16, 16)
                    .tileScale(2f)
                    .slice(6, 6, 6, 6)
                    .textureSize(16, 16));

        Label hLabel1 = UI.label(tempContext, "H1", UI.rgb(255, 255, 0));
        Label hLabel2 = UI.label(tempContext, "H2", UI.rgb(255, 0, 255));
        Label hLabel3 = UI.label(tempContext, "H3", UI.rgb(0, 255, 255));

        hTextHBox.addChild(hLabel1);
        hTextHBox.addChild(hLabel2);
        hTextHBox.addChild(hLabel3);

        // Main container
        outerVBox = UI.vbox()
            .spacing(5)
            .padding(20)
            .alignment(VBox.Alignment.CENTER)
            .fillWidth(true);

        Label titleLabel = UI.label(tempContext, "Scale To Fit Test (Scaling Up)", UI.rgb(255, 255, 255));
        outerVBox.addChild(titleLabel);
        outerVBox.addChild(smallTextVBox);

        outerVBox.addChild(hTextHBox);

        // Close button
        SpriteButton closeBtn = UI.spriteButton(100, 20, UI.rgb(200, 50, 50))
            .text("Close", UI.rgb(255, 255, 255))
            .onClick(e -> onClose());

        outerVBox.addChild(closeBtn);

        // Set screen root
        uiScreen.setRoot(outerVBox);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean anyPressed = super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == 54) { // Key '6' = GLFW_KEY_
            outerVBox.scaleToHeight(this.uiScreen.getViewHeight());
            return true;
        }

        return false;
    }
}
