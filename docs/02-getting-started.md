# Getting Started

This section walks you through building a simple screen and opening it in-game.

Prerequisites
- Java 21
- This mod loaded (Fabric or NeoForge via Architectury Loom)
- Basic familiarity with vanilla Screens

Create a screen
1) Subclass GelatinUIScreen and implement buildUI:
- Create a root container (VBox/HBox/Panel) using UI builder functions.
- Build out child components.
- Call uiScreen.setRoot(root) at the end.

Notes for text sizing (Label)
- Label auto-sizes based on font metrics, but it needs an IRenderContext to measure.
- In GelatinUIScreen.buildUI you can construct a temporary MinecraftRenderContext to call label.updateSize(context) before the first layout. The framework also calls updateComponentSizes(context) each frame, so you can centralize measurements there.

Open the screen
- Fabric example: register a packet (see fabric/GelatinUiModFabricClient) or bind a key, then call Minecraft.getInstance().setScreen(new YourScreen()).

Resize behavior
- GelatinUIScreen forwards resize events to UIScreen. UIScreen updates the viewport and propagates screen width/height to VBox/HBox roots so fillWidth/fillHeight work.

Skeleton example (abbreviated)
- A minimal GelatinUIScreen that centers a VBox and adds a button:
  - Override buildUI()
  - Create a Label, call updateSize(context), add to VBox
  - Create a SpriteButton and set an onClick runnable
  - uiScreen.setRoot(vbox)

You can browse TestScreen.java for a complete, runnable example that combines most concepts in this guide.

