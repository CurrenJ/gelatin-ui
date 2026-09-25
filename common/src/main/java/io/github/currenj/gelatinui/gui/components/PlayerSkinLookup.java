package io.github.currenj.gelatinui.gui.components;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Skin resolution shared by {@link PlayerModelRenderer} and {@link PlayerAvatarRenderer}.
 * <p>
 * 1.20.1 has no {@code ResolvableProfile} (that unified name/UUID-to-full-profile async
 * resolution is a later-version addition, see {@code SkullBlockEntity#updateGameprofile}'s
 * server-side equivalent for what it replaced) and no client-accessible name→UUID lookup either
 * — {@link Minecraft} exposes only a {@code MinecraftSessionService} (UUID+properties →
 * textures), not a {@code GameProfileRepository}. So {@code profile} must already carry a UUID;
 * name-only resolution isn't supported on this version.
 * <p>
 * {@link SkinManager#registerSkins} already fills in missing profile properties (via the session
 * service, exactly like {@code PlayerInfo#registerTextures} does for other players' name tags) and
 * derives the slim/wide model from the skin texture's {@code "model"} metadata key, so it alone
 * covers what {@code ResolvableProfile#resolve()} + {@code SkinManager#lookupInsecure} did together
 * on 1.21.1. Until it lands, the lookup returns the default Steve/Alex skin for the profile's UUID,
 * exactly like a player head does while its texture is loading.
 */
final class PlayerSkinLookup {
    private PlayerSkinLookup() {}

    static Supplier<PlayerSkin> create(GameProfile profile) {
        Minecraft minecraft = Minecraft.getInstance();
        SkinManager skinManager = minecraft.getSkinManager();

        ResourceLocation fallbackTexture = DefaultPlayerSkin.getDefaultSkin(profile.getId());
        PlayerSkin.Model fallbackModel = "slim".equals(DefaultPlayerSkin.getSkinModelName(profile.getId()))
                ? PlayerSkin.Model.SLIM
                : PlayerSkin.Model.WIDE;
        AtomicReference<PlayerSkin> current = new AtomicReference<>(new PlayerSkin(fallbackTexture, fallbackModel));

        skinManager.registerSkins(profile, (type, location, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                PlayerSkin.Model model = "slim".equals(texture.getMetadata("model"))
                        ? PlayerSkin.Model.SLIM
                        : PlayerSkin.Model.WIDE;
                current.set(new PlayerSkin(location, model));
            }
        }, false);

        return current::get;
    }
}
