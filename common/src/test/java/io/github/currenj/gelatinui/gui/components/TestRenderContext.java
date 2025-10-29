package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.IRenderContext;

import java.util.ArrayList;
import java.util.List;

public class TestRenderContext implements IRenderContext {
    public static class FillCall {
        public final int x1, y1, x2, y2, color;
        public FillCall(int x1, int y1, int x2, int y2, int color) { this.x1=x1; this.y1=y1; this.x2=x2; this.y2=y2; this.color=color; }
    }

    public static class DrawCall {
        public final String text; public final int x; public final int y; public final int color;
        public DrawCall(String text, int x, int y, int color) { this.text=text; this.x=x; this.y=y; this.color=color; }
    }

    public final List<FillCall> fills = new ArrayList<>();
    public final List<DrawCall> draws = new ArrayList<>();
    public final List<DrawCall> centered = new ArrayList<>();

    // Simple metrics: width = 6 * chars, height = 10
    @Override
    public void fill(int x1, int y1, int x2, int y2, int color) {
        fills.add(new FillCall(x1,y1,x2,y2,color));
    }

    @Override
    public void drawString(String text, int x, int y, int color) {
        draws.add(new DrawCall(text, x, y, color));
    }

    @Override
    public void drawCenteredString(String text, int x, int y, int color) {
        centered.add(new DrawCall(text, x, y, color));
    }

    @Override
    public int getStringWidth(String text) {
        return (text == null) ? 0 : text.length() * 6;
    }

    @Override
    public int getFontHeight() {
        return 10;
    }

    @Override
    public void pushScissor(int x, int y, int width, int height) { }
    @Override
    public void popScissor() { }
    @Override
    public void enableBlend() { }
    @Override
    public void disableBlend() { }

    @Override
    public void drawTexture(net.minecraft.resources.ResourceLocation texture, int x, int y, int width, int height) {
        // No-op for tests; we could record calls if needed in the future
    }

    @Override
    public void drawTexture(net.minecraft.resources.ResourceLocation texture, int x, int y, int width, int height, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        // No-op for UV-aware draw calls in tests; record or assert in future if necessary
    }
}
