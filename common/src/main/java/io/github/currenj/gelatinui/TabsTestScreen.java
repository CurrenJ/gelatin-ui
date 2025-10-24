package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.ArrayList;

public class TabsTestScreen extends GelatinUIScreen {
    public TabsTestScreen() {
        super(Component.literal("Tabs Test"));
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

        // Create outer VBox
        VBox outerVBox = UI.vbox()
            .spacing(15)
            .padding(30)
            .alignment(VBox.Alignment.CENTER)
            .fillWidth(true);

        // Add an ItemTabs demo section
        Label tabsLabel = UI.label(tempContext, "Item Tabs:", UI.rgb(200, 200, 255));
        outerVBox.addChild(tabsLabel);

        ItemTabs itemTabs = UI.itemTabs()
                .sizes(18, 18, 16, 16)
                // Use existing textures as placeholder frames; replace with your frame sprites as needed
                .hoverFrame(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_embellishment_1.png"))
                .selectedFrame(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_gold_outline.png"))
                .tabSpacing(4)
                .onSelectionChanged(index -> {
                    // Example: change the title to reflect the selected tab
                    tabsLabel
                            .text("Item Tabs: Tab " + (index + 1))
                            .color(UI.rgb(255, 255, 255));
                });

        // Tab 1 content
        VBox tab1Content = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab1Label1 = UI.label(tempContext, "Diamonds are forever", UI.rgb(180, 220, 255));
        Label tab1Label2 = UI.label(tempContext, "Selected: Diamond", UI.rgb(255, 255, 255));
        tab1Content.addChild(tab1Label1);
        tab1Content.addChild(tab1Label2);

        SpriteRectangle.SpriteRectangleImpl tab1tooltip = UI.spriteRectangle(120, 20, UI.rgb(0, 100, 200))
                .text("Shiny and valuable!", UI.rgb(255, 255, 0));

        // Tab 2 content
        VBox tab2Content = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab2Label1 = UI.label(tempContext, "Emeralds are rare", UI.rgb(180, 255, 200));
        Label tab2Label2 = UI.label(tempContext, "Selected: Emerald", UI.rgb(255, 255, 255));
        tab2Content.addChild(tab2Label1);
        tab2Content.addChild(tab2Label2);

        SpriteRectangle.SpriteRectangleImpl tab2tooltip = UI.spriteRectangle(130, 20, UI.rgb(0, 150, 0))
                .text("Villager currency!", UI.rgb(255, 255, 255));

        // Tab 3 content
        VBox tab3Content = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label appleLabelTab = UI.label(tempContext, "An apple a day", UI.rgb(255, 200, 200));
        Label tab3Label2 = UI.label(tempContext, "Selected: Apple", UI.rgb(255, 255, 255));
        tab3Content.addChild(appleLabelTab);
        tab3Content.addChild(tab3Label2);

        SpriteRectangle.SpriteRectangleImpl tab3tooltip = UI.spriteRectangle(100, 20, UI.rgb(200, 0, 0))
                .text("Keeps the doctor away!", UI.rgb(255, 255, 255));

        itemTabs.addTab(new ItemStack(Items.DIAMOND), tab1Content).tooltip(uiScreen, tab1tooltip);
        itemTabs.addTab(new ItemStack(Items.EMERALD), tab2Content).tooltip(uiScreen, tab2tooltip);
        itemTabs.addTab(new ItemStack(Items.APPLE), tab3Content).tooltip(uiScreen, tab3tooltip);

        itemTabs.update(0);
        outerVBox.addChild(itemTabs);

        // Add a second ItemTabs demo section
        Label tabsLabel2 = UI.label(tempContext, "Second Item Tabs:", UI.rgb(200, 255, 200));
        outerVBox.addChild(tabsLabel2);

        ItemTabs itemTabs2 = UI.itemTabs()
                .sizes(18, 18, 16, 16)
                .hoverFrame(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_embellishment_1.png"))
                .selectedFrame(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(GelatinUi.MOD_ID, "textures/gui/progress_bar_gold_outline.png"))
                .tabSpacing(4)
                .onSelectionChanged(index -> {
                    tabsLabel2
                            .text("Second Item Tabs: Tab " + (index + 1))
                            .color(UI.rgb(255, 255, 255));
                });

        // Tab 1 content for second tabs: Labels and progress bar
        VBox tab1Content2 = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab1Label1_2 = UI.label(tempContext, "Gold is shiny", UI.rgb(255, 215, 0));
        Label tab1Label2_2 = UI.label(tempContext, "Selected: Gold", UI.rgb(255, 255, 255));
        SpriteProgressBar progressBar = UI.progressBar().progress(0.7f).skillLevel(50);
        tab1Content2.addChild(tab1Label1_2);
        tab1Content2.addChild(tab1Label2_2);
        tab1Content2.addChild(progressBar);

        // Tab 2 content: HBox with item renderers
        VBox tab2Content2 = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab2Label_2 = UI.label(tempContext, "Iron Items", UI.rgb(200, 200, 200));
        HBox itemBox = UI.hbox().spacing(10).alignment(HBox.Alignment.CENTER);
        ItemRenderer ironRenderer = UI.itemRenderer(new ItemStack(Items.IRON_INGOT, 10));
        ItemRenderer ironSwordRenderer = UI.itemRenderer(new ItemStack(Items.IRON_SWORD, 1));
        itemBox.addChild(ironRenderer);
        itemBox.addChild(ironSwordRenderer);
        tab2Content2.addChild(tab2Label_2);
        tab2Content2.addChild(itemBox);

        // Tab 3 content: Rotating item ring
        VBox tab3Content2 = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab3Label_2 = UI.label(tempContext, "Coal Ring", UI.rgb(100, 100, 100));
        RotatingItemRing ring = UI.rotatingItemRing()
                .radius(40)
                .defaultAngularSpeed(0.5f)
                .defaultItemScale(1.0f)
                .hoverItemScale(1.2f)
                .selectedItemScale(1.4f);
        List<ItemStack> ringItems = new ArrayList<>();
        ringItems.add(new ItemStack(Items.COAL, 64));
        ringItems.add(new ItemStack(Items.CHARCOAL, 32));
        ring.setItems(ringItems);
        tab3Content2.addChild(tab3Label_2);
        tab3Content2.addChild(ring);

        // Tab 4 content: Buttons
        VBox tab4Content2 = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab4Label_2 = UI.label(tempContext, "Redstone Controls", UI.rgb(255, 0, 0));
        SpriteButton button1 = UI.spriteButton(100, 20, UI.rgb(100, 100, 255)).text("Activate", UI.rgb(255, 255, 255));
        SpriteButton button2 = UI.spriteButton(100, 20, UI.rgb(255, 100, 100)).text("Deactivate", UI.rgb(255, 255, 255));
        tab4Content2.addChild(tab4Label_2);
        tab4Content2.addChild(button1);
        tab4Content2.addChild(button2);

        // Tab 5 content: Mixed
        VBox tab5Content2 = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab5Label_2 = UI.label(tempContext, "Lapis Mix", UI.rgb(0, 0, 255));
        ItemRenderer lapisRenderer = UI.itemRenderer(new ItemStack(Items.LAPIS_LAZULI, 20));
        Label tab5Label2_2 = UI.label(tempContext, "Enchanting!", UI.rgb(150, 150, 255));
        tab5Content2.addChild(tab5Label_2);
        tab5Content2.addChild(lapisRenderer);
        tab5Content2.addChild(tab5Label2_2);

        itemTabs2.addTab(new ItemStack(Items.GOLD_INGOT), tab1Content2);
        itemTabs2.addTab(new ItemStack(Items.IRON_INGOT), tab2Content2);
        itemTabs2.addTab(new ItemStack(Items.COAL), tab3Content2);
        itemTabs2.addTab(new ItemStack(Items.REDSTONE), tab4Content2);
        itemTabs2.addTab(new ItemStack(Items.LAPIS_LAZULI), tab5Content2);

        itemTabs2.update(0);
        outerVBox.addChild(itemTabs2);

        // Set screen root
        uiScreen.setRoot(outerVBox);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw title using traditional method
//        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 100, 0xFFFFFF);
    }
}
