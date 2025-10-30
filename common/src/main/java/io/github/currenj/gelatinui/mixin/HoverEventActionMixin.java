package io.github.currenj.gelatinui.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.tooltip.ItemStacksInfo;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@Mixin(HoverEvent.Action.class)
public class HoverEventActionMixin {
    @Unique
    private static final HoverEvent.Action<ItemStacksInfo> gelatinui$SHOW_ITEM_STACKS = 
        new HoverEvent.Action<>("show_item_stacks", true, ItemStacksInfo.CODEC, ItemStacksInfo::legacyCreate);

    @WrapOperation(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/StringRepresentable;fromValues(Ljava/util/function/Supplier;)Lcom/mojang/serialization/Codec;"
            )
    )
    private static <T extends StringRepresentable> Codec<T> wrapFromValues(Supplier<T[]> supplier, Operation<Codec<T>> original) {
        // Type should be HoverEvent.Action<?>. Fail gracefully if not.

        if (!HoverEvent.Action.class.isAssignableFrom(supplier.get().getClass().getComponentType())) {
            GelatinUi.LOGGER.error("Unexpected type in HoverEvent.Action.fromValues mixin: {}", supplier.get().getClass().getComponentType());
            return original.call(supplier);
        }

        Supplier<HoverEvent.Action<?>[]> modifiedSupplier = () -> {
            HoverEvent.Action<?>[] originalActions = (HoverEvent.Action<?>[]) supplier.get();
            HoverEvent.Action<?>[] additionalActions = gelatinui$getAdditionalActions();
            HoverEvent.Action<?>[] modifiedActions = new HoverEvent.Action<?>[originalActions.length + additionalActions.length];

            System.arraycopy(originalActions, 0, modifiedActions, 0, originalActions.length);
            System.arraycopy(additionalActions, 0, modifiedActions, originalActions.length, additionalActions.length);

            return modifiedActions;
        };

        return original.call(modifiedSupplier);
    }

    @Unique
    private static HoverEvent.Action<?>[] gelatinui$getAdditionalActions() {
        return new HoverEvent.Action<?>[]{gelatinui$SHOW_ITEM_STACKS};
    }
}
