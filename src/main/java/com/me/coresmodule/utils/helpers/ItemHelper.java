package com.me.coresmodule.utils.helpers;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

public class ItemHelper {
    public static ItemStack getHeldItem() {
        return mc.player != null ? mc.player.getMainHandItem() : ItemStack.EMPTY;
    }

    public static String getFormattedItemName(ItemStack item) {
        return item != ItemStack.EMPTY ? TextHelper.getFormattedString(item.getDisplayName()) : "";
    }

    public static String getFormattedHeldItemName() {
        return getFormattedItemName(getHeldItem());
    }

    public static List<ItemStack> getArmorItems() {
        Inventory inventory;
        if (mc.player != null) {
            inventory = mc.player.getInventory();
            List<ItemStack> armor = new ArrayList<ItemStack>();
            for (int i = 36; i < 40; i++) {
                armor.add(inventory.getItem(i));
            }

            return armor;
        }
        return new ArrayList<ItemStack>();
    }

    public static String getItemName(ItemStack item) {
        return item != ItemStack.EMPTY ? item.getItemName().getString() : "";
    }

    public static String getHeldItemName() {
        ItemStack item = getHeldItem();
        return getItemName(item);
    }

    public static List<Component> getItemTooltip(ItemStack item) {
        return item.getTooltipLines(
                TooltipContext.of(mc.level),
                mc.player,
                mc.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL
        );
    }

    public static List<Component> getHeldItemTooltip() {
        return getItemTooltip(getHeldItem());
    }

    public static void addTooltip(ItemStack item, String line) {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (stack.equals(item)) {
                list.add(Component.literal(line));
            }
        });
    }

    public static String getValueFromLine(Pattern regex, List<Component> lore) {
        for (Component line : lore) {
            String lineStr = line.getString();
            Matcher matcher = regex.matcher(lineStr);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    public static String getValueFromLineWithFormatting(Pattern regex, List<Component> lore) {
        for (Component line : lore) {
            String lineStr = TextHelper.getFormattedString(line);
            Matcher matcher = regex.matcher(lineStr);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    public static String toString(Component t) {
        return TextHelper.getFormattedString(t).replace("§", "&&");
    }


    public static void replaceTooltipAt(int index, List<Component> list, Component content) {
        if (index < 0 || index >= list.size()) return;
        list.set(index, content);
    }

    public static void replaceTooltipAt(int index, List<Component> list, String content) {
        replaceTooltipAt(index, list, Component.literal(content));
    }

}