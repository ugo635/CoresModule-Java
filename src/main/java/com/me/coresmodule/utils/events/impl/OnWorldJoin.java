package com.me.coresmodule.utils.events.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class OnWorldJoin extends Event {
    public Player player;

    public OnWorldJoin(Player player) {
        this.player = player;
    }
}
