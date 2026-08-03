package com.me.coresmodule.utils.events.impl;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

public class OnWorldJoin extends Event {
    public ClientPacketListener handler;
    public PacketSender packetSender;
    public Minecraft mc;
    public LocalPlayer player;


    public OnWorldJoin(ClientPacketListener handler, PacketSender packetSender, Minecraft mc) {
        this.handler = handler;
        this.packetSender = packetSender;
        this.mc = mc;
        this.player = mc.player;
    }
}
