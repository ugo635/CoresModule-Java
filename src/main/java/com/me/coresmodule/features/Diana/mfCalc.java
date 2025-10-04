package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class mfCalc {
    public static void register() {
        Register.command("mymf", args -> {
            if (args.length > 2 || args.length == 0) {
                Chat.chat("§cUsage: §6/mymf §b<number> §a(Go in mf set with no1 around (so legion ISNT active) with renowned armor, it will give your mf if you used shuriken, fragged dae axe with max bestiary)");
            } else {
                double mf = Double.parseDouble(args[0]);
                double kc = (args.length == 1 ? 0 : Double.parseDouble(args[1]));
                kc = (kc == 0 ? 1 : 1 + (kc / 100));
                mf = mf / 1.04;
                mf = ((mf*1.11/* Renowned + Legion */)*1.05 /* Shuriken */+ (130 /* Max Bestiary */ * 1.05 /* Shuriken */) * kc);
                Chat.chat("§6§l[Cm] Your magic find is §b§l%.2f §6§lon Inquisitors".formatted(mf));
            }
        });
    }
}
