package grill24.gelatinui;

import grill24.gelatinui.gui.UIScreen;
import grill24.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class GelatinUIScreen extends Screen {
    protected UIScreen uiScreen;
    private long lastFrameTimeNanos = 0L;

    protected GelatinUIScreen(Component title) {
        super(title);
        lastFrameTimeNanos = System.nanoTime();
    }

    @Override
    protected void init() {
        super.init();

        uiScreen = new UIScreen(this.width, this.height);
        uiScreen.setAutoCenterRoot(false); // Changed to false
        uiScreen.setAutoCenterThreshold(0.5f);

        buildUI();
    }

    protected abstract void buildUI();

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderContent(guiGraphics, mouseX, mouseY, partialTick);

        // Calculate delta time
        long nowNanos = System.nanoTime();
        float deltaSeconds = Math.min(0.1f, Math.max(0f, (nowNanos - lastFrameTimeNanos) / 1_000_000_000f));
        lastFrameTimeNanos = nowNanos;

        // Render UI
        if (uiScreen != null) {
            MinecraftRenderContext renderContext = new MinecraftRenderContext(guiGraphics, this.font);

            uiScreen.update(deltaSeconds);
            updateComponentSizes(renderContext);
            uiScreen.update(0f);
            uiScreen.onMouseMove(mouseX, mouseY);
            uiScreen.update(0f);
            uiScreen.render(renderContext);
        }
    }

    protected abstract void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    protected void updateComponentSizes(MinecraftRenderContext context) {
        // Default implementation: do nothing
        // Subclasses can override to update specific component sizes
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (uiScreen != null) {
            uiScreen.onMouseMove((int) mouseX, (int) mouseY);
            if (uiScreen.onMouseClick((int) mouseX, (int) mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (uiScreen != null) {
            if (uiScreen.onMouseScroll((int) mouseX, (int) mouseY, (float) scrollY)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void resize(net.minecraft.client.Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        if (uiScreen != null) {
            uiScreen.resize(width, height);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Check for debug toggle keys
        // Key '8' = GLFW_KEY_8 = 56
        // Key '9' = GLFW_KEY_9 = 57
        // Key '0' = GLFW_KEY_0 = 48
        // Key '7' = GLFW_KEY_7 = 55
        if (keyCode == 56) { // Key '8' - Toggle bounds debug
            grill24.gelatinui.gui.UIElement.toggleDebugBounds();
            return true;
        } else if (keyCode == 57) { // Key '9' - Toggle grid debug
            grill24.gelatinui.gui.UIElement.toggleDebugGrid();
            return true;
        } else if (keyCode == 48) { // Key '0' - Toggle padding debug
            grill24.gelatinui.gui.UIElement.toggleDebugPadding();
            return true;
        } else if (keyCode == 55) { // Key '7' - Toggle culled elements debug
            grill24.gelatinui.gui.UIElement.toggleDebugCulled();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
