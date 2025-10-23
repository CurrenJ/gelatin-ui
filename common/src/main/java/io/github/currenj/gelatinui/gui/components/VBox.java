package io.github.currenj.gelatinui.gui.components;

import io.github.currenj.gelatinui.gui.*;
import org.joml.Vector2f;

/**
 * Vertical box layout container.
 * Stacks children vertically with configurable spacing and alignment.
 * Implements efficient layout caching to avoid redundant calculations.
 */
public class VBox extends UIContainer<VBox> {
    /**
     * Horizontal alignment options for children
     */
    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }

    private Alignment alignment = Alignment.LEFT;
    private float spacing = 0;
    private float padding = 0;

    // When true, the VBox will attempt to fill the width of its parent
    private boolean fillWidth = false;

    // When true, the VBox will attempt to fill the width of its parent
    private float screenWidth = 0;

    // When true, the VBox will attempt to fill the height of its parent
    private boolean fillHeight = false;

    // When true, the VBox will use this screen height when parent is null
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

    public VBox() {
        this.size.set(0, 0);
    }

    public VBox spacing(float spacing) {
        if (this.spacing != spacing) {
            this.spacing = spacing;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public VBox alignment(Alignment alignment) {
        if (this.alignment != alignment) {
            this.alignment = alignment;
            // Request animated re-positioning for children when alignment changes
            this.animatePositions = true;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public VBox padding(float padding) {
        if (this.padding != padding) {
            this.padding = padding;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public VBox fillWidth(boolean fillWidth) {
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

    public VBox fillHeight(boolean fillHeight) {
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

    public VBox scaleToFit(boolean scaleToFit) {
        if (this.scaleToFit != scaleToFit) {
            this.scaleToFit = scaleToFit;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public VBox maxWidth(float maxWidth) {
        if (this.maxWidth != maxWidth) {
            this.maxWidth = maxWidth;
            markDirty(DirtyFlag.LAYOUT);
        }
        return this;
    }

    public VBox maxHeight(float maxHeight) {
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

        // First pass: calculate baseline content size from children's unscaled sizes
        float baseMaxWidth = 0f;
        float baseTotalHeight = padding;

        for (IUIElement child : children) {
            if (!child.isVisible()) continue;
            Vector2f childSize = child.getSize();
            baseMaxWidth = Math.max(baseMaxWidth, childSize.x);
            baseTotalHeight += childSize.y + spacing;
        }

        // Remove extra spacing after last element
        if (baseTotalHeight > padding) {
            baseTotalHeight -= spacing;
        }
        baseTotalHeight += padding;

        // Decide initial finalWidth/finalHeight (unscaled baseline)
        float finalWidth = baseMaxWidth + padding * 2;
        if (fillWidth) {
            if (parent != null) {
                finalWidth = parent.getSize().x;
            } else if (screenWidth > 0) {
                finalWidth = screenWidth;
            }
        }

        float finalHeight = baseTotalHeight;
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

            float contentWidth = baseMaxWidth;
            float contentHeight = baseTotalHeight - padding * 2;

            float widthScale = availableWidth > 0 && contentWidth > availableWidth ? availableWidth / contentWidth : 1.0f;
            float heightScale = availableHeight > 0 && contentHeight > availableHeight ? availableHeight / contentHeight : 1.0f;

            scaleFactor = Math.min(widthScale, heightScale);
        }

        // Determine effective scaled content size (take into account existing child scales when not scaling-to-fit)
        float scaledMaxWidth = 0f;
        float scaledTotalHeight = 0f;
        boolean first = true;
        for (IUIElement child : children) {
            if (!child.isVisible()) continue;
            Vector2f childSize = child.getSize();
            float childScale = 1.0f;
            if (!scaleToFit && child instanceof UIElement<?> uiChild) {
                childScale = uiChild.getCurrentScale();
            } else if (scaleToFit) {
                childScale = scaleFactor;
            }
            float w = childSize.x * childScale;
            float h = childSize.y * childScale;
            if (first) {
                scaledTotalHeight = padding + h;
                first = false;
            } else {
                scaledTotalHeight += (scaleToFit ? spacing * scaleFactor : spacing) + h;
            }
            scaledMaxWidth = Math.max(scaledMaxWidth, w);
        }

        if (first) {
            // no visible children
            scaledTotalHeight = padding * 2;
            scaledMaxWidth = padding * 2;
        } else {
            scaledTotalHeight += padding;
        }

        // Use scaled content to set final container size unless fill flags force other sizes
        if (!fillWidth) {
            finalWidth = scaledMaxWidth + padding * 2;
        }

        if (!fillHeight) {
            finalHeight = scaledTotalHeight;
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

        // Second pass: position children with correct alignment using effective scales
        float yOffset = padding;

        for (IUIElement child : children) {
            if (!child.isVisible()) continue;
            Vector2f childSize = child.getSize();
            float effectiveScale = scaleToFit ? scaleFactor : (child instanceof io.github.currenj.gelatinui.gui.UIElement uiChild ? uiChild.getCurrentScale() : 1.0f);
            float scaledChildWidth = childSize.x * effectiveScale;
            float scaledChildHeight = childSize.y * effectiveScale;
            float xOffsetLocal = 0f;

            // Apply horizontal alignment based on calculated size
            switch (alignment) {
                case CENTER:
                    float contentWidth = size.x - padding * 2;
                    xOffsetLocal = padding + (contentWidth - scaledChildWidth) / 2f;
                    break;
                case RIGHT:
                    xOffsetLocal = size.x - scaledChildWidth - padding;
                    break;
                case LEFT:
                default:
                    xOffsetLocal = padding;
                    break;
            }

            // Set child position in local coordinates (relative to this container)
            Vector2f targetPos = new Vector2f(xOffsetLocal, yOffset);
            if (animatePositions) {
                if (child instanceof UIElement<?> uiChild) {
                    uiChild.setTargetPosition(targetPos, true);
                } else {
                    child.setPosition(targetPos);
                }
            } else {
                if (child instanceof UIElement<?> uiChild) {
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

            // Update offsets with scaled height
            yOffset += scaledChildHeight + spacing * Math.min(effectiveScale, 1.0f);
        }

        layoutDirty = false;
        // After layout pass, clear animate flag so subsequent layout changes don't animate unless requested
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
    protected VBox self() {
        return this;
    }

    @Override
    protected void onUpdate(float deltaTime) {
        if (layoutDirty) {
            performLayout();
        }
    }

    @Override
    protected void renderSelf(IRenderContext context) {
        // VBox doesn't render itself, only children
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
