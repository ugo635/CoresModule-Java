package com.me.coresmodule.features.Diana;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class mfCalc {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("mymf")
                    // Branch without argument
                    .executes(mfCalc::mfFunc)

                    // Branch with 1 argument
                    .then(CommandManager.argument("mf", IntegerArgumentType.integer())
                            .executes(mfCalc::mfFunc)

                            // Branch with 2 argument
                            .then(CommandManager.argument("kc", IntegerArgumentType.integer())
                                    .executes(mfCalc::mfFunc))));
        });
    }

    public static int mfFunc(CommandContext<ServerCommandSource> context)  {
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

        context.getSource().sendFeedback(() -> Text.literal(fanswer), false);

        return 1;
    }
}
