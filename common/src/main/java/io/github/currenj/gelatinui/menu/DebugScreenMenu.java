package io.github.currenj.gelatinui.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/**
 * Server-safe menu for debug screens.
 * This menu doesn't contain any inventory logic, it's just used to trigger
 * the client-side screen opening via Minecraft's menu/screen system.
 */
public class DebugScreenMenu extends AbstractContainerMenu {
    private final String screenId;

    public DebugScreenMenu(MenuType<?> menuType, int containerId, String screenId) {
        super(menuType, containerId);
        this.screenId = screenId;
    }

    public String getScreenId() {
        return screenId;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
