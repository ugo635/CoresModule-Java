package com.me.coresmodule.utils.chat;

import com.me.coresmodule.utils.events.Register;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

import static com.me.coresmodule.CoresModule.mc;

public class SimulateChat {

    public static void register() {

        Register.command("simulateChat", args -> {
            if (args.length == 0) return;

            String message = String.join(" ", args).replace("&", "§");

            if (mc.player == null) return;

            ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(Component.literal(message), false);
            mc.player.connection.handleSystemChat(packet);
        });


    }
}

