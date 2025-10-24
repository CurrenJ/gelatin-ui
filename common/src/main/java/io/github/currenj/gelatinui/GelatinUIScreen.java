package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.UIScreen;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class GelatinUIScreen extends Screen {
    protected UIScreen uiScreen;
    private long lastFrameTimeNanos = 0L;

    // Global click listeners for elements that need to respond to clicks anywhere
    private final List<GlobalClickListener> globalClickListeners = new ArrayList<>();

    /**
     * Functional interface for global click listeners.
     */
    @FunctionalInterface
    public interface GlobalClickListener {
        void onGlobalClick(double mouseX, double mouseY, int button);
    }

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

            // Render time control status if not at default settings
            renderTimeControlStatus(guiGraphics);
        }
    }

    /**
     * Render time control status overlay when pause or timescale is active.
     */
    protected void renderTimeControlStatus(GuiGraphics guiGraphics) {
        if (io.github.currenj.gelatinui.gui.UITimeControl.isPaused() ||
            Math.abs(io.github.currenj.gelatinui.gui.UITimeControl.getTimescale() - 1.0f) > 0.01f) {

            String status = io.github.currenj.gelatinui.gui.UITimeControl.getStatusString();
            int textWidth = this.font.width(status);
            int x = this.width - textWidth - 10;
            int y = this.height - 20;

            // Draw semi-transparent background
            guiGraphics.fill(x - 5, y - 2, x + textWidth + 5, y + 10, 0xC0000000);

            // Draw text in yellow if paused, white if just timescaled
            int color = io.github.currenj.gelatinui.gui.UITimeControl.isPaused() ? 0xFFFFFF00 : 0xFFFFFFFF;
            guiGraphics.drawString(this.font, status, x, y, color, false);
        }
    }

    protected abstract void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    protected void updateComponentSizes(MinecraftRenderContext context) {
        // Default implementation: do nothing
        // Subclasses can override to update specific component sizes
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Notify global click listeners first
        notifyGlobalClickListeners(mouseX, mouseY, button);

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
        // Key '6' = GLFW_KEY_6 = 54
        // Key '5' = GLFW_KEY_5 = 53
        // Key '4' = GLFW_KEY_4 = 52
        // Key 'P' = GLFW_KEY_P = 80
        // Key 'N' = GLFW_KEY_N = 78
        // Key '[' = GLFW_KEY_LEFT_BRACKET = 91
        // Key ']' = GLFW_KEY_RIGHT_BRACKET = 93

        if (keyCode == 56) { // Key '8' - Toggle bounds debug
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugBounds();
            return true;
        } else if (keyCode == 57) { // Key '9' - Toggle grid debug
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugGrid();
            return true;
        } else if (keyCode == 48) { // Key '0' - Toggle padding debug
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugPadding();
            return true;
        } else if (keyCode == 55) { // Key '7' - Toggle culled elements debug
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugCulled();
            return true;
        }

        // Time control keys
        else if (keyCode == 80) { // Key 'P' - Toggle pause
            io.github.currenj.gelatinui.gui.UITimeControl.togglePause();
            return true;
        } else if (keyCode == 78) { // Key 'N' - Step forward (when paused)
            if (io.github.currenj.gelatinui.gui.UITimeControl.isPaused()) {
                io.github.currenj.gelatinui.gui.UITimeControl.step();
            }
            return true;
        } else if (keyCode == 91) { // Key '[' - Decrease timescale
            float currentScale = io.github.currenj.gelatinui.gui.UITimeControl.getTimescale();
            float newScale = Math.max(0.1f, currentScale - 0.1f);
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(newScale);
            return true;
        } else if (keyCode == 93) { // Key ']' - Increase timescale
            float currentScale = io.github.currenj.gelatinui.gui.UITimeControl.getTimescale();
            float newScale = Math.min(5.0f, currentScale + 0.1f);
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(newScale);
            return true;
        } else if (keyCode == 52) { // Key '4' - Reset timescale to 1.0
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(1.0f);
            return true;
        } else if (keyCode == 53) { // Key '5' - Set timescale to 0.5x (slow motion)
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(0.5f);
            return true;
        } else if (keyCode == 54) { // Key '6' - Set timescale to 2.0x (fast forward)
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(2.0f);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Add a global click listener to this screen.
     *
     * @param listener The listener to add.
     */
    public void addGlobalClickListener(GlobalClickListener listener) {
        this.globalClickListeners.add(listener);
    }

    /**
     * Remove a global click listener from this screen.
     *
     * @param listener The listener to remove.
     */
    public void removeGlobalClickListener(GlobalClickListener listener) {
        this.globalClickListeners.remove(listener);
    }

    /**
     * Notify all global click listeners of a click event.
     *
     * @param mouseX The mouse X position.
     * @param mouseY The mouse Y position.
     * @param button The mouse button.
     */
    protected void notifyGlobalClickListeners(double mouseX, double mouseY, int button) {
        for (GlobalClickListener listener : globalClickListeners) {
            listener.onGlobalClick(mouseX, mouseY, button);
        }
    }
}
