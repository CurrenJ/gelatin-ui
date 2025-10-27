Gelatin UI — Agent starter prompt

- Project: Gelatin UI is a composable, performant UI toolkit for Minecraft 1.21.1. Targets Fabric and NeoForge via Architectury Loom. Language: Java 21.
- Modules:
  - common: core UI engine, elements, layout, effects, mixins, access widener.
  - fabric: Fabric adapter and entrypoints (fabric.mod.json).
  - neoforge: NeoForge adapter and entrypoint (neoforge.mods.toml).
- Key APIs and classes:
  - gui/IUIElement: base contract (update, render, bounds, events, visibility).
  - gui/UIScreen: manages tree, layout, input routing, culling, tooltip support.
  - GelatinUIScreen: Minecraft Screen glue with buildUI() and renderContent(); tooltip API; time control overlay and debug toggle keys.
    - Debug keys: 8 bounds, 9 grid, 0 padding, 7 culled; time controls: P pause, N step (paused), [ and ] adjust timescale, 4 reset, 5=0.5x, 6=2x.
- Features: VBox/HBox layout; animations/effects (Breathe, Shake, Drift, Wander, ClickBounce, EffectAnimationBinder); dirty flags and bounds caching; tooltip system; example screens in common (TestScreen, EffectsTestScreen, etc.).
- Entry points: fabric.GelatinUiModFabric (+client), neoforge.GelatinUiModNeoForge; common.GelatinUi.init() registers demo/debug screens.
- Build: Gradle + Architectury Loom + Shadow. Group io.github.currenj.gelatinui, mod_id gelatinui, version 1.0.6. Depends on Fabric Loader, Fabric API (Fabric), NeoForge (NeoForge), optional Architectury API.
- Runs/datagen: Loom run configs present; datagen configured in both platform modules; resources include mixins and gelatinui.accesswidener.
- Publishing: GPG signing enabled; aggregate publish to build/central-repo, bundle via createCentralBundle, upload via uploadToCentralPortal (token or user/pass; CENTRAL_PUBLISHING_TYPE USER_MANAGED/AUTOMATIC).
- Docs: see docs/ (start at docs/README.md for guide and links).
- Conventions for edits: put shared logic in common; keep platform code thin; preserve public APIs; write/adjust JUnit 5 tests in common when changing behavior.

Use this context to locate the right module/file, follow existing patterns, and keep Gradle/Loom configuration intact when implementing changes.

