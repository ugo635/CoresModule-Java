package com.me.coresmodule.utils;

import com.me.coresmodule.CoresModule;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.me.coresmodule.CoresModule.mc;

public class ItemHelper {
    public static ItemStack getHeldItem() {
        return mc.player.getMainHandStack() != null ? mc.player.getMainHandStack() : ItemStack.EMPTY;
    }

    public static String getFormattedItemName(ItemStack item) {
        return item != ItemStack.EMPTY ? TextHelper.getFormattedString(item.getName()) : "";
    }

    public static List<ItemStack> getArmorItems() {
        PlayerInventory inventory;
        if (mc.player != null && mc.player.getInventory() != null) {
            inventory = mc.player.getInventory();
            List<ItemStack> armor = new ArrayList<ItemStack>();
            for (int i = 36; i < 40; i++) {
                armor.add(inventory.getStack(i));;
            }
            return armor;
        }
        return new ArrayList<ItemStack>();
    }

    public static String getItemName(ItemStack item) {
        return item != ItemStack.EMPTY ? item.getName().getString() : "";
    }

    public static String getItemNameWithFormatting(ItemStack item) {
        return item != ItemStack.EMPTY ? TextHelper.getFormattedString(item.getName()) : "";
    }

    public static String getHeldItemName() {
        ItemStack item = getHeldItem();
        return item != ItemStack.EMPTY ? item.getName().getString() : "";
    }

    public static List<Text> getItemTooltip(ItemStack item) {
        return item.getTooltip(
                TooltipContext.create(mc.world),
                mc.player,
                mc.options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC
        );
    }

    public static List<Text> getHeldItemTooltip() {
        return getItemTooltip(getHeldItem());
    }

    public static void addTooltip(ItemStack item, String line) {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (ItemStack.areEqual(stack, item)) {
                list.add(Text.literal(line));
            }
        });
    }

    public static String getValueFromLine(Pattern regex, List<Text> lore) {
        for (Text line : lore) {
            String lineStr = line.getString();
            Matcher matcher = regex.matcher(lineStr);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    public static String getValueFromLineWithFormatting(Pattern regex, List<Text> lore) {
        for (Text line : lore) {
            String lineStr = TextHelper.getFormattedString(line);
            Matcher matcher = regex.matcher(lineStr);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }

    public static <T> ItemStack markNbt(ItemStack stack, ComponentType<T> key, T value) {
        stack.set(key, value);
        return stack;
    }
}