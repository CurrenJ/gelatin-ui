package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.UIElement;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.effects.*;
import io.github.currenj.gelatinui.gui.animation.FloatKeyframeAnimation;
import io.github.currenj.gelatinui.gui.animation.Keyframe;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo screen showcasing the effects system with interactive examples.
 */
public class EffectsTestScreen extends GelatinUIScreen<GelatinMenu> {

    // Demo elements
    private SpriteButton clickBounceButton;
    private SpriteButton shakeButton;
    private SpriteButton breatheToggleButton;
    private SpriteButton wanderToggleButton;
    private SpriteButton driftButton;
    private SpriteButton clearAllButton;
    private Label statusLabel;
    private UIElement<?> demoPanel;

    // Effect state
    private boolean breatheEnabled = false;
    private boolean wanderEnabled = false;

    public EffectsTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Effects System Demo"));
    }

    @Override
    protected void buildUI() {
        MinecraftRenderContext tempContext = new MinecraftRenderContext(
                new GuiGraphics(this.minecraft, this.minecraft.renderBuffers().bufferSource()),
                this.font
        );

        // Main container
        VBox mainContainer = new VBox()
                .alignment(VBox.Alignment.CENTER)
                .spacing(20)
                .padding(30);
        mainContainer.fillWidth(true).scaleToHeight(this.uiScreen.getViewHeight());

        // Title
        Label title = UI.label(tempContext, "Effects System Demo", 0xFFFFFFFF)
                .centered(true)
                .scale(1.5f);
        mainContainer.addChild(title);

        // Description
        Label description = UI.label(tempContext,
            "Click buttons to see different effects in action. Effects can stack and animate!",
            0xFFB4B4B4);
        mainContainer.addChild(description);
        mainContainer.addChild(description);

        // Demo panel that will show effects
        demoPanel = UI.spriteRectangle(200, 120, UI.rgb(50, 0, 30)).text("Text!", UI.rgb(220, 220, 50));

        // Add some initial effects to the demo panel
        demoPanel.addBreatheEffect();
        demoPanel.addWanderEffect();

        mainContainer.addChild(UI.label(tempContext, "Demo Panel (has breathe + wander effects):", 0xFFC8C8FF));
        mainContainer.addChild(demoPanel);

        // Control buttons section
        mainContainer.addChild(UI.label(tempContext, "Effect Controls:", 0xFFC8C8FF));

        HBox buttonRow1 = new HBox().spacing(10);
        HBox buttonRow2 = new HBox().spacing(10);
        HBox buttonRow3 = new HBox().spacing(10);

        // Row 1: Instant effects
        clickBounceButton = new SpriteButton(120, 30, 0xFF4682B4)
                .text("Click Bounce", 0xFFFFFFFF)
                .onClick(e -> {
                    demoPanel.addClickBounceEffect();
                    updateStatus("Added click bounce effect!");
                });

        shakeButton = new SpriteButton(120, 30, 0xFFB44646)
                .text("Shake", 0xFFFFFFFF)
                .onClick(e -> {
                    ShakeEffect shake = new ShakeEffect("shake", 0, 0.5f);
                    shake.setAmplitude(6.0f);
                    shake.setFrequency(25.0f);
                    demoPanel.addEffectExclusive(shake);
                    updateStatus("Added shake effect!");
                });

        driftButton = new SpriteButton(120, 30, 0xFF46B446)
                .text("Drift Right", 0xFFFFFFFF)
                .onClick(e -> {
                    DriftEffect drift = new DriftEffect("drift", 0, 1.5f);
                    drift.setVelocity(new Vector2f(30, 0)); // Move right
                    demoPanel.addEffectExclusive(drift);
                    updateStatus("Added drift effect!");
                });

        buttonRow1.addChild(clickBounceButton);
        buttonRow1.addChild(shakeButton);
        buttonRow1.addChild(driftButton);

        // Row 2: Toggle effects
        breatheToggleButton = new SpriteButton(120, 30, 0xFFB48246)
                .text("Toggle Breathe", 0xFFFFFFFF)
                .onClick(e -> {
                    if (breatheEnabled) {
                        demoPanel.cancelEffectChannel("breathe");
                        breatheEnabled = false;
                        updateStatus("Disabled breathe effect");
                    } else {
                        BreatheEffect breathe = new BreatheEffect("breathe", 0);
                        breathe.setAmplitude(0.04f);
                        breathe.setFrequency(1.0f);
                        demoPanel.addEffectExclusive(breathe);
                        breatheEnabled = true;
                        updateStatus("Enabled breathe effect");
                    }
                });

        wanderToggleButton = new SpriteButton(120, 30, 0xFF8246B4)
                .text("Toggle Wander", 0xFFFFFFFF)
                .onClick(e -> {
                    if (wanderEnabled) {
                        demoPanel.cancelEffectChannel("wander");
                        wanderEnabled = false;
                        updateStatus("Disabled wander effect");
                    } else {
                        WanderEffect wander = new WanderEffect("wander", 0);
                        wander.setRadius(12.0f);
                        wander.setSpeed(0.6f);
                        demoPanel.addEffectExclusive(wander);
                        wanderEnabled = true;
                        updateStatus("Enabled wander effect");
                    }
                });

        clearAllButton = new SpriteButton(120, 30, 0xFF646464)
                .text("Clear All", 0xFFFFFFFF)
                .onClick(e -> {
                    demoPanel.clearEffects();
                    breatheEnabled = false;
                    wanderEnabled = false;
                    updateStatus("Cleared all effects");
                });

        buttonRow2.addChild(breatheToggleButton);
        buttonRow2.addChild(wanderToggleButton);
        buttonRow2.addChild(clearAllButton);

        // Row 3: Animated effects
        SpriteButton animateAmplitudeButton = new SpriteButton(120, 30, 0xFFB44682)
                .text("Animate Breathe", 0xFFFFFFFF)
                .onClick(e -> {
                    // First add a breathe effect if not present
                    BreatheEffect breathe = new BreatheEffect("breathe-anim", 0);
                    breathe.setAmplitude(0.0f); // Start at 0
                    demoPanel.addEffectExclusive(breathe);

                    // Animate amplitude from 0 to 0.08 over 2 seconds
                    List<Keyframe> keyframes = new ArrayList<>();
                    keyframes.add(new Keyframe(0.0f, 0.0f));
                    keyframes.add(new Keyframe(1.0f, 0.08f));
                    keyframes.add(new Keyframe(2.0f, 0.0f));

                    FloatKeyframeAnimation ampAnim = EffectAnimationBinder.animateBreatheAmplitude(
                            "breathe-amp-anim",
                            keyframes,
                            demoPanel,
                            breathe
                    );

                    demoPanel.playAnimation(ampAnim);
                    updateStatus("Animating breathe amplitude!");
                });

        SpriteButton stackEffectsButton = new SpriteButton(120, 30, 0xFF46B482)
                .text("Stack Effects", 0xFFFFFFFF)
                .onClick(e -> {
                    // Add multiple effects at once
                    BreatheEffect breathe = new BreatheEffect("stack-breathe", 0);
                    breathe.setAmplitude(0.03f);
                    demoPanel.addEffect(breathe);

                    WanderEffect wander = new WanderEffect("stack-wander", 1);
                    wander.setRadius(8.0f);
                    demoPanel.addEffect(wander);

                    ShakeEffect shake = new ShakeEffect("stack-shake", 2, 0.8f);
                    shake.setAmplitude(3.0f);
                    demoPanel.addEffect(shake);

                    updateStatus("Stacked 3 effects (breathe + wander + shake)!");
                });

        SpriteButton priorityDemoButton = new SpriteButton(120, 30, 0xFFB4B446)
                .text("Priority Demo", 0xFFFFFFFF)
                .onClick(e -> {
                    // Demonstrate priority - higher priority effects override lower ones
                    BreatheEffect lowPriority = new BreatheEffect("priority-low", 0);
                    lowPriority.setAmplitude(0.02f);
                    demoPanel.addEffect(lowPriority);

                    BreatheEffect highPriority = new BreatheEffect("priority-high", 10);
                    highPriority.setAmplitude(0.06f); // This should dominate
                    demoPanel.addEffect(highPriority);

                    updateStatus("Added effects with different priorities!");
                });

        buttonRow3.addChild(animateAmplitudeButton);
        buttonRow3.addChild(stackEffectsButton);
        buttonRow3.addChild(priorityDemoButton);

        mainContainer.addChild(buttonRow1);
        mainContainer.addChild(buttonRow2);
        mainContainer.addChild(buttonRow3);

        // Status display
        statusLabel = UI.label(tempContext, "Click buttons to test effects!", 0xFF969696);
        VBox statusBox = new VBox().spacing(5);
        statusBox.addChild(UI.label(tempContext, "Status:", 0xFFC8C8FF));
        statusBox.addChild(statusLabel);
        mainContainer.addChild(statusBox);

        // Info section
        mainContainer.addChild(UI.label(tempContext, "Info:", UI.rgb(1, 1, 1)));
        mainContainer.addChild(UI.label(tempContext,
                "• Effects stack and combine automatically\n" +
                        "• Channel exclusivity prevents conflicts\n" +
                        "• Higher priority effects override lower ones\n" +
                        "• Effects animate smoothly over time",
                0xFFB4B4B4));

        uiScreen.setRoot(mainContainer);
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Dark background
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.text(message).color(0xFF00FFAA);
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();

        // Update effect counters for demo
        if (demoPanel != null) {
            int effectCount = demoPanel.getEffects().size();
            if (effectCount > 0) {
                // Could update status to show active effect count
                // statusLabel.text("Active effects: " + effectCount).color(0xFF00FFAA);
            }
        }
    }
}
