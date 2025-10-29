package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * MASSIVELY SIMPLIFIED ItemTabs for debugging: A VBox with a tab bar (HBox of SpriteButtons) and content area.
 * No custom components, no item rendering, no frames - just basic clickable buttons with content surfacing.
 */
public class ItemTabs extends VBox {
    private final List<Tab> tabs = new ArrayList<>();
    private int selectedIndex = -1;
    private Consumer<Integer> onSelectionChanged = null;
    private final HBox tabBar;
    private IUIElement currentContent = null;

    // Simple colors for debugging
    private static final int NORMAL_COLOR = 0xFF888888;
    private static final int SELECTED_COLOR = 0xFFFFFFFF;

    public ItemTabs() {
        this.alignment(Alignment.CENTER);
        tabBar = new HBox().spacing(4).alignment(HBox.Alignment.CENTER);
        this.addChild(tabBar);
    }

    // Remove all the complex configuration methods for simplification
    public ItemTabs hoverFrame(SpriteData frame) { return this; }
    public ItemTabs selectedFrame(SpriteData frame) { return this; }
    public ItemTabs hoverFrame(ResourceLocation texture) { return this; }
    public ItemTabs selectedFrame(ResourceLocation texture) { return this; }
    public ItemTabs sizes(float slotW, float slotH, float iconW, float iconH) { return this; }
    public ItemTabs tabSpacing(float spacing) { this.spacing(spacing); return this; }

    /**
     * Add a simple SpriteButton tab.
     */
    public UIElement<?> addTab(ItemStack icon, IUIElement content) {
        int index = tabs.size();

        // Create a simple SpriteButton (20x20 colored rectangle with number)
        UIElement<?> btn;
        if (icon == null || icon.isEmpty()) {
            btn = new SpriteButton(20, 20, NORMAL_COLOR)
                    .text(String.valueOf(index + 1), 0xFFFFFFFF);
        } else {
            btn = UI.itemButton(icon);
        }

        btn.onClick(e -> select(index));


        tabs.add(new Tab(btn, content));
        tabBar.addChild(btn);
        tabBar.recalculateLayout();

        // If this is the first tab, select it by default
        if (selectedIndex == -1) {
            select(0);
        }

        return btn;
    }

    /**
     * Select the tab at the given index.
     */
    public ItemTabs select(int index) {
        if (index < 0 || index >= tabs.size()) {
            return this;
        }
        if (index == selectedIndex) {
            return this;
        }

        selectedIndex = index;

        // Update visual selection state on buttons (change color)
        for (int i = 0; i < tabs.size(); i++) {
            IUIElement btn = tabs.get(i).button;
            if (btn instanceof SpriteButton spriteButton) {
                spriteButton.color(i == selectedIndex ? SELECTED_COLOR : NORMAL_COLOR);
            }
        }

        // Update the currently displayed content
        if (currentContent != null) {
            this.removeChild(currentContent);
        }
        currentContent = tabs.get(selectedIndex).content;
        this.addChild(currentContent);
        // Ensure the new content recalculates its layout in the new context
        currentContent.markDirty(DirtyFlag.LAYOUT);

        // Animate layout changes, don't snap
        this.animatePositions = true;
        markDirty(DirtyFlag.LAYOUT);

        // Notify listener
        if (onSelectionChanged != null) {
            onSelectionChanged.accept(selectedIndex);
        }

        return this;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public ItemTabs onSelectionChanged(Consumer<Integer> listener) {
        this.onSelectionChanged = listener;
        return this;
    }

    // Remove the custom onEvent since SpriteButton handles its own events
    // Remove the ItemTabButton class entirely

    // Simple holder for tab data
    private static class Tab {
        final IUIElement button;
        final IUIElement content;
        Tab(IUIElement button, IUIElement content) {
            this.button = button;
            this.content = content;
        }
    }
}
