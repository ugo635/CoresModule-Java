package com.me.coresmodule.features.Diana;

import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Pattern;

public class NewMfCalc {
    public static double additionalMf = 0;
    public static void register() {
        Register.command("getAdditionalMf", args -> {
            Chat.chat(String.valueOf(additionalMf));
        }, "addMf");

        Register.command("getItemName", args -> {
            Chat.chat(ItemHelper.getHeldItemName());
        });

        Register.command("addTooltipToHeldItem", args -> {
            ItemHelper.addTooltip(ItemHelper.getHeldItem(), "§6§lHiiiii");
        });

        Register.command("getAllArmor", args -> {
            List<ItemStack> armor = ItemHelper.getArmorItems();
            String mf1;
            String mf2;
            String mf3;
            String mf4;
            String mf5;
            String amt;

            mf1 = (amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(0)))) != "" && !amt.isEmpty() ? amt : "0.0";
            mf2 = (amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(1)))) != "" && !amt.isEmpty() ? amt : "0.0";
            mf3 = (amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(2)))) != "" && !amt.isEmpty() ? amt : "0.0";
            mf4 = (amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getItemTooltip(armor.get(3)))) != "" && !amt.isEmpty() ? amt : "0.0";
            mf5 = (amt = ItemHelper.getValueFromLine(Pattern.compile("\\+([0-9]*\\.?[0-9]+)✯ Magic Find"), ItemHelper.getItemTooltip(armor.get(3)))) != "" && !amt.isEmpty() ? amt : "0.0";

            double mf = Double.parseDouble(mf1) + Double.parseDouble(mf2) + Double.parseDouble(mf3) + Double.parseDouble(mf4) + Double.parseDouble(mf5);
            Chat.chat("§6 Armor mf: " + mf);
        });

        Register.command("getCurrentItemLore", args -> {
            List<Text> lore = ItemHelper.getHeldItemTooltip();
            for (Text line : lore) {
                Chat.chat(TextHelper.getFormattedString(line));
            }
        });

        Register.command("getMfFromDae", args -> {
            String mf1 = "0.0";
            String mf2 = "0.0";
            String amt;
            mf1 = (amt = ItemHelper.getValueFromLine(Pattern.compile("Magic Find: \\+(.*)"), ItemHelper.getHeldItemTooltip())) != "" && !amt.isEmpty() ? amt : "0.0";
            mf2 = (amt = ItemHelper.getValueFromLine(Pattern.compile("\\+([0-9]*\\.?[0-9]+)✯ Magic Find"), ItemHelper.getHeldItemTooltip())) != "" && !amt.isEmpty() ? amt : "0.0";
            double mf = Double.parseDouble(mf1) + Double.parseDouble(mf2);
            System.out.println("§6 Dae Bonus: " + mf);
            Chat.chat("§6 Dae Bonus: " + mf);
        });
    }
}