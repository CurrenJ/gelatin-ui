package io.github.currenj.gelatinui.tooltip;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Objects;

public class ItemStacksInfo {
    // Lazy codecs: ItemStack.CODEC resolves BuiltInRegistries at class-init/use time. HoverEvent's
    // static Action codec is built during registry bootstrap, so eager codecs here would trigger
    // "Not bootstrapped" on Forge. Deferring until first use keeps all loaders safe.
    public static final Codec<ItemStacksInfo> FULL_CODEC = Codec.lazyInitialized(
            () -> Codec.list(ItemStack.CODEC).xmap(ItemStacksInfo::new, ItemStacksInfo::getItemStacks));
    public static final Codec<ItemStacksInfo> SIMPLE_CODEC = Codec.lazyInitialized(
            () -> Codec.list(ItemStack.SIMPLE_ITEM_CODEC).xmap(ItemStacksInfo::new, ItemStacksInfo::getItemStacks));
    public static final Codec<ItemStacksInfo> CODEC = Codec.lazyInitialized(
            () -> Codec.withAlternative(FULL_CODEC, SIMPLE_CODEC));
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

    public static DataResult<ItemStacksInfo> legacyCreate(Component arg, RegistryOps<?> arg2) {
        // TODO: Implement legacy parsing if needed
        return DataResult.error(() -> "Legacy create not implemented for ItemStacksInfo");
    }
}
