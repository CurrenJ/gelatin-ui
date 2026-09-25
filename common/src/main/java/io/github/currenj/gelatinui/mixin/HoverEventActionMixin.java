package io.github.currenj.gelatinui.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.currenj.gelatinui.tooltip.ItemStacksTooltip;
import net.minecraft.network.chat.HoverEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 1.20.1's HoverEvent.Action has no Codec extension point (it's Gson-based) and its
 * name -> action LOOKUP map is a private static final ImmutableMap built once in
 * {@code <clinit>}. Reassigning the shadowed field (legal via bytecode PUTSTATIC even
 * though it's declared final) is the only way to register a custom action, since there's
 * no public mutation point and no codec to hook the way 1.21.1's mixin does.
 */
@Mixin(value = HoverEvent.Action.class)
public class HoverEventActionMixin {
    @Shadow @Final @Mutable
    private static Map<String, HoverEvent.Action<?>> LOOKUP;

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void gelatinui$registerAdditionalActions(CallbackInfo ci) {
        Map<String, HoverEvent.Action<?>> combined = new HashMap<>(LOOKUP);
        for (HoverEvent.Action<?> action : gelatinui$getAdditionalActions()) {
            combined.put(action.getName(), action);
        }
        LOOKUP = ImmutableMap.copyOf(combined);
    }

    private static HoverEvent.Action<?>[] gelatinui$getAdditionalActions() {
        return new HoverEvent.Action<?>[]{ItemStacksTooltip.SHOW_ITEM_STACKS};
    }
}
