package io.github.currenj.gelatinui.gui.components;

import net.minecraft.resources.ResourceLocation;

/**
 * 1.20.1 has no {@code net.minecraft.client.resources.PlayerSkin} record (that unification is a
 * later-version addition) — this is the local equivalent, populated from
 * {@link com.mojang.authlib.minecraft.MinecraftProfileTexture}'s {@code "model"} metadata key the
 * same way {@code PlayerInfo#getModelName} derives it.
 */
public record PlayerSkin(ResourceLocation texture, Model model) {
    public enum Model {
        SLIM,
        WIDE
    }
}
