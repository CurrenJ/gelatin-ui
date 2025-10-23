package io.github.currenj.gelatinui;

import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.components.VBox;
import io.github.currenj.gelatinui.gui.components.Label;
import io.github.currenj.gelatinui.gui.components.ItemTabs;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

        // Tab 2 content
        VBox tab2Content = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label tab2Label1 = UI.label(tempContext, "Emeralds are rare", UI.rgb(180, 255, 200));
        Label tab2Label2 = UI.label(tempContext, "Selected: Emerald", UI.rgb(255, 255, 255));
        tab2Content.addChild(tab2Label1);
        tab2Content.addChild(tab2Label2);

        // Tab 3 content
        VBox tab3Content = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        Label appleLabelTab = UI.label(tempContext, "An apple a day", UI.rgb(255, 200, 200));
        Label tab3Label2 = UI.label(tempContext, "Selected: Apple", UI.rgb(255, 255, 255));
        tab3Content.addChild(appleLabelTab);
        tab3Content.addChild(tab3Label2);

        itemTabs.addTab(new ItemStack(Items.DIAMOND), tab1Content);
        itemTabs.addTab(new ItemStack(Items.AIR), tab2Content);
        itemTabs.addTab(new ItemStack(Items.APPLE), tab3Content);

        itemTabs.update(0);
        outerVBox.addChild(itemTabs);

        // Set screen root
        uiScreen.setRoot(outerVBox);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw title using traditional method
//        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 100, 0xFFFFFF);
    }
}
