package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class OnDisconnect extends Event {
    public ClientPlayNetworkHandler handler;
    public MinecraftClient mc;

    public OnDisconnect(ClientPlayNetworkHandler handler, MinecraftClient mc) {
        this.handler = handler;
        this.mc = mc;
    }
}
