package com.me.coresmodule.utils.render.gui.guis;

import gg.essential.elementa.ElementaVersion;
import gg.essential.elementa.UIComponent;
import gg.essential.elementa.UIConstraints;
import gg.essential.elementa.components.UIBlock;
import gg.essential.elementa.WindowScreen;
import gg.essential.elementa.components.Window;
import gg.essential.elementa.constraints.CenterConstraint;
import gg.essential.elementa.constraints.PixelConstraint;
import gg.essential.universal.UScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

import static com.me.coresmodule.CoresModule.mc;
import static gg.essential.elementa.dsl.UtilitiesKt.pixels;

public class ItemCustomization extends WindowScreen {
    public ItemCustomization() {
        super(ElementaVersion.V10);
    }

    public static void OpenGUI() {
        Window window = new Window();
        UIComponent box = new UIBlock()
                .setX(new CenterConstraint())
                .setY(new PixelConstraint(10f))
                .setWidth(new PixelConstraint(20f))
                .setHeight(new PixelConstraint(36f));

        window.addChild(box);

        mc.send(() -> {
            UScreen.displayScreen(new ItemCustomization());
        });
    }

}
