package io.github.currenj.gelatinui.registration.item.neoforge;

import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.registration.item.IItemRegistrationHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * NeoForge implementation of item registration handler.
 * Uses NeoForge's DeferredRegister system to register items.
 */
public class ItemRegistrationHandlerNeoForge implements IItemRegistrationHandler {
    public static final ItemRegistrationHandlerNeoForge INSTANCE = new ItemRegistrationHandlerNeoForge();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, GelatinUi.MOD_ID);

    private ItemRegistrationHandlerNeoForge() {}

    @Override
    public void register(String id, Item item) {
        ITEMS.register(id, () -> item);
    }
}
