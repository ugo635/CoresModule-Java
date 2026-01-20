package com.me.coresmodule.utils.render.gui;

import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomization;
import gg.essential.universal.UScreen;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.InvocationTargetException;

import static com.me.coresmodule.CoresModule.mc;

public class GUIs {
    public static void register() {
        createCommand();
    }

    public static void createCommand() {
        Register.command("ItemCustomGUI", args -> {
            mc.send(() -> {
                UScreen.displayScreen(new ItemCustomization());
            });
        });
    }
}
