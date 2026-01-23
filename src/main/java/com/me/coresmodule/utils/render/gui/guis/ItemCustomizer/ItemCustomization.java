package com.me.coresmodule.utils.render.gui.guis.ItemCustomizer;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.input.UITextInput;
import gg.essential.elementa.constraints.*;
import com.me.coresmodule.utils.render.gui.GUIs;
import java.awt.*;

public class ItemCustomization extends WindowScreen {

    public ItemCustomization() {
        super(ElementaVersion.V10, true, false);

        // Main container
        UIComponent main = new UIRoundedRectangle(7.5f)
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(325f))
                .setHeight(new PixelConstraint(400f))
                .setColor(new Color(50, 50, 50, 255))
                .setChildOf(getWindow());

        // Titles
        UIComponent titleBox = new UIRoundedRectangle(7.5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(12.5f))
                .setWidth(new PixelConstraint(260f))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(40, 40, 40, 255))
                .setChildOf(main);

        UIText titleText = (UIText) new UIText("CoresModule Item Customization")
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setTextScale(new PixelConstraint(1.5f))
                .setColor(new Color(145, 5, 145, 255))
                .setChildOf(titleBox);

        GUIs.addRoundedBorder(titleBox, 1.5f, new Color(80, 80, 80, 255));

        // Inputs
        UIComponent inputBox = new UIRoundedRectangle(5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(50f))
                .setWidth(new PixelConstraint(200f))
                .setHeight(new PixelConstraint(25f))
                .setColor(new Color(40, 40, 40, 255))
                .setChildOf(main);

        UITextInput input = (UITextInput) new UITextInput("Enter item name...")
                .setX(new PixelConstraint(5f))
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(190f))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(255, 255, 255, 255))
                .setChildOf(inputBox);

        input.onMouseClick((inp, ignored) -> {
            input.grabWindowFocus();
            return null;
        });

        GUIs.addBorder(inputBox, 1.5f, Color.WHITE);

        // Item Texture Display
        UIComponent itemDisplayBox = new UIBlock()
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(100f))
                .setWidth(new PixelConstraint(260f))
                .setHeight(new PixelConstraint(260f))
                .setColor(new Color(40, 40, 40, 255))
                .setChildOf(main);

        // Initialize and add the animated component
        AnimatedItemComponent itemDisplay = new AnimatedItemComponent();
        itemDisplay.setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(256f))
                .setHeight(new PixelConstraint(256f))
                .setChildOf(itemDisplayBox);
    }
}