package io.github.currenj.gelatinui.tooltip;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;

public class ItemStacksInfo {
    private static final Logger LOGGER = LogUtils.getLogger();
    // Bridges to HoverEvent.Action's Gson-based (de)serializer functions via JsonOps —
    // 1.20.1 has no Codec extension point on HoverEvent.Action itself, see
    // HoverEventActionMixin. 1.20.1's ItemStack has only one CODEC (no data-component
    // SIMPLE_ITEM_CODEC split, that's 1.20.5+).
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

    public static JsonElement serializeArg(ItemStacksInfo info) {
        return CODEC.encodeStart(JsonOps.INSTANCE, info)
                .resultOrPartial(LOGGER::error)
                .orElseGet(JsonObject::new);
    }

    public static ItemStacksInfo deserializeArg(JsonElement json) {
        return CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(LOGGER::error)
                .orElse(null);
    }

    // Legacy pre-1.16 "value" chat-component transport isn't supported for this action.
    public static ItemStacksInfo legacyCreate(Component arg) {
        return null;
    }
}
