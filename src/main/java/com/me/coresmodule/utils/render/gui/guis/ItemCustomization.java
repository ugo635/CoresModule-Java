package com.me.coresmodule.utils.render.gui.guis;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.UIRoundedRectangle;
import gg.essential.elementa.components.UIText;
import gg.essential.elementa.components.input.UITextInput;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.elementa.constraints.RelativeConstraint;

import java.awt.*;

public class ItemCustomization extends WindowScreen {

    public ItemCustomization() {
        super(ElementaVersion.V10, true, false);

        UIComponent main = new UIRoundedRectangle(7.5f)
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setWidth(new PixelConstraint(325f))
                .setHeight(new PixelConstraint(400f))
                .setColor(new Color(50, 50, 50, 255))
                .setChildOf(getWindow());

        UIComponent titleBox = new UIRoundedRectangle(7.5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(12.5f))
                .setWidth(new PixelConstraint(0.8f * main.getWidth()))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(40, 40, 40, 255));

        UIText titleText = (UIText) new UIText()
                .setText("CoresModule Item Customization")
                .setX(new CenterConstraint())
                .setY(new CenterConstraint())
                .setTextScale(new PixelConstraint(1.5f))
                .setColor(new Color(145, 5, 145, 255));

        UIComponent border = new UIRoundedRectangle(5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(50f))
                .setWidth(new PixelConstraint(205f))
                .setHeight(new PixelConstraint(30f))
                .setColor(Color.WHITE);

        UIComponent inputBox = new UIRoundedRectangle(5f)
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(50f))
                .setWidth(new PixelConstraint(200f))
                .setHeight(new PixelConstraint(25f))
                .setColor(new Color(40, 40, 40, 255));

        UITextInput input = (UITextInput) new UITextInput()
                .setX(new PixelConstraint(new RelativeConstraint().getValue() + 5f))
                .setY(new PixelConstraint(7.5f))
                .setWidth(new PixelConstraint(200f))
                .setHeight(new PixelConstraint(20f))
                .setColor(new Color(255, 255, 255, 255));

        input.setPlaceholder("Enter item name...");
        input.onMouseClick((inp, ignored) -> {
            input.grabWindowFocus();
            return null;
        });




        //main.onMouseEnterRunnable(() -> {
        //
        //});



        // <div>

        // <h1> Item Customization </h1>
        main.addChild(titleBox);
        titleBox.addChild(titleText);

        // <div>
        // <input type="text" placeholder="Enter item name"/>
        main.addChild(border);
        main.addChild(inputBox);
        inputBox.addChild(input);
        //addBorder(inputBox, 2.5f, Color.WHITE);
        // </div>

        // </div>
    }

    public static void addBorder(UIComponent ui, float thickness, Color color) {
        ui.getRight()
        UIComponent border = new UIBlock()
                .setWidth(new PixelConstraint(ui.getWidth() + thickness))
                .setHeight(new PixelConstraint(ui.getHeight() + thickness))
                .setColor(color);

        var parent = ui.getParent();
        parent.removeChild(ui);
        parent.addChild(border);
        border.addChild(ui);

    }

}
