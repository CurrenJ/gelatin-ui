package io.github.currenj.gelatinui.tooltip;

import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ItemStacksTooltip(List<ItemStack> items, boolean renderItemDecorations) implements TooltipComponent {
    /**
     * Gets the SHOW_ITEM_STACKS action. This is lazily initialized to avoid
     * issues with enum instantiation during class loading.
     * The action is created and added to the enum by HoverEventActionMixin.
     */
    public static HoverEvent.Action<ItemStacksInfo> getShowItemStacksAction() {
        // Find the action by name in the enum values
        for (HoverEvent.Action<?> action : HoverEvent.Action.values()) {
            if (action.getName().equals("show_item_stacks")) {
                @SuppressWarnings("unchecked")
                HoverEvent.Action<ItemStacksInfo> typedAction = (HoverEvent.Action<ItemStacksInfo>) action;
                return typedAction;
            }
        }
        throw new IllegalStateException("SHOW_ITEM_STACKS action not found in HoverEvent.Action values. Is HoverEventActionMixin applied?");
    }

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
