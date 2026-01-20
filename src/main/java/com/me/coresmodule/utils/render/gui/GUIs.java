package com.me.coresmodule.utils.render.gui;

import com.me.coresmodule.utils.events.Register;
import com.me.coresmodule.utils.render.gui.guis.ItemCustomization;

import java.lang.reflect.InvocationTargetException;

public class GUIs {
    public static void register() {
        createCommand(ItemCustomization.class);
    }

    public static void createCommand(Class<?> clazz) {
        Register.command(clazz.getName(), args -> {
            try {
                clazz.getMethod("OpenGUI").invoke(null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

        });
    }
}
