package io.github.currenj.gelatinui.tooltip;

import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ItemStacksTooltip(List<ItemStack> items, boolean renderItemDecorations) implements TooltipComponent {
    public static final HoverEvent.Action<ItemStacksInfo> SHOW_ITEM_STACKS = new HoverEvent.Action<>(
            "show_item_stacks", true, ItemStacksInfo.CODEC, ItemStacksInfo::legacyCreate
    );

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
