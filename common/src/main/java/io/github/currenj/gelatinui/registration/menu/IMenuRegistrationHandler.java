package io.github.currenj.gelatinui.registration.menu;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public interface IMenuRegistrationHandler {
    void register(String id, MenuType<?> menuType);
}
