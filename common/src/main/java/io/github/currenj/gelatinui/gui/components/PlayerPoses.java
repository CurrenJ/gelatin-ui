package io.github.currenj.gelatinui.gui.components;

import net.minecraft.client.model.player.PlayerModel;

import java.util.function.Consumer;

/**
 * Ready-made per-limb poses for {@link PlayerModelRenderer}. Angles are starting points, not
 * final art — ModelPart rotations are notoriously fiddly to eyeball from source, so expect to
 * nudge these in-game (e.g. via a debug screen exposing the values live) once you can see them
 * rendered rather than trusting the numbers alone.
 * <p>
 * Every preset here only needs to set parts away from neutral — {@link PlayerModelRenderer}
 * resets all root parts to zero rotation before applying the pose.
 */
public final class PlayerPoses {
    private PlayerPoses() {}

    /** Default standing pose — arms at sides, no pose applied. */
    public static final Consumer<PlayerModel> IDLE = model -> {};

    /** Right arm raised overhead, fist-pump style — for a 1st-place podium slot. */
    public static final Consumer<PlayerModel> VICTORY = model -> {
        model.rightArm.setRotation(-2.9f, 0.0f, 0.2f);
        model.head.setRotation(-0.1f, 0.0f, 0.0f);
    };

    /** Arms crossed over the chest — for a relaxed 2nd/3rd-place podium slot. */
    public static final Consumer<PlayerModel> ARMS_CROSSED = model -> {
        model.rightArm.setRotation(-1.4f, -0.3f, -1.1f);
        model.leftArm.setRotation(-1.4f, 0.3f, 1.1f);
    };

    /** Waving with the right hand — friendly alternative to {@link #VICTORY}. */
    public static final Consumer<PlayerModel> WAVE = model -> {
        model.rightArm.setRotation(-2.2f, 0.0f, -0.9f);
        model.head.setRotation(0.0f, -0.2f, 0.0f);
    };
}
