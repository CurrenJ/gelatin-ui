Gelatin UI

A composable, performant UI toolkit for Minecraft (Fabric + NeoForge via Architectury Loom). Build modern screens with a clean builder API and extendable components.

Highlights
- Declarative tree of UIElements (Label, SpriteRectangle, SpriteButton, ItemRenderer, ProgressBar, etc.)
- Predictable layout with VBox/HBox
- Dirty flags, bounds caching, and culling for performance
- Smooth interpolation and keyframe animations
- **2D Particle System** with physics simulation and 3D item rendering
- Debug overlays and culled inspector for development

Docs
- Full developer guide lives in docs/. Start with docs/README.md.

Examples
- See common/src/main/java/io/github/currenj/gelatinui/TestScreen.java for a runnable demo screen that exercises most features.
- See common/src/main/java/io/github/currenj/gelatinui/example/ItemAnimationsTestScreen.java for particle system demonstration.

Particle System
- Full-featured 2D particle effect system with sprite and 3D itemstack rendering
- Physics simulation: velocity, angular velocity, and gravity
- Property interpolation: scale, alpha, and color transitions over lifetime
- See PARTICLE_SYSTEM.md for complete documentation and examples

License
- See LICENSE (or project metadata) for details.

