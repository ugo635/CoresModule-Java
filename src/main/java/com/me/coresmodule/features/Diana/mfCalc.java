package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mfCalc {
    public static void register() {
        Register.command("mymf", args -> {
            if (args.length > 2 || args.length == 0) {
                Chat.chat("§cUsage: §6/mymf §b<number> §a(Go in mf set with no1 around (so legion ISNT active) with renowned armor, it will give your mf if you used shuriken, fragged dae axe with max bestiary)");
            } else {
                double mf = Double.parseDouble(args[0]);
                double kc = (args.length == 1 ? 0 : Double.parseDouble(args[1]));
                Chat.chat("§6§l[Cm] Your magic find is §b§l%.2f §6§lon Inquisitors".formatted(mymf(mf, kc)));
            }
        });

        Register.onChatMessage(message -> {
            Pattern regexWith2Numbers = Pattern.compile("^Party > \\[?[^\\]]*\\]?\\s*(\\w+): !mymf (\\d+) (\\d+)$"); // Matches "!mymf <number> <number>"
            Pattern regexWithNumber = Pattern.compile("^Party > \\[?[^\\]]*\\]?\\s*(\\w+): !mymf (\\d+)$"); // Matches "!mymf <number>"
            Pattern regexWithoutNumber = Pattern.compile("^Party > \\[?[^\\]]*\\]?\\s*(\\w+): !mymf$"); // Matches only "!mymf"

            String msg = Helper.removeFormatting(message.getString());
            System.out.println(msg);

            if (!regexWithNumber.matcher(msg).find() && !regexWithoutNumber.matcher(msg).find() && !regexWith2Numbers.matcher(msg).find()) return;

            Matcher match1 = regexWithNumber.matcher(msg);
            Matcher match2 = regexWith2Numbers.matcher(msg);

            if (match1.find()) {
                double number = Double.parseDouble(match1.group(2));
                double mf = mymf(number, 0);
                Helper.sleep(750, () -> {
                    Chat.command("pc [Cm] Your magic find is " + String.format("%.2f", mf) + " on Inquisitors");
                });
            } else if (match2.find()) {
                double number = Double.parseDouble(match2.group(2));
                double kc = Double.parseDouble(match2.group(3));
                double mf = mymf(number, kc);
                Helper.sleep(750, () -> {
                    Chat.command("pc [Cm] Your magic find is " + String.format("%.2f", mf) + " on Inquisitors");
                });
            } else {
                Helper.sleep(750, () -> {
                    Chat.command("pc Usage: !mymf <number> (Go in mf set with no1 around (so legion ISNT active) with renowned armor, it will give your mf if you used shuriken, fragged dae axe with max bestiary)");
                });
            }
        });
    }

    public static double mymf(double mf, double kc) {
        kc = (kc == 0 ? 1 : 1 + (kc / 100));
        mf = mf / 1.04;
        mf = ((mf*1.11/* Renowned + Legion */)*1.05 /* Shuriken */+ (130 /* Max Bestiary */ * 1.05 /* Shuriken */) * kc);
        return mf;
    }
}