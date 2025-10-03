package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.chat.Chat;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class mfCalc {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("mymf")
                    // Branch without argument
                    .executes(mfCalc::mfFunc)

                    // Branch with 1 argument
                    .then(ClientCommandManager.argument("mf", IntegerArgumentType.integer())
                            .executes(mfCalc::mfFunc)

                            // Branch with 2 argument
                            .then(ClientCommandManager.argument("kc", IntegerArgumentType.integer())
                                    .executes(mfCalc::mfFunc))));
        });
    }

    public static int mfFunc(CommandContext<FabricClientCommandSource> context)  {
        double mf = 0;
        double kc = 0;
        int args = 0;

        try {
            mf = IntegerArgumentType.getInteger(context, "mf");
            args++;
        } catch (IllegalArgumentException ignored) {}

        try {
            kc = IntegerArgumentType.getInteger(context, "kc");
            args++;
        } catch (IllegalArgumentException ignored) {}

        String answer = "";

        if (args == 0) {
            answer = "§cUsage: §6/mymf §b<number> §a(Go in mf set with no1 around (so legion ISNT active) with renowned armor, it will give your mf if you used shuriken, fragged dae axe with max bestiary)";
        } else {
            kc = (kc == 0 ? 1 : 1 + (kc / 100));
            mf = mf / 1.04;
            mf = ((mf*1.11/* Renowned + Legion */)*1.05 /* Shuriken */+ (130 /* Max Bestiary */ * 1.05 /* Shuriken */) * kc);
            answer = "§6§l[Cm] Your magic find is §b§l%.2f §6§lon Inquisitors".formatted(mf);
        }

        final String fanswer = answer;

        Chat.chat(fanswer);

        return 1;
    }
}
