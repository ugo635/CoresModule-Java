package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.gui.DrawContext;

public class AfterHudRenderer extends Event{
    public DrawContext drawContext;

    public AfterHudRenderer(DrawContext drawContext) {
        this.drawContext = drawContext;
    }
}