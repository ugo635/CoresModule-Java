package com.me.coresmodule.utils. render.gui;

import gg.essential.elementa.state. BasicState;
import gg. essential.elementa.state.MappedState;
import gg.essential.elementa.state. State;
import gg.essential.universal.UGraphics;
import gg.essential.universal. UMatrixStack;
import gg.essential.elementa.effects.Effect;
import kotlin.jvm.functions.Function1;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Set;

/**
 * Draws a rounded outline that follows the shape of UIRoundedRectangle components.
 */
public class RoundedOutlineEffect extends Effect {
    private boolean hasLeft;
    private boolean hasTop;
    private boolean hasRight;
    private boolean hasBottom;

    private final MappedState<Color, Color> colorState;
    private final MappedState<Float, Float> widthState;

    public boolean drawAfterChildren;
    public boolean drawInsideChildren;
    private Set<Side> sides;
    private int segments = 16; // Number of segments for rounded corners

    // Main constructor
    public RoundedOutlineEffect(
            State<Color> color,
            State<Float> width,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this.drawAfterChildren = drawAfterChildren;
        this.drawInsideChildren = drawInsideChildren;

        this.hasLeft = sides. contains(Side.Left);
        this.hasTop = sides.contains(Side.Top);
        this.hasRight = sides.contains(Side.Right);
        this.hasBottom = sides.contains(Side.Bottom);

        this.colorState = color.map((Function1<? super Color, ? extends Color>) (Color c) -> c);
        this.widthState = width.map((Function1<?  super Float, ? extends Float>) (Float w) -> w);
        this.sides = sides;
    }

    // Convenience constructor with Color and float
    public RoundedOutlineEffect(
            Color color,
            float width,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this(new BasicState<>(color), new BasicState<>(width), drawAfterChildren, drawInsideChildren, sides);
    }

    // Overloaded constructors
    public RoundedOutlineEffect(State<Color> color, State<Float> width) {
        this(color, width, false, false, EnumSet.of(Side. Left, Side.Top, Side. Right, Side.Bottom));
    }

    public RoundedOutlineEffect(Color color, float width) {
        this(color, width, false, false, EnumSet.of(Side.Left, Side.Top, Side.Right, Side.Bottom));
    }

    public RoundedOutlineEffect(Color color, float width, boolean drawAfterChildren, boolean drawInsideChildren) {
        this(color, width, drawAfterChildren, drawInsideChildren, EnumSet.of(Side.Left, Side.Top, Side.Right, Side.Bottom));
    }

    // Getters and setters
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

    public RoundedOutlineEffect bindColor(State<Color> state) {
        colorState.rebind(state);
        return this;
    }

    public RoundedOutlineEffect bindWidth(State<Float> state) {
        widthState.rebind(state);
        return this;
    }

    public Set<Side> getSides() {
        return sides;
    }

    public void setSides(Set<Side> sides) {
        this.sides = sides;
        this.hasLeft = sides.contains(Side.Left);
        this.hasTop = sides.contains(Side.Top);
        this.hasRight = sides.contains(Side.Right);
        this.hasBottom = sides. contains(Side.Bottom);
    }

    public RoundedOutlineEffect addSide(Side side) {
        Set<Side> newSides = EnumSet. copyOf(sides);
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

    public void setSegments(int segments) {
        this.segments = Math.max(4, segments);
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

        double left = boundComponent.getLeft();
        double right = boundComponent.getRight();
        double top = boundComponent.getTop();
        double bottom = boundComponent.getBottom();
        float radius = boundComponent.getRadius();

        // Clamp radius to not exceed half of the smallest dimension
        float maxRadius = (float) Math.min(right - left, bottom - top) / 2f;
        radius = Math.min(radius, maxRadius);

        if (radius <= 0) {
            // No rounding, use square outline
            drawSquareOutline(matrixStack, color, width, left, right, top, bottom);
            return;
        }

        // Draw rounded outline
        drawRoundedOutline(matrixStack, color, width, left, right, top, bottom, radius);
    }

    private void drawSquareOutline(UMatrixStack matrixStack, Color color, float width,
                                   double left, double right, double top, double bottom) {
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
            drawBlock(matrixStack, color, leftBoundsFirst, top, leftBoundsSecond, bottom);
        }

        if (hasTop) {
            drawBlock(matrixStack, color, left, topBoundsFirst, right, topBoundsSecond);
        }

        if (hasRight) {
            drawBlock(matrixStack, color, rightBoundsFirst, top, rightBoundsSecond, bottom);
        }

        if (hasBottom) {
            drawBlock(matrixStack, color, left, bottomBoundsFirst, right, bottomBoundsSecond);
        }

        if (!drawInsideChildren) {
            if (hasLeft && hasTop) {
                drawBlock(matrixStack, color, leftBoundsFirst, topBoundsFirst, left, top);
            }
            if (hasRight && hasTop) {
                drawBlock(matrixStack, color, right, topBoundsFirst, rightBoundsSecond, top);
            }
            if (hasRight && hasBottom) {
                drawBlock(matrixStack, color, right, bottom, rightBoundsSecond, bottomBoundsSecond);
            }
            if (hasBottom && hasLeft) {
                drawBlock(matrixStack, color, leftBoundsFirst, bottom, left, bottomBoundsSecond);
            }
        }
    }

    private void drawRoundedOutline(UMatrixStack matrixStack, Color color, float width,
                                    double left, double right, double top, double bottom, float radius) {
        // Calculate inner and outer radii
        float outerRadius, innerRadius;
        if (drawInsideChildren) {
            outerRadius = radius;
            innerRadius = Math.max(0, radius - width);
        } else {
            outerRadius = radius + width;
            innerRadius = radius;
        }

        // Calculate the straight edge positions
        double leftEdge = drawInsideChildren ? left : left - width;
        double rightEdge = drawInsideChildren ? right :  right + width;
        double topEdge = drawInsideChildren ?  top : top - width;
        double bottomEdge = drawInsideChildren ? bottom : bottom + width;

        // Draw the four straight edges (excluding corners)
        if (hasLeft) {
            drawBlock(matrixStack, color, leftEdge, top + radius, leftEdge + width, bottom - radius);
        }

        if (hasTop) {
            drawBlock(matrixStack, color, left + radius, topEdge, right - radius, topEdge + width);
        }

        if (hasRight) {
            drawBlock(matrixStack, color, rightEdge - width, top + radius, rightEdge, bottom - radius);
        }

        if (hasBottom) {
            drawBlock(matrixStack, color, left + radius, bottomEdge - width, right - radius, bottomEdge);
        }

        // Draw the four rounded corners
        if (hasLeft && hasTop) {
            drawRoundedCorner(matrixStack, color, left + radius, top + radius, innerRadius, outerRadius, 180, 270);
        }

        if (hasRight && hasTop) {
            drawRoundedCorner(matrixStack, color, right - radius, top + radius, innerRadius, outerRadius, 270, 360);
        }

        if (hasRight && hasBottom) {
            drawRoundedCorner(matrixStack, color, right - radius, bottom - radius, innerRadius, outerRadius, 0, 90);
        }

        if (hasLeft && hasBottom) {
            drawRoundedCorner(matrixStack, color, left + radius, bottom - radius, innerRadius, outerRadius, 90, 180);
        }
    }

    private void drawRoundedCorner(UMatrixStack matrixStack, Color color,
                                   double centerX, double centerY,
                                   float innerRadius, float outerRadius,
                                   float startAngle, float endAngle) {
        UGraphics.enableBlend();
        UGraphics.tryBlendFuncSeparate(770, 771, 1, 0);

        UGraphics buffer = UGraphics.getFromTessellator();
        buffer.beginWithDefaultShader(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats. POSITION_COLOR);

        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        float alpha = color.getAlpha() / 255f;

        int segmentCount = segments / 4; // Segments per corner
        float angleStep = (endAngle - startAngle) / segmentCount;

        for (int i = 0; i < segmentCount; i++) {
            float angle1 = (float) Math.toRadians(startAngle + i * angleStep);
            float angle2 = (float) Math.toRadians(startAngle + (i + 1) * angleStep);

            // Outer arc points
            double x1Outer = centerX + Math.cos(angle1) * outerRadius;
            double y1Outer = centerY + Math. sin(angle1) * outerRadius;
            double x2Outer = centerX + Math.cos(angle2) * outerRadius;
            double y2Outer = centerY + Math.sin(angle2) * outerRadius;

            // Inner arc points
            double x1Inner = centerX + Math.cos(angle1) * innerRadius;
            double y1Inner = centerY + Math.sin(angle1) * innerRadius;
            double x2Inner = centerX + Math.cos(angle2) * innerRadius;
            double y2Inner = centerY + Math.sin(angle2) * innerRadius;

            // Draw quad for this segment
            buffer.pos(matrixStack, x1Inner, y1Inner, 0.0).color(red, green, blue, alpha).endVertex();
            buffer.pos(matrixStack, x2Inner, y2Inner, 0.0).color(red, green, blue, alpha).endVertex();
            buffer.pos(matrixStack, x2Outer, y2Outer, 0.0).color(red, green, blue, alpha).endVertex();
            buffer.pos(matrixStack, x1Outer, y1Outer, 0.0).color(red, green, blue, alpha).endVertex();
        }

        buffer.drawDirect();
        UGraphics. disableBlend();
    }

    public enum Side {
        Left,
        Top,
        Right,
        Bottom
    }

    @SuppressWarnings("deprecation")
    private void drawBlock(UMatrixStack matrixStack, Color color, double x1, double y1, double x2, double y2) {
        UGraphics.enableBlend();
        UGraphics.tryBlendFuncSeparate(770, 771, 1, 0);

        UGraphics buffer = UGraphics.getFromTessellator();
        buffer.beginWithDefaultShader(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_COLOR);

        float red = color.getRed() / 255f;
        float green = color.getGreen() / 255f;
        float blue = color.getBlue() / 255f;
        float alpha = color.getAlpha() / 255f;

        buffer. pos(matrixStack, x1, y2, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrixStack, x2, y2, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrixStack, x2, y1, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.pos(matrixStack, x1, y1, 0.0).color(red, green, blue, alpha).endVertex();

        buffer.drawDirect();
        UGraphics.disableBlend();
    }
}