package com.me.coresmodule.utils.render.gui;

import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomization;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.UIConstraints;
import gg.essential.elementa.components.GradientComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;
import gg.essential.elementa.constraints.XConstraint;
import gg.essential.elementa.constraints.YConstraint;
import gg.essential.elementa.effects.Effect;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.elementa.effects.ScissorEffect;
import gg.essential.universal.UScreen;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

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

    public static void addRoundedBorder(UIComponent ui, float thickness, Color color) {
        float width = ui. getWidth();
        float height = ui.getHeight();
        float rad = ui.getRadius();
        XConstraint x;
        YConstraint y;

        try {
            Field xField = UIConstraints.class.getDeclaredField("x");
            Field yField = UIConstraints.class. getDeclaredField("y");
            xField.setAccessible(true);
            yField.setAccessible(true);
            x = (XConstraint) xField.get(ui.getConstraints());
            y = (YConstraint) yField.get(ui.getConstraints());

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        // Create border container
        UIBlock border = (UIBlock) new UIBlock()
                .setX(x)
                .setY(y)
                .setWidth(new PixelConstraint(width))
                .setHeight(new PixelConstraint(height))
                .setColor(color); // Transparent background

        // Create the rounded outline effect with proper parameters
        RoundedOutlineEffect outlineEffect = new RoundedOutlineEffect(
                border,           // UIBlock
                color,            // outline color
                thickness,        // outline width
                rad,              // radius to match the component's radius
                true,             // drawAfterChildren - draw on top
                true,             // drawInsideChildren - draw inside bounds
                EnumSet.of(       // all sides and corners
                        RoundedOutlineEffect.Side. Left,
                        RoundedOutlineEffect.Side.Top,
                        RoundedOutlineEffect.Side.Right,
                        RoundedOutlineEffect.Side.Bottom,
                        RoundedOutlineEffect.Side.TopLeft,
                        RoundedOutlineEffect.Side. TopRight,
                        RoundedOutlineEffect.Side. BottomLeft,
                        RoundedOutlineEffect.Side. BottomRight
                )
        );

        // Enable effects on the border
        border.enableEffects(new ScissorEffect(), outlineEffect);

        // Reset original component position to be relative to border
        ui. setX(new PixelConstraint(0))
                .setY(new PixelConstraint(0));

        // Replace the component with border wrapper
        replaceChild(ui, border);
        border.addChild(ui);
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

        ui
                .setX(new PixelConstraint(0))
                .setY(new PixelConstraint(0));

        replaceChild(ui, border);
        border.addChild(ui);
    }

    // TODO: Try making it without effect to see if there's still white outline or without scissor effect
    public static void addShadow(UIComponent ui, float shadowSize) {
        float width = ui.getWidth() + shadowSize * 2;
        float height = ui.getHeight() + shadowSize * 2;
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
                .setHeight(new PixelConstraint(height));


        Color shadowColor = new Color(80 , 80, 80, 255); //getShadowColor(ui);
        ArrayList<Effect> effectList = new ArrayList<>();
        effectList.add(new ScissorEffect());
        effectList.add(new OutlineEffect(shadowColor, shadowSize, true, false, Set.of(OutlineEffect.Side.Top, OutlineEffect.Side.Left)));

        shadowColor = new Color(
                Math.round(shadowColor.getRed() * 0.25f),
                Math.round(shadowColor.getGreen() * 0.25f),
                Math.round(shadowColor.getBlue() * 0.25f),
                Math.round(shadowColor.getAlpha() * 0.6f)
        );

        effectList.add(new OutlineEffect(shadowColor, shadowSize, true, false, Set.of(OutlineEffect.Side.Bottom, OutlineEffect.Side.Right)));

        border.enableEffects(effectList.getFirst(), effectList.get(1),effectList.getLast());

        ui
            .setX(new PixelConstraint(shadowSize))
            .setY(new PixelConstraint(shadowSize));

        replaceChild(ui, border);
        border.addChild(ui);
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

}
