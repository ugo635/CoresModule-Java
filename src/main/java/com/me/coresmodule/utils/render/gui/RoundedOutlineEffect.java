package com.me.coresmodule.utils.render. gui;

import gg.essential.elementa.components.UIBlock;
import gg.essential. elementa.effects.Effect;
import gg. essential.elementa.state.BasicState;
import gg.essential.elementa.state. MappedState;
import gg.essential.elementa.state.State;
import gg. essential.universal.UMatrixStack;
import kotlin.jvm. functions.Function1;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Set;

/**
 * Draws a basic, rectangular outline of the specified color and width around
 * this component. The outline will be drawn before this component's children are drawn,
 * so all children will render above the outline.
 */
public class RoundedOutlineEffect extends Effect {
    private final UIBlock UIBLOCK;

    private boolean hasLeft;
    private boolean hasTop;
    private boolean hasRight;
    private boolean hasBottom;

    private final MappedState<Color, Color> colorState;
    private final MappedState<Float, Float> widthState;

    public boolean drawAfterChildren;
    public boolean drawInsideChildren;
    private Set<Side> sides;

    // Main constructor
    public RoundedOutlineEffect(
            UIBlock uiBlock,
            State<Color> color,
            State<Float> width,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this.UIBLOCK = uiBlock;
        this.drawAfterChildren = drawAfterChildren;
        this.drawInsideChildren = drawInsideChildren;
        this.sides = sides;

        this.hasLeft = sides. contains(Side.Left);
        this.hasTop = sides.contains(Side.Top);
        this.hasRight = sides.contains(Side.Right);
        this.hasBottom = sides.contains(Side.Bottom);

        this.colorState = color.map((Function1<? super Color, ?  extends Color>) (Color c) -> c);
        this.widthState = width.map((Function1<? super Float, ? extends Float>) (Float w) -> w);
    }

    // Convenience constructor with Color and float
    public RoundedOutlineEffect(
            UIBlock uiBlock,
            Color color,
            float width,
            boolean drawAfterChildren,
            boolean drawInsideChildren,
            Set<Side> sides
    ) {
        this(uiBlock, new BasicState<>(color), new BasicState<>(width), drawAfterChildren, drawInsideChildren, sides);
    }

    // Overloaded constructors with defaults
    public RoundedOutlineEffect(UIBlock uiBlock, State<Color> color, State<Float> width) {
        this(uiBlock, color, width, false, false, EnumSet.of(Side.Left, Side.Top, Side.Right, Side.Bottom));
    }

    public RoundedOutlineEffect(UIBlock uiBlock, Color color, float width) {
        this(uiBlock, color, width, false, false, EnumSet.of(Side.Left, Side.Top, Side.Right, Side.Bottom));
    }

    public RoundedOutlineEffect(UIBlock uiBlock, Color color, float width, boolean drawAfterChildren, boolean drawInsideChildren) {
        this(uiBlock, color, width, drawAfterChildren, drawInsideChildren, EnumSet.of(Side.Left, Side.Top, Side.Right, Side.Bottom));
    }

    public RoundedOutlineEffect(UIBlock uiBlock, State<Color> color, State<Float> width, boolean drawAfterChildren, boolean drawInsideChildren) {
        this(uiBlock, color, width, drawAfterChildren, drawInsideChildren, EnumSet.of(Side. Left, Side.Top, Side. Right, Side.Bottom));
    }

    // Getters and setters
    public UIBlock getUIBlock() {
        return UIBLOCK;
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

    public RoundedOutlineEffect bindColor(State<Color> state) {
        colorState.rebind(state);
        return this;
    }

    public RoundedOutlineEffect bindWidth(State<Float> state) {
        widthState. rebind(state);
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
        this.hasBottom = sides.contains(Side. Bottom);
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

        double left = boundComponent.getLeft();
        double right = boundComponent. getRight();
        double top = boundComponent.getTop();
        double bottom = boundComponent.getBottom();

        // Calculate bounds for each side
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

        // Left outline block
        if (hasLeft) {
            drawBlock(matrixStack, color, leftBoundsFirst, top, leftBoundsSecond, bottom);
        }

        // Top outline block
        if (hasTop) {
            drawBlock(matrixStack, color, left, topBoundsFirst, right, topBoundsSecond);
        }

        // Right outline block
        if (hasRight) {
            drawBlock(matrixStack, color, rightBoundsFirst, top, rightBoundsSecond, bottom);
        }

        // Bottom outline block
        if (hasBottom) {
            drawBlock(matrixStack, color, left, bottomBoundsFirst, right, bottomBoundsSecond);
        }

        if (! drawInsideChildren) {
            // Top left square
            if (hasLeft && hasTop) {
                drawBlock(matrixStack, color, leftBoundsFirst, topBoundsFirst, left, top);
            }

            // Top right square
            if (hasRight && hasTop) {
                drawBlock(matrixStack, color, right, topBoundsFirst, rightBoundsSecond, top);
            }

            // Bottom right square
            if (hasRight && hasBottom) {
                drawBlock(matrixStack, color, right, bottom, rightBoundsSecond, bottomBoundsSecond);
            }

            // Bottom left square
            if (hasBottom && hasLeft) {
                drawBlock(matrixStack, color, leftBoundsFirst, bottom, left, bottomBoundsSecond);
            }
        }
    }

    private void drawBlock(UMatrixStack matrixStack, Color color, double left, double top, double right, double bottom) {
        UIBlock.Companion.drawBlock(color, left, top, right, bottom);
/*        try {
            UIBlock.class.getMethod("drawBlock$Elementa", UMatrixStack.class, double.class, double.class, double.class, double.class).invoke(this.UIBLOCK, matrixStack, left, top, right, bottom);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }*/
    }

    public enum Side {
        Left,
        Top,
        Right,
        Bottom
    }
}