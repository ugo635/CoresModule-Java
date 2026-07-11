package com.me.coresmodule.utils.events.impl;

import com.me.coresmodule.utils.events.EventBus.EventBus;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class OnDisconnect extends Event {
    public ClientPacketListener handler;
    public Minecraft mc;

    public OnDisconnect(ClientPacketListener handler, Minecraft mc) {
        this.handler = handler;
        this.mc = mc;
    }

}
