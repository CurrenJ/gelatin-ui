package io.github.currenj.gelatinui.example;

import io.github.currenj.gelatinui.GelatinUIScreen;
import io.github.currenj.gelatinui.gui.UI;
import io.github.currenj.gelatinui.gui.components.*;
import io.github.currenj.gelatinui.gui.minecraft.MinecraftRenderContext;
import io.github.currenj.gelatinui.gui.GelatinMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Demonstrates nested ItemTabs — tabs whose content panels themselves contain further tab widgets.
 * Each level uses content panels of varying heights to exercise the smooth-repositioning fix.
 */
public class NestedTabsTestScreen extends GelatinUIScreen<GelatinMenu> {

    public NestedTabsTestScreen(GelatinMenu menu, Inventory inv) {
        super(menu, inv, Component.literal("Nested Tabs Test"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void buildUI() {
        MinecraftRenderContext ctx = new MinecraftRenderContext(
            new GuiGraphics(this.minecraft, this.minecraft.renderBuffers().bufferSource()),
            this.font
        );

        // ── Outer container ──────────────────────────────────────────────────
        VBox root = UI.vbox()
                .spacing(12)
                .padding(20)
                .alignment(VBox.Alignment.CENTER)
                .fillWidth(true);

        Label title = UI.label(ctx, "Nested Tabs Demo", UI.rgb(255, 220, 100));
        root.addChild(title);

        // ── Outer ItemTabs (3 tabs) ───────────────────────────────────────────
        Label outerStatus = UI.label(ctx, "Outer tab: 1", UI.rgb(200, 200, 200));
        root.addChild(outerStatus);

        ItemTabs outerTabs = UI.itemTabs()
                .tabSpacing(4)
                .onSelectionChanged(i -> outerStatus.text("Outer tab: " + (i + 1)).color(UI.rgb(255, 255, 255)));

        // ── Outer tab 1: small plain content (used as baseline) ──────────────
        VBox tab1Content = UI.vbox().spacing(6).alignment(VBox.Alignment.CENTER);
        tab1Content.addChild(UI.label(ctx, "A simple tab", UI.rgb(180, 220, 255)));
        tab1Content.addChild(UI.label(ctx, "No nested tabs here.", UI.rgb(200, 200, 200)));
        tab1Content.addChild(UI.label(ctx, "Switch to tab 2 or 3", UI.rgb(200, 200, 200)));
        tab1Content.addChild(UI.label(ctx, "to see nested tabs.", UI.rgb(200, 200, 200)));

        // ── Outer tab 2: contains an inner ItemTabs with 3 sub-tabs ──────────
        VBox tab2Content = UI.vbox().spacing(8).alignment(VBox.Alignment.CENTER);
        tab2Content.addChild(UI.label(ctx, "Inner tabs:", UI.rgb(180, 255, 200)));

        Label innerStatus = UI.label(ctx, "Inner tab: 1", UI.rgb(200, 200, 200));
        tab2Content.addChild(innerStatus);

        ItemTabs innerTabs = UI.itemTabs()
                .tabSpacing(4)
                .onSelectionChanged(i -> innerStatus.text("Inner tab: " + (i + 1)).color(UI.rgb(255, 255, 255)));

        // Inner tab A — two short labels (small height)
        VBox innerTabA = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        innerTabA.addChild(UI.label(ctx, "Wheat", UI.rgb(255, 220, 100)));
        innerTabA.addChild(UI.label(ctx, "Bread ingredient", UI.rgb(220, 200, 150)));

        // Inner tab B — several labels (medium height, different from A)
        VBox innerTabB = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        innerTabB.addChild(UI.label(ctx, "Diamond", UI.rgb(100, 220, 255)));
        innerTabB.addChild(UI.label(ctx, "Hard as a rock", UI.rgb(200, 240, 255)));
        innerTabB.addChild(UI.label(ctx, "Found at Y -58", UI.rgb(180, 220, 240)));
        innerTabB.addChild(UI.label(ctx, "Use silk touch to mine", UI.rgb(160, 200, 220)));
        innerTabB.addChild(UI.label(ctx, "Great for tools and armour", UI.rgb(140, 180, 200)));

        // Inner tab C — a progress bar row (larger content, different width)
        VBox innerTabC = UI.vbox().spacing(6).alignment(VBox.Alignment.CENTER);
        innerTabC.addChild(UI.label(ctx, "Experience", UI.rgb(100, 255, 100)));
        innerTabC.addChild(UI.label(ctx, "Progress to next level:", UI.rgb(200, 255, 200)));
        SpriteProgressBar xpBar = UI.progressBar().progress(0.65f).skillLevel(7);
        innerTabC.addChild(xpBar);
        innerTabC.addChild(UI.label(ctx, "Keep grinding!", UI.rgb(150, 230, 150)));

        innerTabs.addTab(new ItemStack(Items.WHEAT), innerTabA);
        innerTabs.addTab(new ItemStack(Items.DIAMOND), innerTabB);
        innerTabs.addTab(new ItemStack(Items.EXPERIENCE_BOTTLE), innerTabC);
        innerTabs.update(0);
        tab2Content.addChild(innerTabs);

        // ── Outer tab 3: another inner ItemTabs, different item count / sizes ─
        VBox tab3Content = UI.vbox().spacing(8).alignment(VBox.Alignment.CENTER);
        tab3Content.addChild(UI.label(ctx, "Another level of tabs:", UI.rgb(255, 180, 180)));

        Label deepStatus = UI.label(ctx, "Deep tab: 1", UI.rgb(200, 200, 200));
        tab3Content.addChild(deepStatus);

        ItemTabs deepTabs = UI.itemTabs()
                .tabSpacing(4)
                .onSelectionChanged(i -> deepStatus.text("Deep tab: " + (i + 1)).color(UI.rgb(255, 255, 255)));

        // Deep tab 1 — minimal (just a label)
        VBox deepTabA = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        deepTabA.addChild(UI.label(ctx, "Redstone", UI.rgb(255, 80, 80)));

        // Deep tab 2 — tall content (many rows)
        VBox deepTabB = UI.vbox().spacing(4).alignment(VBox.Alignment.CENTER);
        deepTabB.addChild(UI.label(ctx, "Gold Ingot", UI.rgb(255, 215, 0)));
        deepTabB.addChild(UI.label(ctx, "Obtained by smelting", UI.rgb(230, 195, 80)));
        deepTabB.addChild(UI.label(ctx, "raw gold ore", UI.rgb(210, 175, 60)));
        deepTabB.addChild(UI.label(ctx, "Used in crafting:", UI.rgb(230, 195, 80)));
        deepTabB.addChild(UI.label(ctx, "  - Golden sword", UI.rgb(210, 175, 60)));
        deepTabB.addChild(UI.label(ctx, "  - Golden helmet", UI.rgb(210, 175, 60)));
        deepTabB.addChild(UI.label(ctx, "  - Golden apple", UI.rgb(210, 175, 60)));

        // Deep tab 3 — medium, uses an HBox of items
        VBox deepTabC = UI.vbox().spacing(6).alignment(VBox.Alignment.CENTER);
        deepTabC.addChild(UI.label(ctx, "Nether items", UI.rgb(255, 140, 60)));
        HBox netherRow = UI.hbox().spacing(8).alignment(HBox.Alignment.CENTER);
        netherRow.addChild(UI.itemRenderer(new ItemStack(Items.NETHERRACK)));
        netherRow.addChild(UI.itemRenderer(new ItemStack(Items.NETHER_BRICK)));
        netherRow.addChild(UI.itemRenderer(new ItemStack(Items.BLAZE_ROD)));
        deepTabC.addChild(netherRow);
        deepTabC.addChild(UI.label(ctx, "From the Nether dimension", UI.rgb(220, 120, 50)));

        deepTabs.addTab(new ItemStack(Items.REDSTONE), deepTabA);
        deepTabs.addTab(new ItemStack(Items.GOLD_INGOT), deepTabB);
        deepTabs.addTab(new ItemStack(Items.NETHERRACK), deepTabC);
        deepTabs.update(0);
        tab3Content.addChild(deepTabs);

        // ── Wire up outer tabs ────────────────────────────────────────────────
        outerTabs.addTab(new ItemStack(Items.GRASS_BLOCK), tab1Content);
        outerTabs.addTab(new ItemStack(Items.CRAFTING_TABLE), tab2Content);
        outerTabs.addTab(new ItemStack(Items.NETHER_STAR), tab3Content);
        outerTabs.update(0);

        root.addChild(outerTabs);
        uiScreen.setRoot(root);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}
}
