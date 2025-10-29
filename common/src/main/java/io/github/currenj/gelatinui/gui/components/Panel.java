package io.github.currenj.gelatinui.gui.components;

/**
 * A generic panel container with optional background.
 * Can be used as a simple grouping container or with visual styling.
 * Supports solid color backgrounds and sprite backgrounds (with stretch, repeat, or slice modes).
 */
public class Panel extends PanelBase<Panel> {
    public Panel() {
    }

    @Override
    protected Panel self() {
        return this;
    }
}
