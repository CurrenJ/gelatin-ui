package io.github.currenj.gelatinui.mixin;

import net.minecraft.network.chat.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;

/**
 * In MC 26.1, HoverEvent.Action is a final enum with immutable CODEC/UNSAFE_CODEC fields.
 * Custom action injection via codec wrapping is no longer possible.
 * This mixin is kept as a stub; the ItemStacksTooltip hover event feature is disabled until
 * an alternative injection mechanism is found.
 */
@Mixin(value = HoverEvent.Action.class)
public class HoverEventActionMixin {
}
