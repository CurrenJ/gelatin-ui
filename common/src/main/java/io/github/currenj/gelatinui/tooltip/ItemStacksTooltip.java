package io.github.currenj.gelatinui.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ItemStacksTooltip(List<ItemStack> items, boolean renderItemDecorations) implements TooltipComponent {
    // Note: Custom HoverEvent.Action injection is not possible in MC 26.1 since Action is a final enum.

    public static ItemStacksTooltip of(List<List<ItemStack>> items, boolean renderItemDecorations) {
        List<ItemStack> displayStacks = new ArrayList<>();
        final int maxGridWidth = 9;
        for (List<ItemStack> itemStacks : items) {
            displayStacks.addAll(itemStacks);
            int xIndex = displayStacks.size() % maxGridWidth;
            if (xIndex != 0) {
                for (int i = 0; i < maxGridWidth - xIndex; i++) {
                    displayStacks.add(ItemStack.EMPTY);
                }
            }
        }
        return new ItemStacksTooltip(displayStacks, renderItemDecorations);
    }
}
