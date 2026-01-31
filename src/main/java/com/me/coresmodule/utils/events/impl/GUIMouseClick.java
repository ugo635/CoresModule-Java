package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class GUIMouseClick extends Event {
    public double mouseX;
    public double mouseY;
    public double button;
    public Screen screen;

    public GUIMouseClick(double mouseX, double mouseY, double button, Screen screen) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
        this.screen = screen;
    }
}
