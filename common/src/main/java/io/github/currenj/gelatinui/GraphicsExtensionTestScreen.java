package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.extension.IGuiGraphicsExtension;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.components.Label;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class GraphicsExtensionTestScreen extends GelatinUIScreen {
    public GraphicsExtensionTestScreen() {
        super(Component.literal("Graphics Extension Test"));
    }

    @Override
    protected void buildUI() {
        // Create a temporary render context to measure text
        MinecraftRenderContext tempContext = new MinecraftRenderContext(
            new GuiGraphics(this.minecraft, this.minecraft.renderBuffers().bufferSource()),
            this.font
        );

        // Simple label
        Label titleLabel = UI.label(tempContext, "Testing fillQuad Extension", UI.rgb(255, 255, 255));
        uiScreen.setRoot(titleLabel);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        IGuiGraphicsExtension gfxExt = (IGuiGraphicsExtension) guiGraphics;

        // Rectangle (should look like a normal fill)
        gfxExt.gelatinui$fillQuad(
                RenderType.gui(),
                50, 50,
                150, 50,
                150, 100,
                50, 100,
                0, UI.rgb(255, 0, 0) // Red
        );

        // Parallelogram
        gfxExt.gelatinui$fillQuad(
                RenderType.gui(),
                200, 50,
                250, 30,
                250, 80,
                200, 100,
                0, UI.rgb(0, 255, 0) // Green
        );

        // Trapezoid
        gfxExt.gelatinui$fillQuad(
                RenderType.gui(),
                50, 150,
                150, 130,
                140, 180,
                60, 200,
                0, UI.rgb(0, 0, 255) // Blue
        );

        // Diamond (rhombus)
        gfxExt.gelatinui$fillQuad(
                RenderType.gui(),
                200, 150,
                225, 125,
                250, 150,
                225, 175,
                0, UI.rgb(255, 255, 0) // Yellow
        );

        // Irregular quad
        gfxExt.gelatinui$fillQuad(
                RenderType.gui(),
                50, 250,
                120, 230,
                140, 270,
                70, 290,
                0, UI.rgb(255, 0, 255) // Magenta
        );

        // Another shape
        gfxExt.gelatinui$fillQuad(
                RenderType.gui(),
                200, 250,
                280, 240,
                270, 280,
                190, 290,
                0, UI.rgb(0, 255, 255) // Cyan
        );
    }
}
