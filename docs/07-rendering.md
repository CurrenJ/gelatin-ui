# Rendering & Textures

IRenderContext
- fill(x1,y1,x2,y2,color)
- drawString(text,x,y,color) and drawCenteredString(text,x,y,color)
- getStringWidth(text), getFontHeight()
- pushScissor(x,y,w,h) / popScissor()
- enableBlend() / disableBlend()
- drawTexture(ResourceLocation, x, y, w, h) and UV/atlas variant drawTexture(..., u, v, texW, texH, atlasW, atlasH)

Minecraft integration
- MinecraftRenderContext adapts GuiGraphics and Font to IRenderContext. When UIElement.render runs under this context, it pushes a PoseStack transform for your element: local origin at (0,0), and scaling already applied. Draw at local space.

Textures and sprites
- SpriteRectangle draws either a solid color or a configured SpriteData/ResourceLocation.
- SpriteData lets you specify a sub-region (u,v,width,height) and optional actual content size for centered cropping. Atlas size defaults to 256x256 but can be overridden.
- When drawing textures directly, prefer the UV/atlas overload to match GuiGraphics.blit semantics.

Clipping
- Use pushScissor/popScissor to clip child rendering to a region if your custom component scrolls or masks content.

Measuring text
- Label uses IRenderContext to measure getStringWidth and getFontHeight. Construct a MinecraftRenderContext with the current GuiGraphics and Font when measuring.

