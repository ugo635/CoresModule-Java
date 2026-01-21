package com.me.coresmodule.utils. render.gui;

import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential. elementa.components.UIRoundedRectangle;
import gg.essential.elementa.effects.Effect;
import gg.essential. elementa.state.BasicState;
import gg.essential.elementa.state.MappedState;
import gg.essential.elementa.state.State;
import gg.essential.universal.UMatrixStack;
import kotlin.jvm.functions.Function1;

import java.awt. Color;
import java.util.EnumSet;
import java.util.Set;

/**
 * Draws a rounded outline around the component, following any corner curves.
 */
public class RoundedOutlineEffect extends Effect {
    private final UIComponent UICOMPONENT;

    private boolean hasLeft;
    private boolean hasTop;
    private boolean hasRight;
    private boolean hasBottom;
    private boolean hasTopLeft;
    private boolean hasTopRight;
    private boolean hasBottomLeft;
    private boolean hasBottomRight;

    private final MappedState<Color, Color> colorState;
    private final MappedState<Float, Float> widthState;
    private final MappedState<Float, Float> radiusState;

    public boolean drawAfterChildren;
    public boolean drawInsideChildren;
    private Set<Side> sides;

    // Main constructor with radius support (accepts UIComponent)
    public RoundedOutlineEffect(
            UIComponent uiComponent,
            State<Color> color,
            State<Float> width,
            State<Float> radius,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this. UICOMPONENT = uiComponent;
        this.drawAfterChildren = drawAfterChildren;
        this.drawInsideChildren = drawInsideChildren;
        this.sides = sides;

        updateSideFlags();

        this.colorState = color. map((Function1<?  super Color, ? extends Color>) (Color c) -> c);
        this.widthState = width.map((Function1<? super Float, ? extends Float>) (Float w) -> w);
        this.radiusState = radius.map((Function1<? super Float, ?  extends Float>) (Float r) -> r);
    }

    // ============= UIRoundedRectangle constructors (auto-detect radius) =============

    // Auto-detect radius from UIRoundedRectangle
    public RoundedOutlineEffect(
            UIRoundedRectangle uiRoundedRect,
            State<Color> color,
            State<Float> width,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this((UIComponent) uiRoundedRect, color, width, new BasicState<>(uiRoundedRect. getRadius()),
                drawAfterChildren, drawInsideChildren, sides);
    }

    public RoundedOutlineEffect(
            UIRoundedRectangle uiRoundedRect,
            Color color,
            float width,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this((UIComponent) uiRoundedRect, new BasicState<>(color), new BasicState<>(width),
                new BasicState<>(uiRoundedRect.getRadius()), drawAfterChildren, drawInsideChildren, sides);
    }

    public RoundedOutlineEffect(UIRoundedRectangle uiRoundedRect, State<Color> color, State<Float> width) {
        this(uiRoundedRect, color, width, false, false, getAllSides());
    }

    public RoundedOutlineEffect(UIRoundedRectangle uiRoundedRect, Color color, float width) {
        this(uiRoundedRect, color, width, false, false, getAllSides());
    }

    public RoundedOutlineEffect(UIRoundedRectangle uiRoundedRect, Color color, float width,
                                boolean drawAfterChildren, boolean drawInsideChildren) {
        this(uiRoundedRect, color, width, drawAfterChildren, drawInsideChildren, getAllSides());
    }

    // ============= UIBlock constructors (manual radius) =============

    public RoundedOutlineEffect(
            UIBlock uiBlock,
            Color color,
            float width,
            float radius,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this((UIComponent) uiBlock, new BasicState<>(color), new BasicState<>(width), new BasicState<>(radius),
                drawAfterChildren, drawInsideChildren, sides);
    }

    public RoundedOutlineEffect(UIBlock uiBlock, State<Color> color, State<Float> width, State<Float> radius) {
        this((UIComponent) uiBlock, color, width, radius, false, false, getAllSides());
    }

    public RoundedOutlineEffect(UIBlock uiBlock, Color color, float width, float radius) {
        this((UIComponent) uiBlock, color, width, radius, false, false, getAllSides());
    }

    public RoundedOutlineEffect(UIBlock uiBlock, Color color, float width) {
        this((UIComponent) uiBlock, color, width, 0f, false, false, getAllSides());
    }

    public RoundedOutlineEffect(UIBlock uiBlock, Color color, float width,
                                boolean drawAfterChildren, boolean drawInsideChildren) {
        this((UIComponent) uiBlock, color, width, 0f, drawAfterChildren, drawInsideChildren, getAllSides());
    }

    // ============= Generic UIComponent constructors =============

    public RoundedOutlineEffect(
            UIComponent uiComponent,
            Color color,
            float width,
            float radius,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this(uiComponent, new BasicState<>(color), new BasicState<>(width), new BasicState<>(radius),
                drawAfterChildren, drawInsideChildren, sides);
    }

    public RoundedOutlineEffect(UIComponent uiComponent, Color color, float width, float radius) {
        this(uiComponent, color, width, radius, false, false, getAllSides());
    }

    public RoundedOutlineEffect(UIComponent uiComponent, Color color, float width) {
        this(uiComponent, color, width, 0f, false, false, getAllSides());
    }

    private static Set<Side> getAllSides() {
        return EnumSet.of(Side.Left, Side.Top, Side.Right, Side.Bottom,
                Side.TopLeft, Side.TopRight, Side.BottomLeft, Side.BottomRight);
    }

    // Getters and setters
    public UIComponent getUIComponent() {
        return UICOMPONENT;
    }

    public Color getColor() {
        return colorState.get();
    }

    public void setColor(Color color) {
        colorState.set(color);
    }

    public float getWidth() {
        return widthState.get();
    }

    public void setWidth(float width) {
        widthState.set(width);
    }

    public float getRadius() {
        return radiusState.get();
    }

    public void setRadius(float radius) {
        radiusState.set(radius);
    }

    public RoundedOutlineEffect bindColor(State<Color> state) {
        colorState.rebind(state);
        return this;
    }

    public RoundedOutlineEffect bindWidth(State<Float> state) {
        widthState.rebind(state);
        return this;
    }

    public RoundedOutlineEffect bindRadius(State<Float> state) {
        radiusState.rebind(state);
        return this;
    }

    public Set<Side> getSides() {
        return sides;
    }

    public void setSides(Set<Side> sides) {
        this.sides = sides;
        updateSideFlags();
    }

    private void updateSideFlags() {
        this.hasLeft = sides.contains(Side.Left);
        this.hasTop = sides.contains(Side.Top);
        this.hasRight = sides.contains(Side. Right);
        this.hasBottom = sides.contains(Side.Bottom);
        this.hasTopLeft = sides.contains(Side.TopLeft);
        this.hasTopRight = sides.contains(Side. TopRight);
        this.hasBottomLeft = sides.contains(Side.BottomLeft);
        this.hasBottomRight = sides.contains(Side.BottomRight);
    }

    public RoundedOutlineEffect addSide(Side side) {
        Set<Side> newSides = EnumSet.copyOf(sides);
        newSides.add(side);
        setSides(newSides);
        return this;
    }

    public RoundedOutlineEffect removeSide(Side side) {
        Set<Side> newSides = EnumSet.copyOf(sides);
        newSides.remove(side);
        setSides(newSides);
        return this;
    }

    @Override
    public void beforeChildrenDraw(UMatrixStack matrixStack) {
        if (!drawAfterChildren) {
            drawOutline(matrixStack);
        }
    }

    @Override
    public void afterDraw(UMatrixStack matrixStack) {
        if (drawAfterChildren) {
            drawOutline(matrixStack);
        }
    }

    private void drawOutline(UMatrixStack matrixStack) {
        Color color = colorState.get();
        float width = widthState.get();
        float radius = radiusState. get();

        double left = boundComponent. getLeft();
        double right = boundComponent.getRight();
        double top = boundComponent.getTop();
        double bottom = boundComponent.getBottom();

        if (radius <= 0) {
            // Fall back to rectangular outline
            drawRectangularOutline(matrixStack, color, width, left, right, top, bottom);
        } else {
            // Draw rounded outline
            drawRoundedOutline(matrixStack, color, width, radius, left, right, top, bottom);
        }
    }

    private void drawRectangularOutline(UMatrixStack matrixStack, Color color, float width,
                                        double left, double right, double top, double bottom) {
        // Original rectangular implementation
        double leftBoundsFirst, leftBoundsSecond;
        if (drawInsideChildren) {
            leftBoundsFirst = left;
            leftBoundsSecond = left + width;
        } else {
            leftBoundsFirst = left - width;
            leftBoundsSecond = left;
        }

        double topBoundsFirst, topBoundsSecond;
        if (drawInsideChildren) {
            topBoundsFirst = top;
            topBoundsSecond = top + width;
        } else {
            topBoundsFirst = top - width;
            topBoundsSecond = top;
        }

        double rightBoundsFirst, rightBoundsSecond;
        if (drawInsideChildren) {
            rightBoundsFirst = right - width;
            rightBoundsSecond = right;
        } else {
            rightBoundsFirst = right;
            rightBoundsSecond = right + width;
        }

        double bottomBoundsFirst, bottomBoundsSecond;
        if (drawInsideChildren) {
            bottomBoundsFirst = bottom - width;
            bottomBoundsSecond = bottom;
        } else {
            bottomBoundsFirst = bottom;
            bottomBoundsSecond = bottom + width;
        }

        if (hasLeft) {
            UIBlock. Companion.drawBlock(matrixStack, color, leftBoundsFirst, top, leftBoundsSecond, bottom);
        }

        if (hasTop) {
            UIBlock. Companion.drawBlock(matrixStack, color, left, topBoundsFirst, right, topBoundsSecond);
        }

        if (hasRight) {
            UIBlock. Companion.drawBlock(matrixStack, color, rightBoundsFirst, top, rightBoundsSecond, bottom);
        }

        if (hasBottom) {
            UIBlock. Companion.drawBlock(matrixStack, color, left, bottomBoundsFirst, right, bottomBoundsSecond);
        }

        if (! drawInsideChildren) {
            // Top left corner
            if (hasTopLeft || (hasLeft && hasTop)) {
                UIBlock.Companion.drawBlock(matrixStack, color, leftBoundsFirst, topBoundsFirst, left, top);
            }

            // Top right corner
            if (hasTopRight || (hasRight && hasTop)) {
                UIBlock. Companion.drawBlock(matrixStack, color, right, topBoundsFirst, rightBoundsSecond, top);
            }

            // Bottom right corner
            if (hasBottomRight || (hasRight && hasBottom)) {
                UIBlock. Companion.drawBlock(matrixStack, color, right, bottom, rightBoundsSecond, bottomBoundsSecond);
            }

            // Bottom left corner
            if (hasBottomLeft || (hasBottom && hasLeft)) {
                UIBlock.Companion.drawBlock(matrixStack, color, leftBoundsFirst, bottom, left, bottomBoundsSecond);
            }
        }
    }

    private void drawRoundedOutline(UMatrixStack matrixStack, Color color, float width, float radius,
                                    double left, double right, double top, double bottom) {
        // Adjust radius and width based on draw position
        double outerRadius, innerRadius;
        if (drawInsideChildren) {
            outerRadius = radius;
            innerRadius = Math.max(0, radius - width);
        } else {
            outerRadius = radius + width;
            innerRadius = radius;
        }

        double componentWidth = right - left;
        double componentHeight = bottom - top;

        // Ensure radius doesn't exceed half the component size
        outerRadius = Math.min(outerRadius, Math.min(componentWidth, componentHeight) / 2.0);
        innerRadius = Math.min(innerRadius, Math.min(componentWidth, componentHeight) / 2.0);

        // Draw the four sides (excluding corners)
        if (hasTop) {
            UIBlock. Companion.drawBlock(matrixStack, color,
                    left + outerRadius, top - (drawInsideChildren ? 0 :  width),
                    right - outerRadius, top + (drawInsideChildren ? width : 0));
        }

        if (hasBottom) {
            UIBlock. Companion.drawBlock(matrixStack, color,
                    left + outerRadius, bottom - (drawInsideChildren ? width : 0),
                    right - outerRadius, bottom + (drawInsideChildren ? 0 : width));
        }

        if (hasLeft) {
            UIBlock.Companion.drawBlock(matrixStack, color,
                    left - (drawInsideChildren ? 0 : width), top + outerRadius,
                    left + (drawInsideChildren ? width :  0), bottom - outerRadius);
        }

        if (hasRight) {
            UIBlock.Companion. drawBlock(matrixStack, color,
                    right - (drawInsideChildren ? width : 0), top + outerRadius,
                    right + (drawInsideChildren ? 0 : width), bottom - outerRadius);
        }

        // Draw the four rounded corners using small blocks to approximate the arc
        int segments = Math.max(16, (int) (outerRadius / 1.5)); // More segments for smoother curves

        if (hasTopLeft || (hasTop && hasLeft)) {
            drawRoundedCorner(matrixStack, color, left + outerRadius, top + outerRadius,
                    innerRadius, outerRadius, 180, 270, segments);
        }

        if (hasTopRight || (hasTop && hasRight)) {
            drawRoundedCorner(matrixStack, color, right - outerRadius, top + outerRadius,
                    innerRadius, outerRadius, 270, 360, segments);
        }

        if (hasBottomRight || (hasBottom && hasRight)) {
            drawRoundedCorner(matrixStack, color, right - outerRadius, bottom - outerRadius,
                    innerRadius, outerRadius, 0, 90, segments);
        }

        if (hasBottomLeft || (hasBottom && hasLeft)) {
            drawRoundedCorner(matrixStack, color, left + outerRadius, bottom - outerRadius,
                    innerRadius, outerRadius, 90, 180, segments);
        }
    }

    private void drawRoundedCorner(UMatrixStack matrixStack, Color color,
                                   double centerX, double centerY,
                                   double innerRadius, double outerRadius,
                                   double startAngle, double endAngle, int segments) {
        double angleStep = (endAngle - startAngle) / segments;

        // Draw small rectangles along the arc to approximate the rounded corner
        for (int i = 0; i < segments; i++) {
            double angle1 = Math.toRadians(startAngle + i * angleStep);
            double angle2 = Math.toRadians(startAngle + (i + 1) * angleStep);

            // Calculate points on inner and outer radius
            double x1Inner = centerX + Math.cos(angle1) * innerRadius;
            double y1Inner = centerY + Math.sin(angle1) * innerRadius;
            double x1Outer = centerX + Math.cos(angle1) * outerRadius;
            double y1Outer = centerY + Math.sin(angle1) * outerRadius;

            double x2Inner = centerX + Math.cos(angle2) * innerRadius;
            double y2Inner = centerY + Math. sin(angle2) * innerRadius;
            double x2Outer = centerX + Math.cos(angle2) * outerRadius;
            double y2Outer = centerY + Math.sin(angle2) * outerRadius;

            // Calculate bounding box for this segment
            double minX = Math. min(Math.min(x1Inner, x1Outer), Math.min(x2Inner, x2Outer));
            double maxX = Math.max(Math.max(x1Inner, x1Outer), Math.max(x2Inner, x2Outer));
            double minY = Math.min(Math.min(y1Inner, y1Outer), Math.min(y2Inner, y2Outer));
            double maxY = Math.max(Math.max(y1Inner, y1Outer), Math.max(y2Inner, y2Outer));

            // Draw a small block for this arc segment using UIBlock. drawBlock
            UIBlock.Companion.drawBlock(matrixStack, color, minX, minY, maxX, maxY);
        }
    }

    public enum Side {
        Left,
        Top,
        Right,
        Bottom,
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight
    }
}