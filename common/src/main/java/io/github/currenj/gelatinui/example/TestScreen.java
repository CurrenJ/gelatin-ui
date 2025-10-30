package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.GelatinUi;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.UIEvent;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TestScreen extends GelatinUIScreen<GelatinMenu> {
    private float totalTime = 0;
    // Track the root and labels so the debug button can change alignment
    private VBox outerVBox;  // New outer container
    private VBox textVBox;   // Now the inner container with labels
    private Label titleLabel;
    private Label subtitleLabel;
    private Label infoLabel;
    private SpriteProgressBar progressBar;

    // The screen-level title as a Label component
    private Label screenTitleLabel;

    public TestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal(""));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void buildUI() {
        // Create a temporary render context to measure text
        MinecraftRenderContext tempContext = new MinecraftRenderContext(
            new GuiGraphics(this.minecraft, this.minecraft.renderBuffers().bufferSource()),
            this.font
        );

        // Create the screen-level title label and size it immediately
        screenTitleLabel = UI.label(tempContext, "New UI System", UI.rgb(255, 255, 255))
            .centered(true);
        // Snap scale so sizing reflects the final visual size
        screenTitleLabel.setTargetScale(1, false);

        // Build inner VBox with labels
        textVBox = UI.vbox()
            .spacing(10)
            .alignment(VBox.Alignment.CENTER);

        // Add labels demonstrating the new system
        titleLabel = UI.label(tempContext, "New GUI System", UI.rgb(255, 255, 255));
        subtitleLabel = UI.label(tempContext, "With Dirty Flags & Caching", UI.rgb(200, 200, 200));
        infoLabel = UI.label(tempContext, "This text is rendered efficiently!", UI.rgb(150, 255, 150));

        // Add children to inner VBox
        textVBox.addChild(titleLabel);
        textVBox.addChild(subtitleLabel);
        textVBox.addChild(infoLabel);

        // Create and add progress bar
        progressBar = UI.progressBar()
            .progress(0.5f)
            .skillLevel(30);
        textVBox.addChild(progressBar);

        // Force layout calculation to determine VBox size using temporary metrics
        textVBox.update(0);

        // Create outer VBox and add inner VBox as child
        outerVBox = UI.vbox()
            .spacing(15)
            .padding(30)
            .alignment(VBox.Alignment.CENTER)
            .fillWidth(true);
        outerVBox.addChild(screenTitleLabel);

        // Demonstrate ItemRenderer component
        Label itemLabel = UI.label(tempContext, "Item Renderers:", UI.rgb(200, 200, 255));
        outerVBox.addChild(itemLabel);

        // Create a horizontal box to display multiple items
        HBox itemBox = UI.hbox()
                .spacing(10)
                .alignment(HBox.Alignment.CENTER);

        // Add various items to demonstrate the renderer
        ItemRenderer diamondRenderer = UI.itemRenderer(new ItemStack(Items.DIAMOND, 1));
        diamondRenderer.setDebugName("Diamond Item");
        ItemRenderer goldRenderer = UI.itemRenderer(new ItemStack(Items.GOLD_INGOT, 16));
        goldRenderer.setDebugName("Gold Ingot Item");
        ItemRenderer appleRenderer = UI.itemRenderer(new ItemStack(Items.APPLE, 32));
        appleRenderer.setDebugName("Apple Item");
        ItemRenderer swordRenderer = UI.itemRenderer(new ItemStack(Items.DIAMOND_SWORD, 1));
        swordRenderer.setDebugName("Diamond Sword Item");

        // Add a scaled item
        ItemRenderer scaledRenderer = UI.itemRenderer(32, 32, new ItemStack(Items.EMERALD, 64))
                .itemScale(2.0f);
        scaledRenderer.setDebugName("Emerald Item (Scaled)");

        itemBox.addChild(diamondRenderer);
        itemBox.addChild(goldRenderer);
        itemBox.addChild(appleRenderer);
        itemBox.addChild(swordRenderer);
        itemBox.addChild(scaledRenderer);

        outerVBox.addChild(itemBox);

        // Add rotating item ring demo
        Label ringLabel = UI.label(tempContext, "Rotating Item Ring:", UI.rgb(200, 255, 200));
        ringLabel.setDebugName("Ring Label");
        outerVBox.addChild(ringLabel);

        RotatingItemRing ring = UI.rotatingItemRing()
                .radius(60)
                .defaultAngularSpeed(0.8f)
                .defaultItemScale(1.0f)
                .hoverItemScale(1.3f)
                .selectedItemScale(1.5f);
        ring.setDebugName("Rotating Item Ring");
        java.util.List<ItemStack> ringItems = new java.util.ArrayList<>();
        ringItems.add(new ItemStack(Items.DIAMOND));
        ringItems.add(new ItemStack(Items.GOLD_INGOT, 16));
        ringItems.add(new ItemStack(Items.APPLE, 5));
        ringItems.add(new ItemStack(Items.DIAMOND_SWORD));
        ringItems.add(new ItemStack(Items.EMERALD, 32));
        ringItems.add(new ItemStack(Items.IRON_INGOT, 12));
        ring.setItems(ringItems);
        // Give the ring some breathing room
        ring.setPosition(new org.joml.Vector2f(0, 0));
        ring.setSize(new org.joml.Vector2f(180, 180));
        outerVBox.addChild(ring);

        outerVBox.addChild(textVBox);

        // Create a button column using SpriteRectangle components instead of Minecraft widgets
        VBox buttonsVBox = UI.vbox()
            .spacing(2)
            .scaleToHeight(50)
            .alignment(VBox.Alignment.CENTER);

        // Progress control buttons
        SpriteButton increaseProgressBtn = UI.spriteButton(200, 10, UI.rgb(80, 200, 120))
                .text("Increase Progress", UI.rgb(255, 255, 255))
                .onClick(e -> {
                    float newProgress = Math.min(1.0f, progressBar.getProgress() + 0.1f);
                    progressBar.progress(newProgress);
                });

        SpriteButton decreaseProgressBtn = UI.spriteButton(200, 10, UI.rgb(200, 80, 80))
                .text("Decrease Progress", UI.rgb(255, 255, 255))
                .onClick(e -> {
                    float newProgress = Math.max(0.0f, progressBar.getProgress() - 0.1f);
                    progressBar.progress(newProgress);
                });

        SpriteButton increaseSkillBtn = UI.spriteButton(200, 10, UI.rgb(200, 180, 50))
                .text("Increase Skill (+15)", UI.rgb(255, 255, 255))
                .onClick(e -> {
                    int newSkill = Math.min(75, progressBar.getSkillLevel() + 15);
                    progressBar.skillLevel(newSkill);
                });

        SpriteButton decreaseSkillBtn = UI.spriteButton(200, 10, UI.rgb(150, 100, 50))
                .text("Decrease Skill (-15)", UI.rgb(255, 255, 255))
                .onClick(e -> {
                    int newSkill = Math.max(0, progressBar.getSkillLevel() - 15);
                    progressBar.skillLevel(newSkill);
                });

        // Close button
        ResourceLocation progressBarBgTex = ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_background.png");
        SpriteButton closeBtn = UI.spriteButton(128, 10, UI.rgb(180, 50, 50))
                .texture(SpriteData
                        .texture(progressBarBgTex)
                        .uv(9, 9)
                        .textureSize(128)
                )
                .text("Close", UI.rgb(255, 255, 255))
                .onClick(e -> onClose());

        // Cycle Inner button
        SpriteButton cycleInnerBtn = UI.spriteButton(200, 10, UI.rgb(80, 120, 200))
                .text("Cycle Inner", UI.rgb(255, 255, 255))
                .onClick(this::cycleAlignment);

        // Cycle Outer button
        SpriteButton cycleOuterBtn = UI.spriteButton(200, 10, UI.rgb(80, 160, 120))
                .text("Cycle Outer", UI.rgb(255, 255, 255))
                .onClick(this::cycleOuterAlignment);

        buttonsVBox.addChild(increaseProgressBtn);
        buttonsVBox.addChild(decreaseProgressBtn);
        buttonsVBox.addChild(increaseSkillBtn);
        buttonsVBox.addChild(decreaseSkillBtn);
        buttonsVBox.addChild(closeBtn);
        buttonsVBox.addChild(cycleInnerBtn);
        buttonsVBox.addChild(cycleOuterBtn);

        // Add buttons column to outer VBox
        outerVBox.addChild(buttonsVBox);
//        outerVBox.maxHeight((float) uiScreen.getViewport().getHeight())
//                .scaleToFit(true);

        // Add hover effects to labels
        addHoverEffects(titleLabel);
        addHoverEffects(subtitleLabel);
        addHoverEffects(infoLabel);

        // Set screen root on UIScreen (auto-centering will handle positioning)
        uiScreen.setRoot(outerVBox);

        // Note: Traditional Minecraft widgets were replaced with SpriteRectangle-based UI elements.
    }

    private void cycleAlignment(UIEvent e) {
        if (textVBox == null) return;
        VBox.Alignment current = textVBox.getAlignment();
        VBox.Alignment next = switch (current) {
            case LEFT -> VBox.Alignment.CENTER;
            case CENTER -> VBox.Alignment.RIGHT;
            case RIGHT -> VBox.Alignment.LEFT;
        };
        textVBox.alignment(next);
        // Mark root for layout so effect is immediate
        textVBox.markDirty(io.github.currenj.gelatinui.gui.DirtyFlag.LAYOUT);
    }

    private void cycleOuterAlignment(UIEvent e) {
        if (outerVBox == null) return;
        VBox.Alignment current = outerVBox.getAlignment();
        VBox.Alignment next = switch (current) {
            case LEFT -> VBox.Alignment.CENTER;
            case CENTER -> VBox.Alignment.RIGHT;
            case RIGHT -> VBox.Alignment.LEFT;
        };
        outerVBox.alignment(next);
        // Mark outer VBox for layout so effect is immediate
        outerVBox.markDirty(io.github.currenj.gelatinui.gui.DirtyFlag.LAYOUT);

        // Force a synchronous layout pass so the change is visible immediately
        outerVBox.forceLayout();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw title using traditional method
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 100, 0xFFFFFF);

        // Draw performance info
//        drawPerformanceInfo(guiGraphics);
    }

    @Override
    protected void updateComponentSizes(MinecraftRenderContext context) {
        // Helper method to ensure labels have correct sizes
        if (uiScreen.getRoot() instanceof VBox root) {
            // Navigate to inner VBox if nested
            for (var child : root.getChildren()) {
                if (child instanceof VBox innerVBox) {
                    for (var innerChild : innerVBox.getChildren()) {
                        if (innerChild instanceof Label label) {
                            label.updateSize(context);
                        }
                    }
                } else if (child instanceof Label label) {
                    label.updateSize(context);
                }
            }
        }
    }

    protected void drawPerformanceInfo(GuiGraphics guiGraphics) {
        totalTime += 0.016f; // Approximate frame time
        String perfInfo = String.format("Dirty Flag System Active | Time: %.1fs", totalTime);
        guiGraphics.drawString(this.font, perfInfo, 10, this.height - 20, UI.rgb(100, 255, 100), false);

        // Debug mode indicators
        boolean bounds = io.github.currenj.gelatinui.gui.UIElement.isDebugBoundsEnabled();
        boolean grid = io.github.currenj.gelatinui.gui.UIElement.isDebugGridEnabled();
        boolean pad = io.github.currenj.gelatinui.gui.UIElement.isDebugPaddingEnabled();

        String debugInfo = String.format("Debug: [8]Bounds:%s  [9]Grid:%s  [0]Pad:%s",
                bounds ? "ON " : "off", grid ? "ON " : "off", pad ? "ON" : "off");
        guiGraphics.drawString(this.font, debugInfo, 10, this.height - 30, UI.rgb(255, 255, 100), false);
    }

    private void addHoverEffects(Label label) {
        label.addEventListener(event -> {
            if (event.getType() == UIEvent.Type.HOVER_ENTER) {
                // Scale up the text on hover
                label.scale(1.2f);
                // Change color to indicate hover
                if (label.getText().equals("New GUI System")) {
                    label.color(UI.rgb(255, 255, 150)); // Yellow tint for title
                } else if (label.getText().equals("With Dirty Flags & Caching")) {
                    label.color(UI.rgb(200, 255, 200)); // Green tint for subtitle
                } else {
                    label.color(UI.rgb(200, 255, 200)); // Green tint for info
                }
            } else if (event.getType() == UIEvent.Type.HOVER_EXIT) {
                // Scale back to normal
                label.scale(1.0f);
                // Reset to original color
                if (label.getText().equals("New GUI System")) {
                    label.color(UI.rgb(255, 255, 255)); // White for title
                } else if (label.getText().equals("With Dirty Flags & Caching")) {
                    label.color(UI.rgb(200, 200, 200)); // Light gray for subtitle
                } else {
                    label.color(UI.rgb(150, 255, 150)); // Light green for info
                }
            }
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 54) { // Key '6' = GLFW_KEY_
            outerVBox.scaleToHeight(this.uiScreen.getViewHeight());
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
