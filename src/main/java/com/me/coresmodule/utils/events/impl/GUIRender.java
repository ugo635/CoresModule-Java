package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

/**
 * Emmited when a GUI renders, ex if the screen is the Inventory, then when the inventory will open, GUIRender will be emmited.
 */
public class GUIRender extends Event {
    public GuiGraphicsExtractor graphics;
    public Screen screen;
    public double mouseX;
    public double mouseY;
    public float tickDelta;

    public GUIRender(GuiGraphicsExtractor graphics, Screen screen, double mouseX, double mouseY, float tickDelta) {
        this.screen = screen;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.tickDelta = tickDelta;
        this.graphics = graphics;
    }
}
