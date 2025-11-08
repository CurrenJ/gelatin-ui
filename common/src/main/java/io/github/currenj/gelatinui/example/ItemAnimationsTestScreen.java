package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.effects.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Demo screen showcasing the new item animations with actual ItemRenderer components.
 * Demonstrates 3D rotation effects on items including coin spin, regular spin, and more.
 */
public class ItemAnimationsTestScreen extends GelatinUIScreen<GelatinMenu> {

    private ItemRenderer.ItemRendererImpl coinSpinItem;
    private ItemRenderer.ItemRendererImpl spinItem;
    private ItemRenderer.ItemRendererImpl jumpBounceItem;
    private ItemRenderer.ItemRendererImpl flipItem;
    private ItemRenderer.ItemRendererImpl fallBounceItem;
    private ItemRenderer.ItemRendererImpl pulseGlowItem;
    private Label statusLabel;

    public ItemAnimationsTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Item Animations Demo"));
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
                .spacing(15)
                .padding(30);
        mainContainer.fillWidth(true).scaleToHeight(this.uiScreen.getViewHeight());

        // Title
        Label title = UI.label(tempContext, "Item Animation Effects", 0xFFFFFFFF)
                .centered(true)
                .scale(1.5f);
        mainContainer.addChild(title);

        // Description
        Label description = UI.label(tempContext,
            "Click buttons to trigger animations on 3D item renderers",
            0xFFB4B4B4);
        mainContainer.addChild(description);

        // Item display section
        mainContainer.addChild(UI.label(tempContext, "Demo Items:", 0xFFC8C8FF));

        // Create item grid with 3 rows of 2 items each
        VBox itemsContainer = new VBox().spacing(20);

        // Row 1: Coin Spin and Regular Spin
        HBox row1 = new HBox().spacing(40).alignment(HBox.Alignment.CENTER);
        
        VBox coinSpinBox = createItemBox(tempContext, "Coin Spin", Items.GOLD_INGOT);
        coinSpinItem = (ItemRenderer.ItemRendererImpl) coinSpinBox.getChildren().get(1);
        row1.addChild(coinSpinBox);
        
        VBox spinBox = createItemBox(tempContext, "Spin", Items.DIAMOND);
        spinItem = (ItemRenderer.ItemRendererImpl) spinBox.getChildren().get(1);
        row1.addChild(spinBox);
        
        itemsContainer.addChild(row1);

        // Row 2: Jump Bounce and Flip
        HBox row2 = new HBox().spacing(40).alignment(HBox.Alignment.CENTER);
        
        VBox jumpBounceBox = createItemBox(tempContext, "Jump Bounce", Items.EMERALD);
        jumpBounceItem = (ItemRenderer.ItemRendererImpl) jumpBounceBox.getChildren().get(1);
        row2.addChild(jumpBounceBox);
        
        VBox flipBox = createItemBox(tempContext, "Flip", Items.IRON_INGOT);
        flipItem = (ItemRenderer.ItemRendererImpl) flipBox.getChildren().get(1);
        row2.addChild(flipBox);
        
        itemsContainer.addChild(row2);

        // Row 3: Fall Bounce and Pulse Glow
        HBox row3 = new HBox().spacing(40).alignment(HBox.Alignment.CENTER);
        
        VBox fallBounceBox = createItemBox(tempContext, "Fall Bounce", Items.NETHERITE_INGOT);
        fallBounceItem = (ItemRenderer.ItemRendererImpl) fallBounceBox.getChildren().get(1);
        row3.addChild(fallBounceBox);
        
        VBox pulseGlowBox = createItemBox(tempContext, "Pulse Glow", Items.NETHER_STAR);
        pulseGlowItem = (ItemRenderer.ItemRendererImpl) pulseGlowBox.getChildren().get(1);
        row3.addChild(pulseGlowBox);
        
        itemsContainer.addChild(row3);

        mainContainer.addChild(itemsContainer);

        // Control buttons section
        mainContainer.addChild(UI.label(tempContext, "Animation Controls:", 0xFFC8C8FF));

        // Row 1: Coin Spin, Spin, Jump Bounce
        HBox buttonRow1 = new HBox().spacing(10).alignment(HBox.Alignment.CENTER);

        SpriteButton coinSpinButton = new SpriteButton(110, 28, 0xFFFFD700)
                .text("Coin Spin", 0xFF000000)
                .onClick(e -> {
                    coinSpinItem.addCoinSpinEffect();
                    updateStatus("Coin spin effect triggered (3D Y-axis rotation)!");
                });

        SpriteButton spinButton = new SpriteButton(110, 28, 0xFF1E90FF)
                .text("Spin", 0xFFFFFFFF)
                .onClick(e -> {
                    spinItem.addSpinEffect();
                    updateStatus("Spin effect triggered (3D showcase rotation)!");
                });

        SpriteButton jumpBounceButton = new SpriteButton(110, 28, 0xFF32CD32)
                .text("Jump Bounce", 0xFFFFFFFF)
                .onClick(e -> {
                    jumpBounceItem.addJumpBounceEffect();
                    updateStatus("Jump bounce effect triggered!");
                });

        buttonRow1.addChild(coinSpinButton);
        buttonRow1.addChild(spinButton);
        buttonRow1.addChild(jumpBounceButton);
        mainContainer.addChild(buttonRow1);

        // Row 2: Flip, Fall Bounce, Pulse Glow
        HBox buttonRow2 = new HBox().spacing(10).alignment(HBox.Alignment.CENTER);

        SpriteButton flipButton = new SpriteButton(110, 28, 0xFF9370DB)
                .text("Flip", 0xFFFFFFFF)
                .onClick(e -> {
                    flipItem.addFlipEffect();
                    updateStatus("Flip effect triggered!");
                });

        SpriteButton fallBounceButton = new SpriteButton(110, 28, 0xFFFF6347)
                .text("Fall Bounce", 0xFFFFFFFF)
                .onClick(e -> {
                    fallBounceItem.addFallBounceEffect();
                    updateStatus("Fall bounce effect triggered!");
                });

        SpriteButton pulseGlowButton = new SpriteButton(110, 28, 0xFFFF1493)
                .text("Pulse Glow", 0xFFFFFFFF)
                .onClick(e -> {
                    pulseGlowItem.addPulseGlowEffect();
                    updateStatus("Pulse glow effect triggered (continuous)!");
                });

        buttonRow2.addChild(flipButton);
        buttonRow2.addChild(fallBounceButton);
        buttonRow2.addChild(pulseGlowButton);
        mainContainer.addChild(buttonRow2);

        // Row 3: Utility buttons
        HBox buttonRow3 = new HBox().spacing(10).alignment(HBox.Alignment.CENTER);

        SpriteButton triggerAllButton = new SpriteButton(110, 28, 0xFF4682B4)
                .text("Trigger All", 0xFFFFFFFF)
                .onClick(e -> {
                    coinSpinItem.addCoinSpinEffect();
                    spinItem.addSpinEffect();
                    jumpBounceItem.addJumpBounceEffect();
                    flipItem.addFlipEffect();
                    fallBounceItem.addFallBounceEffect();
                    pulseGlowItem.addPulseGlowEffect();
                    updateStatus("All animations triggered!");
                });

        SpriteButton clearAllButton = new SpriteButton(110, 28, 0xFF646464)
                .text("Clear All", 0xFFFFFFFF)
                .onClick(e -> {
                    coinSpinItem.clearEffects();
                    spinItem.clearEffects();
                    jumpBounceItem.clearEffects();
                    flipItem.clearEffects();
                    fallBounceItem.clearEffects();
                    pulseGlowItem.clearEffects();
                    updateStatus("All effects cleared");
                });

        SpriteButton advancedButton = new SpriteButton(110, 28, 0xFFB44682)
                .text("Advanced Demo", 0xFFFFFFFF)
                .onClick(e -> {
                    // Coin spin with custom parameters
                    CoinSpinEffect customCoin = new CoinSpinEffect("custom-coin", 0, 5f);
                    customCoin.setRotationSpeed(1080f); // 3 full rotations
                    customCoin.setGlowPulse(0.3f);
                    coinSpinItem.addEffect(customCoin);

                    // Continuous slow spin
                    SpinEffect continuousSpin = SpinEffect.continuous(0.5f);
                    spinItem.addEffect(continuousSpin);

                    updateStatus("Advanced effects: fast coin spin + continuous rotation!");
                });

        buttonRow3.addChild(triggerAllButton);
        buttonRow3.addChild(clearAllButton);
        buttonRow3.addChild(advancedButton);
        mainContainer.addChild(buttonRow3);

        // Status display
        VBox statusBox = new VBox().spacing(5);
        statusBox.addChild(UI.label(tempContext, "Status:", 0xFFC8C8FF));
        statusLabel = UI.label(tempContext, "Click buttons to trigger item animations!", 0xFF969696);
        statusBox.addChild(statusLabel);
        mainContainer.addChild(statusBox);

        // Info section
        mainContainer.addChild(UI.label(tempContext, "Notes:", 0xFFC8C8FF));
        mainContainer.addChild(UI.label(tempContext,
                "• Coin Spin and Spin use true 3D rotation (Y-axis) for items\n" +
                "• Items are rendered as 3D models with realistic rotation\n" +
                "• Pulse Glow is continuous - click Clear All to stop\n" +
                "• Try Advanced Demo for customized effects",
                0xFFB4B4B4));

        uiScreen.setRoot(mainContainer);
    }

    /**
     * Helper to create a labeled item display box.
     */
    private VBox createItemBox(MinecraftRenderContext context, String label, net.minecraft.world.item.Item item) {
        VBox box = new VBox().spacing(8).alignment(VBox.Alignment.CENTER);
        
        // Label
        Label itemLabel = UI.label(context, label, 0xFFFFFFFF).centered(true);
        box.addChild(itemLabel);
        
        // Item renderer (larger size for better visibility)
        ItemRenderer.ItemRendererImpl itemRenderer = UI.itemRenderer(48, 48, new ItemStack(item, 1));
        itemRenderer.itemScale(2.0f);
        box.addChild(itemRenderer);
        
        return box;
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.text(message).color(0xFF00FFAA);
        }
    }

    @Override
    protected void renderContent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Dark background
    }

    @Override
    public void containerTick() {
        super.containerTick();
    }
}
