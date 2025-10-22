package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.DirtyFlag;
import io.github.currenj.gelatinui.gui.IUIElement;
import io.github.currenj.gelatinui.gui.IRenderContext;
import io.github.currenj.gelatinui.gui.UIContainer;
import org.joml.Vector2f;

/**
 * Horizontal box layout container.
 * Stacks children horizontally with configurable spacing and alignment.
 * Implements efficient layout caching to avoid redundant calculations.
 */
public class HBox extends UIContainer {
    /**
     * Vertical alignment options for children
     */
    public enum Alignment {
        TOP,
        CENTER,
        BOTTOM
    }

    private Alignment alignment = Alignment.TOP;
    private float spacing = 0;
    private float padding = 0;

    // When true, the HBox will attempt to fill the width of its parent
    private boolean fillWidth = false;

    // When true, the HBox will attempt to fill the width of its parent (screen fallback)
    private float screenWidth = 0;

    // When true, the HBox will attempt to fill the height of its parent
    private boolean fillHeight = false;

    // When true, the HBox will use this screen height when parent is null
    private float screenHeight = 0;

    // When true, children will be uniformly scaled down if they exceed the container bounds
    private boolean scaleToFit = false;

    // Maximum bounds for scaling (if 0, uses container size minus padding)
    private float maxWidth = 0;
    private float maxHeight = 0;

    // Track if layout needs recalculation
    private boolean layoutDirty = true;

    // When true, position changes during the next layout pass will animate instead of snapping
    private boolean animatePositions = false;

    public HBox() {
        this.size.set(0, 0);
    }

    public HBox spacing(float spacing) {
        if (this.spacing != spacing) {
            this.spacing = spacing;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public HBox alignment(Alignment alignment) {
        if (this.alignment != alignment) {
            this.alignment = alignment;
            // Request animated re-positioning for children when alignment changes
            this.animatePositions = true;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public HBox padding(float padding) {
        if (this.padding != padding) {
            this.padding = padding;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public HBox fillWidth(boolean fillWidth) {
        if (this.fillWidth != fillWidth) {
            this.fillWidth = fillWidth;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public void setScreenWidth(float screenWidth) {
        if (this.screenWidth != screenWidth) {
            this.screenWidth = screenWidth;
            if (fillWidth && parent == null) {
                markDirty(DirtyFlag.LAYOUT);
            }
        }
    }

    public HBox fillHeight(boolean fillHeight) {
        if (this.fillHeight != fillHeight) {
            this.fillHeight = fillHeight;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public void setScreenHeight(float screenHeight) {
        if (this.screenHeight != screenHeight) {
            this.screenHeight = screenHeight;
            if (fillHeight && parent == null) {
                markDirty(DirtyFlag.LAYOUT);
            }
        }
    }

    public HBox scaleToFit(boolean scaleToFit) {
        if (this.scaleToFit != scaleToFit) {
            this.scaleToFit = scaleToFit;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public HBox maxWidth(float maxWidth) {
        if (this.maxWidth != maxWidth) {
            this.maxWidth = maxWidth;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public HBox maxHeight(float maxHeight) {
        if (this.maxHeight != maxHeight) {
            this.maxHeight = maxHeight;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    @Override
    protected void performLayout() {
        if (children.isEmpty()) {
            size.set(padding * 2, padding * 2);
            layoutDirty = false;
            animatePositions = false;
            return;
        }

        // First, ensure all child containers have performed their layout
        for (IUIElement child : children) {
            if (child instanceof VBox vbox) {
                vbox.forceLayout();
            } else if (child instanceof HBox hbox) {
                hbox.forceLayout();
            }
        }

        // First pass: calculate base content size from children's unscaled sizes
        float baseMaxHeight = 0f;
        float baseTotalWidth = padding;

        for (IUIElement child : children) {
            if (!child.isVisible()) continue;
            Vector2f childSize = child.getSize();
            baseTotalWidth += childSize.x + spacing;
            baseMaxHeight = Math.max(baseMaxHeight, childSize.y);
        }

        // Remove extra spacing after last element
        if (baseTotalWidth > padding) {
            baseTotalWidth -= spacing;
        }
        baseTotalWidth += padding;

        // Decide initial finalWidth/finalHeight (unscaled baseline)
        float finalWidth = baseTotalWidth;
        if (fillWidth) {
            if (parent != null) {
                finalWidth = parent.getSize().x;
            } else if (screenWidth > 0) {
                finalWidth = screenWidth;
            }
        }

        float finalHeight = baseMaxHeight + padding * 2;
        if (fillHeight) {
            if (parent != null) {
                finalHeight = parent.getSize().y;
            } else if (screenHeight > 0) {
                finalHeight = screenHeight;
            }
        }

        // Compute scaleFactor if needed (based on baseline content)
        float scaleFactor = 1.0f;
        if (scaleToFit) {
            float availableWidth = (this.maxWidth > 0 ? this.maxWidth : finalWidth) - padding * 2;
            float availableHeight = (this.maxHeight > 0 ? this.maxHeight : finalHeight) - padding * 2;

            float contentWidth = baseTotalWidth - padding * 2;
            float contentHeight = baseMaxHeight;

            float widthScale = availableWidth > 0 && contentWidth > availableWidth ? availableWidth / contentWidth : 1.0f;
            float heightScale = availableHeight > 0 && contentHeight > availableHeight ? availableHeight / contentHeight : 1.0f;

            scaleFactor = Math.min(widthScale, heightScale);
        }

        // Determine effective scaled content size (take into account existing child scales when not scaling-to-fit)
        float scaledTotalWidth = 0f;
        float scaledMaxHeight = 0f;
        boolean first = true;
        for (IUIElement child : children) {
            if (!child.isVisible()) continue;
            Vector2f childSize = child.getSize();
            float childScale = 1.0f;
            if (!scaleToFit && child instanceof io.github.currenj.gelatinui.gui.UIElement uiChild) {
                childScale = uiChild.getCurrentScale();
            } else if (scaleToFit) {
                childScale = scaleFactor;
            }
            float w = childSize.x * childScale;
            float h = childSize.y * childScale;
            if (first) {
                scaledTotalWidth = padding + w;
                first = false;
            } else {
                scaledTotalWidth += spacing + w;
            }
            scaledMaxHeight = Math.max(scaledMaxHeight, h);
        }

        if (first) {
            // no visible children
            scaledTotalWidth = padding * 2;
            scaledMaxHeight = padding * 2;
        } else {
            scaledTotalWidth += padding;
        }

        // Use scaled content to set final container size unless fill flags force other sizes
        if (fillWidth) {
            // keep finalWidth as parent/screen width
        } else {
            finalWidth = scaledTotalWidth;
        }

        if (fillHeight) {
            // keep finalHeight
        } else {
            finalHeight = scaledMaxHeight + padding * 2;
        }

        // Ensure we respect configured max bounds when scaleToFit is enabled
        if (scaleToFit) {
            if (this.maxWidth > 0) {
                finalWidth = Math.min(finalWidth, this.maxWidth);
            }
            if (this.maxHeight > 0) {
                finalHeight = Math.min(finalHeight, this.maxHeight);
            }
        }

        size.set(finalWidth, finalHeight);

        // Now actually apply scaling to children (preserve previous behavior of setting targetScale)
        if (scaleToFit) {
            if (scaleFactor < 1.0f) {
                for (IUIElement child : children) {
                    if (!child.isVisible()) continue;
                    if (child instanceof io.github.currenj.gelatinui.gui.UIElement uiChild) {
                        uiChild.scale(scaleFactor);
                    }
                }
            } else {
                for (IUIElement child : children) {
                    if (!child.isVisible()) continue;
                    if (child instanceof io.github.currenj.gelatinui.gui.UIElement uiChild) {
                        uiChild.scale(1.0f);
                    }
                }
            }
        }

        // Position children using effective scale (scaleFactor if scaleToFit, otherwise child's currentScale)
        float xOffset = padding;
        float contentHeight = size.y - padding * 2;

        for (IUIElement child : children) {
            if (!child.isVisible()) continue;
            Vector2f childSize = child.getSize();
            float effectiveScale = scaleToFit ? scaleFactor : (child instanceof io.github.currenj.gelatinui.gui.UIElement uiChild ? uiChild.getCurrentScale() : 1.0f);
            float scaledChildWidth = childSize.x * effectiveScale;
            float scaledChildHeight = childSize.y * effectiveScale;
            float yOffsetLocal;

            switch (alignment) {
                case CENTER:
                    yOffsetLocal = padding + (contentHeight - scaledChildHeight) / 2f;
                    break;
                case BOTTOM:
                    yOffsetLocal = size.y - scaledChildHeight - padding;
                    break;
                case TOP:
                default:
                    yOffsetLocal = padding;
                    break;
            }

            Vector2f targetPos = new Vector2f(xOffset, yOffsetLocal);
            if (animatePositions) {
                if (child instanceof io.github.currenj.gelatinui.gui.UIElement) {
                    ((io.github.currenj.gelatinui.gui.UIElement) child).setTargetPosition(targetPos, true);
                } else {
                    child.setPosition(targetPos);
                }
            } else {
                if (child instanceof io.github.currenj.gelatinui.gui.UIElement) {
                    io.github.currenj.gelatinui.gui.UIElement uiChild = (io.github.currenj.gelatinui.gui.UIElement) child;
                    if (uiChild.isAnimating()) {
                        Vector2f currentTarget = uiChild.getTargetPosition();
                        if (!currentTarget.equals(targetPos, 0.001f)) {
                            uiChild.setTargetPosition(targetPos, true);
                        }
                    } else {
                        child.setPosition(targetPos);
                    }
                } else {
                    child.setPosition(targetPos);
                }
            }

            xOffset += scaledChildWidth + spacing * effectiveScale;
        }

        layoutDirty = false;
        animatePositions = false;
    }

    @Override
    protected void recalculateLayout() {
        if (!layoutDirty) {
            layoutDirty = true;
        }
        super.recalculateLayout();
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (layoutDirty) {
            performLayout();
        }
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        // HBox doesn't render itself, only children
    }

    public float getSpacing() {
        return spacing;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public float getPadding() {
        return padding;
    }
}
