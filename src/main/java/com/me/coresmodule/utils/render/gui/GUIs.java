package com.me.coresmodule.utils.render.gui;

import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomizer.AnimatedItemComponent;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomizer.ItemCustomization;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomizer.RoundedOutlineEffect;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.UIConstraints;
import gg.essential.elementa.components.*;
import gg.essential.elementa.constraints.*;
import gg.essential.elementa.effects.Effect;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.elementa.effects.ScissorEffect;
import gg.essential.universal.UScreen;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.EnumSet;

import static com.me.coresmodule.CoresModule.mc;

public class GUIs {
    public static void register() {
        createCommand();
    }

    private static void createCommand() {
        Register.command("ItemCustomGUI", args -> {
            mc.send(() -> {
                UScreen.displayScreen(new ItemCustomization());
            });
        });
    }

    public static void addRoundedBorder(UIComponent ui, float thickness, Color color, EnumSet<RoundedOutlineEffect.Side> sides, boolean drawInside) {
        float width = ui.getWidth();
        float height = ui.getHeight();
        float rad = ui.getRadius();
        XConstraint x;
        YConstraint y;

        try {
            Field xField = UIConstraints.class.getDeclaredField("x");
            Field yField = UIConstraints.class.getDeclaredField("y");
            xField.setAccessible(true);
            yField.setAccessible(true);
            x = (XConstraint) xField.get(ui.getConstraints());
            y = (YConstraint) yField.get(ui.getConstraints());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        UIBlock decoy = (UIBlock) new UIBlock()
                .setX(x)
                .setY(y)
                .setWidth(new PixelConstraint(width))
                .setHeight(new PixelConstraint(height))
                .setColor(new Color(0, 0, 0, 0));

        UIRoundedRectangle border = (UIRoundedRectangle) new UIRoundedRectangle(rad)
                .setX(x)
                .setY(y)
                .setWidth(new PixelConstraint(width))
                .setHeight(new PixelConstraint(height))
                .setColor(new Color(0, 0, 0, 0));

        RoundedOutlineEffect outlineEffect = new RoundedOutlineEffect(decoy, color, thickness, rad * 0.75f, true, drawInside, sides);

        border.enableEffects(new ScissorEffect(), outlineEffect);

        ui.setX(new PixelConstraint(0))
                .setY(new PixelConstraint(0));

        replaceChild(ui, border);
        border.addChild(ui);
    }

    public static void addRoundedBorder(UIComponent ui, float thickness, Color color) {
        addRoundedBorder(ui, thickness, color, EnumSet.allOf(RoundedOutlineEffect.Side.class), true);
    }

    public static void addRoundedBorder(UIComponent ui, float thickness, Color color, EnumSet<RoundedOutlineEffect.Side> sides) {
        addRoundedBorder(ui, thickness, color, sides, true);
    }

    public static void addBorder(UIComponent ui, float thickness, Color color) {
        float width = ui.getWidth();
        float height = ui.getHeight();
        float rad =  ui.getRadius();
        XConstraint x;
        YConstraint y;

        try {
            Field xField = UIConstraints.class.getDeclaredField("x");
            Field yField = UIConstraints.class.getDeclaredField("y");
            xField.setAccessible(true);
            yField.setAccessible(true);
            x = (XConstraint) xField.get(ui.getConstraints());
            y = (YConstraint) yField.get(ui.getConstraints());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        UIComponent border = new UIRoundedRectangle(rad)
                .setX(x)
                .setY(y)
                .setWidth(new PixelConstraint(width))
                .setHeight(new PixelConstraint(height))
                .setColor(getShadowColor(ui));

        Effect ef = new OutlineEffect(color, thickness, true, true);
        border.enableEffects(new ScissorEffect(), ef);

        ui.setX(new PixelConstraint(0))
                .setY(new PixelConstraint(0));

        replaceChild(ui, border);
        border.addChild(ui);
    }

    public static void addShadow(UIComponent ui, float shadowSize) {
        GUIs.addRoundedBorder(ui, shadowSize, new Color (80, 80, 80, 255), EnumSet.of(
                RoundedOutlineEffect.Side.TopLeft,
                RoundedOutlineEffect.Side.Left,
                RoundedOutlineEffect.Side.Top
        ));

        GUIs.addRoundedBorder(ui, shadowSize, new Color (65, 65, 65, 255), EnumSet.of(
                RoundedOutlineEffect.Side.TopRight,
                RoundedOutlineEffect.Side.BottomLeft
        ));

        GUIs.addRoundedBorder(ui, shadowSize, new Color (25, 25, 25, 255), EnumSet.of(
                RoundedOutlineEffect.Side.BottomRight,
                RoundedOutlineEffect.Side.Right,
                RoundedOutlineEffect.Side.Bottom
        ));

    }

    public static void addShadow(UIComponent ui) {
        addShadow(ui, 2f);
    }

    private static Color getShadowColor(UIComponent element) {
        Color elementColor = element.getColor();
        Color background = element.getParent().getColor();

        float[] bgHsb = Color.RGBtoHSB(background.getRed(), background.getGreen(), background.getBlue(), null);
        float[] elHsb = Color.RGBtoHSB(elementColor.getRed(), elementColor.getGreen(), elementColor.getBlue(), null);

        boolean elementIsLighter = elHsb[2] > bgHsb[2];

        float shadowAlpha = 0.3f;
        float[] shadowHsb = elHsb.clone();

        if (elementIsLighter) {
            shadowHsb[2] = Math.max(0, elHsb[2] - 0.4f);
        } else {
            shadowHsb[2] = Math.min(1, elHsb[2] + 0.2f);
        }

        Color shadow = Color.getHSBColor(shadowHsb[0], shadowHsb[1], shadowHsb[2]);
        return new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), Math.round(shadowAlpha * 255));
    }

    public static void replaceChild(UIComponent oldChild, UIComponent newChild) {
        UIComponent parent = oldChild.getParent();
        parent.removeChild(oldChild);
        parent.addChild(newChild);
        newChild.setParent(parent);
    }

    /**
     * Creates an animated item texture component that shows the currently held item.
     * The component will automatically animate enchantment glints if the item has them.
     *
     * @return AnimatedItemComponent instance that can be refreshed
     */
    public static AnimatedItemComponent createItemTextureComponent() {
        return new AnimatedItemComponent();
    }
}