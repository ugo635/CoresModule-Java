package com.me.coresmodule.utils.render.gui;

import com.me.coresmodule.utils.ItemRenderingHelper;
import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomization;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.UIConstraints;
import gg.essential.elementa.components.*;
import gg.essential.elementa.constraints.*;
import gg.essential.elementa.effects.Effect;
import gg.essential.elementa.effects.OutlineEffect;
import gg.essential.elementa.effects.ScissorEffect;
import gg.essential.universal.UScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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

    public static void addRoundedBorder(UIComponent ui, float thickness, Color color, EnumSet<RoundedOutlineEffect.Side> sides) {
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

        // Decoy to create the outline effect
        UIBlock decoy = (UIBlock) new UIBlock()
                .setX(x)
                .setY(y)
                .setWidth(new PixelConstraint(width))
                .setHeight(new PixelConstraint(height))
                .setColor(new Color(0, 0, 0, 0));

        // Main gui
        UIRoundedRectangle border = (UIRoundedRectangle) new UIRoundedRectangle(rad)
                .setX(x)
                .setY(y)
                .setWidth(new PixelConstraint(width))
                .setHeight(new PixelConstraint(height))
                .setColor(new Color(0, 0, 0, 0));

        // Create the rounded outline effect with proper parameters
        RoundedOutlineEffect outlineEffect = new RoundedOutlineEffect(decoy, color, thickness, rad * 0.75f, true, true, sides);

        border.enableEffects(new ScissorEffect(), outlineEffect);

        // Reset original component position to be relative to border
        ui.setX(new PixelConstraint(0))
                .setY(new PixelConstraint(0));

        // Replace the component with border wrapper
        replaceChild(ui, border);
        border.addChild(ui);
    }

    public static void addRoundedBorder(UIComponent ui, float thickness, Color color) {
        addRoundedBorder(ui, thickness, color, EnumSet.allOf(RoundedOutlineEffect.Side.class));
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






























    public static UIComponent createItemTextureComponent() {
        ItemRenderingHelper helper = new ItemRenderingHelper();

        // Execute on the CURRENT thread (render thread), not async
        BufferedImage image = helper.renderHeldItemToImage();

        // Wrap the already-rendered image in a completed future
        CompletableFuture<BufferedImage> imageFuture = CompletableFuture.completedFuture(image);

        // Create UIImage with the CompletableFuture
        return new UIImage(imageFuture)
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(256f))
                .setHeight(new PixelConstraint(256f));
    }

}
