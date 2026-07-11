package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class OnDisconnect extends Event {
    public ClientPlayNetworkHandler handler;
    public Minecraft mc;

    public OnDisconnect(ClientPlayNetworkHandler handler, Minecraft mc) {
        this.handler = handler;
        this.mc = mc;
    }
}
