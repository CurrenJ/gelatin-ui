package io.github.currenj.gelatinui.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Skin resolution shared by {@link PlayerModelRenderer} and {@link PlayerAvatarRenderer}: the same
 * pipeline a player-head item uses on 1.21.1. An unresolved profile (name only, or UUID only) is
 * completed through {@link ResolvableProfile#resolve()}, then its textures load through
 * {@link SkinManager#lookupInsecure}. Until both land, the lookup returns the default Steve/Alex
 * skin for the profile, exactly like a player head does while its texture is loading.
 */
final class PlayerSkinLookup {
    private PlayerSkinLookup() {}

    static Supplier<PlayerSkin> create(ResolvableProfile profile) {
        Minecraft minecraft = Minecraft.getInstance();
        SkinManager skinManager = minecraft.getSkinManager();
        if (profile.isResolved()) {
            return skinManager.lookupInsecure(profile.gameProfile());
        }

        PlayerSkin fallback = DefaultPlayerSkin.get(profile.gameProfile());
        AtomicReference<Supplier<PlayerSkin>> current = new AtomicReference<>(() -> fallback);
        profile.resolve().thenAcceptAsync(resolved -> current.set(skinManager.lookupInsecure(resolved.gameProfile())), minecraft);
        return () -> current.get().get();
    }
}
