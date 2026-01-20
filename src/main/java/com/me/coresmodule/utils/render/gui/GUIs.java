package com.me.coresmodule.utils.render.gui;

import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomization;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.UIConstraints;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.XConstraint;
import gg.essential.elementa.constraints.YConstraint;
import gg.essential.universal.UScreen;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

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



    public static void addBorder(UIComponent ui, float thickness, Color color) {
        float width = ui.getWidth() + thickness * 2;
        float height = ui.getHeight() + thickness * 2;
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
                .setColor(color);


        ui
                .setX(new PixelConstraint(thickness))
                .setY(new PixelConstraint(thickness));

        replaceChild(ui, border);
        border.addChild(ui);

    }





    public static void replaceChild(UIComponent oldChild, UIComponent newChild) {
        UIComponent parent = oldChild.getParent();
        parent.removeChild(oldChild);
        parent.addChild(newChild);
        newChild.setParent(parent);
    }





    public static void addShadow(UIComponent ui, float shadowSize, float offsetX, float offsetY) {
        float width = ui.getWidth();
        float height = ui.getHeight();
        float rad = ui.getRadius();

        // SAVE ORIGINAL PARENT FIRST - before any modifications!
        UIComponent originalParent = ui.getParent();

        XConstraint x;
        YConstraint y;

        try {
            Field xField = UIConstraints.class.getDeclaredField("x");
            Field yField = UIConstraints.class.getDeclaredField("y");
            xField.setAccessible(true);
            yField.setAccessible(true);
            x = (XConstraint) xField.get(ui. getConstraints());
            y = (YConstraint) yField.get(ui.getConstraints());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        // Get adaptive shadow color based on element and background
        Color shadowColor = getShadowColor(ui);

        // Right shadow
        UIComponent outline = new UIRoundedRectangle(rad)
                .setX(x)
                .setY(y)
                .setWidth(new PixelConstraint(width + shadowSize))
                .setHeight(new PixelConstraint(height + shadowSize))
                .setColor(shadowColor);

        shadowColor = new Color(
                Math.round(shadowColor.getRed() * 0.25f),
                Math.round(shadowColor.getGreen() * 0.25f),
                Math.round(shadowColor.getBlue() * 0.25f),
                Math.round(shadowColor.getAlpha() * 0.6f)
        );

        // Bottom shadow
        UIComponent bottomRightShadow = new UIRoundedRectangle(rad)
                .setX(new PixelConstraint(x.getCachedValue() + offsetX))
                .setY(new PixelConstraint(y.getCachedValue() + offsetY))
                .setWidth(new PixelConstraint(width + shadowSize))
                .setHeight(new PixelConstraint(height + shadowSize))
                .setColor(shadowColor);

        // Remove UI from original parent FIRST
        originalParent.removeChild(ui);

        // Reset UI position to be relative to container
        ui
            .setX(new PixelConstraint(shadowSize))
            .setY(new PixelConstraint(shadowSize));

        // Add everything to container
        outline.addChild(ui);
        outline.addChild(bottomRightShadow);
        ui.setParent(outline);

        // Add container to original parent
        originalParent.addChild(outline);
    }



    public static void addShadow(UIComponent ui) {
        addShadow(ui, 2f, 2f, 2f);
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


}
