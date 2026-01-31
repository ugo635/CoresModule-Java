package com.me.coresmodule.utils.chat;

import com.me.coresmodule.utils.events.Register;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;

public class SimulateChat {

    public static void register() {

        Register.command("simulateChat", args -> {
            if (args.length == 0) return;

            String message = String.join(" ", args).replace("&", "§");

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.player.networkHandler == null) return;

            GameMessageS2CPacket packet = new GameMessageS2CPacket(Text.of(message), false);
            client.player.networkHandler.onGameMessage(packet);
        });


    }
}

