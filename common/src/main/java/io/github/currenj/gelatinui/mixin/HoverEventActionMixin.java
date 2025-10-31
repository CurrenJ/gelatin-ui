package io.github.currenj.gelatinui.mixin;

import com.mojang.serialization.Codec;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HoverEvent.Action.class)
public class HoverEventActionMixin {
    @Shadow public static Codec<HoverEvent.Action<?>> UNSAFE_CODEC;

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void wrapFromValues(CallbackInfo ci) {
        // Add alternative to codec, so when MC's codec fails to decode our custom action, fall back to our own codec
        HoverEvent.Action.UNSAFE_CODEC = Codec.withAlternative(UNSAFE_CODEC,
                StringRepresentable.fromValues(HoverEventActionMixin::gelatinui$getAdditionalActions));
        HoverEvent.Action.CODEC = UNSAFE_CODEC.validate(HoverEvent.Action::filterForSerialization);
    }

    @Unique
    private static HoverEvent.Action<?>[] gelatinui$getAdditionalActions() {
        return new HoverEvent.Action<?>[]{ItemStacksTooltip.SHOW_ITEM_STACKS};
    }
}
