package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class AfterHudRenderer extends Event {
    public GuiGraphicsExtractor graphics;

    public AfterHudRenderer(GuiGraphicsExtractor graphics) {
        this.graphics = graphics;
    }
}