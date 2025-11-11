package io.github.currenj.gelatinui.item;

import io.github.currenj.gelatinui.registration.item.IItemRegistrationHandler;
import io.github.currenj.gelatinui.registration.item.SidedItemRegistrationHelper;
import net.minecraft.world.item.Item;

public class Items {
    public static void init() {
        IItemRegistrationHandler itemRegistrar = SidedItemRegistrationHelper.getItemRegistrationHandler();

        itemRegistrar.register("panel", new Item(new Item.Properties()));
    }
}
