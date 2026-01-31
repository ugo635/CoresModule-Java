package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class GUIRender extends Event {
    public DrawContext drawContext;
    public Screen screen;
    public double mouseX;
    public double mouseY;
    public float tickDelta;

    public GUIRender(DrawContext drawContext, Screen screen, double mouseX, double mouseY, float tickDelta) {
        this.screen = screen;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.tickDelta = tickDelta;
        this.drawContext = drawContext;
    }
}
