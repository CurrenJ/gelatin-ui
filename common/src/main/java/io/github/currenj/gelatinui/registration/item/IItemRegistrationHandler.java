package io.github.currenj.gelatinui.registration.item;

import net.minecraft.world.item.Item;

/**
 * Platform-agnostic interface for registering items.
 * Used for registering custom items that are used solely for rendering purposes.
 */
public interface IItemRegistrationHandler {
    /**
     * Register an item with the given ID.
     * @param id The item ID (without namespace)
     * @param item The item to register
     */
    void register(String id, Item item);
}
