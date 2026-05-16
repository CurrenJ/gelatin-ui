package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.IUIElement;
import io.github.currenj.gelatinui.gui.UIScreen;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class GelatinUIScreen<M extends GelatinMenu> extends AbstractContainerScreen<M> {
    protected UIScreen uiScreen;
    private long lastFrameTimeNanos = 0L;
    private long fadeStartTime;
    private static final float FADE_DURATION = 0.2f;

    private final List<GlobalClickListener> globalClickListeners = new ArrayList<>();

    @FunctionalInterface
    public interface GlobalClickListener {
        void onGlobalClick(double mouseX, double mouseY, int button);
    }

    protected GelatinUIScreen(M menu, Inventory inv, Component title) {
        super(menu, inv, title);
        lastFrameTimeNanos = System.nanoTime();
        fadeStartTime = System.nanoTime();
    }

    @Override
    protected void init() {
        super.init();

        uiScreen = new UIScreen(this.width, this.height);
        uiScreen.setAutoCenterRoot(false);
        uiScreen.setAutoCenterThreshold(0.5f);

        buildUI();
    }

    protected abstract void buildUI();

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        extractContent(graphics, mouseX, mouseY, partialTick);

        long nowNanos = System.nanoTime();
        float deltaSeconds = Math.min(0.1f, Math.max(0f, (nowNanos - lastFrameTimeNanos) / 1_000_000_000f));
        lastFrameTimeNanos = nowNanos;

        if (uiScreen != null) {
            MinecraftRenderContext renderContext = new MinecraftRenderContext(graphics, this.font);

            uiScreen.update(deltaSeconds);
            updateComponentSizes(renderContext);
            uiScreen.update(0f);
            uiScreen.onMouseMove(mouseX, mouseY);
            uiScreen.update(0f);
            uiScreen.render(renderContext);

            renderTimeControlStatus(graphics);
        }
    }

    protected void renderTimeControlStatus(GuiGraphicsExtractor graphics) {
        if (io.github.currenj.gelatinui.gui.UITimeControl.isPaused() ||
            Math.abs(io.github.currenj.gelatinui.gui.UITimeControl.getTimescale() - 1.0f) > 0.01f) {

            String status = io.github.currenj.gelatinui.gui.UITimeControl.getStatusString();
            int textWidth = this.font.width(status);
            int x = this.width - textWidth - 10;
            int y = this.height - 20;

            graphics.fill(x - 5, y - 2, x + textWidth + 5, y + 10, 0xC0000000);

            int color = io.github.currenj.gelatinui.gui.UITimeControl.isPaused() ? 0xFFFFFF00 : 0xFFFFFFFF;
            graphics.text(this.font, status, x, y, color, false);
        }
    }

    protected void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}

    protected void updateComponentSizes(MinecraftRenderContext context) {}

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        notifyGlobalClickListeners(event.x(), event.y(), event.button());

        if (uiScreen != null) {
            uiScreen.onMouseMove((int) event.x(), (int) event.y());
            if (uiScreen.onMouseClick((int) event.x(), (int) event.y(), event.button())) {
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        if (uiScreen != null) {
            if (uiScreen.onMouseScroll((int) mouseX, (int) mouseY, (float) scrollY)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        if (uiScreen != null) {
            uiScreen.resize(width, height);
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        if (keyCode == 56) {
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugBounds();
            return true;
        } else if (keyCode == 57) {
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugGrid();
            return true;
        } else if (keyCode == 48) {
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugPadding();
            return true;
        } else if (keyCode == 55) {
            io.github.currenj.gelatinui.gui.UIElement.toggleDebugCulled();
            return true;
        } else if (keyCode == 80) {
            io.github.currenj.gelatinui.gui.UITimeControl.togglePause();
            return true;
        } else if (keyCode == 78) {
            if (io.github.currenj.gelatinui.gui.UITimeControl.isPaused()) {
                io.github.currenj.gelatinui.gui.UITimeControl.step();
            }
            return true;
        } else if (keyCode == 91) {
            float currentScale = io.github.currenj.gelatinui.gui.UITimeControl.getTimescale();
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(Math.max(0.1f, currentScale - 0.1f));
            return true;
        } else if (keyCode == 93) {
            float currentScale = io.github.currenj.gelatinui.gui.UITimeControl.getTimescale();
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(Math.min(5.0f, currentScale + 0.1f));
            return true;
        } else if (keyCode == 52) {
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(1.0f);
            return true;
        } else if (keyCode == 53) {
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(0.5f);
            return true;
        } else if (keyCode == 54) {
            io.github.currenj.gelatinui.gui.UITimeControl.setTimescale(2.0f);
            return true;
        }

        return super.keyPressed(event);
    }

    public void addGlobalClickListener(GlobalClickListener listener) {
        this.globalClickListeners.add(listener);
    }

    public void removeGlobalClickListener(GlobalClickListener listener) {
        this.globalClickListeners.remove(listener);
    }

    protected void notifyGlobalClickListeners(double mouseX, double mouseY, int button) {
        for (GlobalClickListener listener : globalClickListeners) {
            listener.onGlobalClick(mouseX, mouseY, button);
        }
    }

    public void setTooltip(IUIElement tooltip) {
        if (uiScreen != null) {
            uiScreen.setTooltip(tooltip);
        }
    }

    public IUIElement getTooltip() {
        return uiScreen != null ? uiScreen.getTooltip() : null;
    }

    public void setTooltipOffset(float offsetX, float offsetY) {
        if (uiScreen != null) {
            uiScreen.setTooltipOffset(offsetX, offsetY);
        }
    }

    public void clearTooltip() {
        if (uiScreen != null) {
            uiScreen.clearTooltip();
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int i, int j) {
        // No label rendering here; handled in extractContent
    }

    @Override
    public void extractTransparentBackground(GuiGraphicsExtractor graphics) {
        long now = System.nanoTime();
        float elapsed = (now - fadeStartTime) / 1_000_000_000f;
        if (elapsed >= FADE_DURATION) {
            graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            float progress = Math.min(1f, elapsed / FADE_DURATION);

            int color1 = -1072689136;
            int color2 = -804253680;
            int alpha1 = (color1 >> 24) & 0xFF;
            int alpha2 = (color2 >> 24) & 0xFF;
            int newAlpha1 = (int) (alpha1 * progress);
            int newAlpha2 = (int) (alpha2 * progress);
            int newColor1 = (newAlpha1 << 24) | (color1 & 0x00FFFFFF);
            int newColor2 = (newAlpha2 << 24) | (color2 & 0x00FFFFFF);
            graphics.fillGradient(0, 0, this.width, this.height, newColor1, newColor2);
        }
    }
}
