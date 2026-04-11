package io.github.currenj.gelatinui.tooltip;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Objects;

public class ItemStacksInfo {
    public static final Codec<ItemStacksInfo> CODEC = Codec.list(ItemStack.CODEC).xmap(ItemStacksInfo::new, ItemStacksInfo::getItemStacks);
    private final List<ItemStack> itemStacks;

    public ItemStacksInfo(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks.stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    public ItemStacksInfo(ItemStack... itemStacks) {
        this(List.of(itemStacks));
    }

    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ItemStacksInfo that = (ItemStacksInfo) object;
        return Objects.equals(itemStacks, that.itemStacks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStacks);
    }

}
