package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.Identifier;
import io.github.currenj.gelatinui.GelatinUi;
import org.joml.Vector2f;

/**
 * Demo screen showcasing the ManualContainer component.
 */
public class ManualContainerTestScreen extends GelatinUIScreen<GelatinMenu> {
    private ManualContainer manualContainer;

    public ManualContainerTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("ManualContainer Demo"));
    }

    @Override
    protected void buildUI() {
        // Create a root panel that centers the manual container
        VBox root = UI.vbox()
                .spacing(10)
                .padding(10)
                .alignment(VBox.Alignment.CENTER)
                .fillWidth(true);

        Label title = new Label("ManualContainer Demo", 0xFFFFFFFF, true).scale(1.2f);
        root.addChild(title);

        // Create the manual container and size it explicitly
        manualContainer = UI.manualContainer()
                .setSize(220, 120)
                .backgroundColor(0xFF222222);

        // Optionally limit children for demo
        manualContainer.maxChildren(6);

        // Add a textured background sprite (if texture exists)
        Identifier tex = Identifier.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/panel.png");
        SpriteData spriteData = SpriteData.texture(tex)
                .uv(0, 0, 15, 15)
                .slice(6, 7, 6, 7)
                .textureSize(16)
                .renderMode(SpriteRenderMode.SLICE);
        // Use sprite rectangle if texture is available; fall back to colored panel
        try {
            SpriteRectangle<SpriteRectangle.SpriteRectangleImpl> bg = UI.spriteRectangle(220, 120, tex).texture(spriteData);
            // Put background as first child so it draws behind others
            bg.setPosition(new Vector2f(0, 0));
            manualContainer.addChildAt(bg, 110, 60);
        } catch (Exception ignored) {
            // ignore if texture helper fails; already set backgroundColor above
        }

        // Create some item renderers and place them manually (centered at the given coords)
        ItemRenderer<ItemRenderer.ItemRendererImpl> item1 = UI.itemRenderer(new ItemStack(Items.DIAMOND)).itemScale(1.2f);
        item1.onMouseEnter(e -> item1.setTargetScale(2f, true)).onMouseExit(e -> item1.setTargetScale(1.2f, false));
        ItemRenderer<ItemRenderer.ItemRendererImpl> item2 = UI.itemRenderer(new ItemStack(Items.GOLD_INGOT)).itemScale(1.0f);
        ItemRenderer<ItemRenderer.ItemRendererImpl> item3 = UI.itemRenderer(new ItemStack(Items.APPLE)).itemScale(1.0f);
        ItemRenderer<ItemRenderer.ItemRendererImpl> item4 = UI.itemRenderer(new ItemStack(Items.EMERALD)).itemScale(1.0f);

        // Set sizes so centering calculations behave predictably
        item1.setSize(24, 24);
        item2.setSize(20, 20);
        item3.setSize(20, 20);
        item4.setSize(20, 20);

        // Add them at manual positions (coordinates are the center points)
        manualContainer.addChildAt(item1, 40, 40);
        manualContainer.addChildAt(item2, 110, 40);
        manualContainer.addChildAt(item3, 180, 40);
        manualContainer.addChildAt(item4, 110, 85);

        // Add a small label manually positioned
        Label centerLabel = new Label("Center", 0xFFFFFFFF);
        centerLabel.setSize(60, 10);
        manualContainer.addChildAt(centerLabel, 110, 60);

        // Add container into root and set as screen root
        root.addChild(manualContainer);

        uiScreen.setRoot(root);
    }

    @Override
    protected void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Darken the screen background slightly
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF0A0A0A);
    }

    @Override
    protected void updateComponentSizes(MinecraftRenderContext context) {
        // Update sizes for labels or text if needed
        if (manualContainer != null) {
            for (var child : manualContainer.getChildren()) {
                if (child instanceof Label l) {
                    l.updateSize(context);
                }
            }
        }
    }
}

