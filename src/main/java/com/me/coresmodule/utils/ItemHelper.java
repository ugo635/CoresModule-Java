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
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static ItemStack markNbt(ItemStack stack, String key, boolean value) {
        NbtCompound tag = new NbtCompound();
        tag.putBoolean(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    public static ItemStack markNbt(ItemStack stack, String key, int value) {
        NbtCompound tag = new NbtCompound();
        tag.putInt(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    public static ItemStack markNbt(ItemStack stack, String key, double value) {
        NbtCompound tag = new NbtCompound();
        tag.putDouble(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    public static ItemStack markNbt(ItemStack stack, String key, String value) {
        NbtCompound tag = new NbtCompound();
        tag.putString(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    public static ItemStack addMarkNbt(ItemStack stack, String key, boolean value) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return null; // Try adding instead of creating
        NbtCompound tag = comp.copyNbt(); // Add existing data
        tag.putBoolean(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    public static ItemStack addMarkNbt(ItemStack stack, String key, int value) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return null; // Try adding instead of creating
        NbtCompound tag = comp.copyNbt(); // Add existing data
        tag.putInt(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    public static ItemStack addMarkNbt(ItemStack stack, String key, double value) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return null; // Try adding instead of creating
        NbtCompound tag = comp.copyNbt(); // Add existing data
        tag.putDouble(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    public static ItemStack addMarkNbt(ItemStack stack, String key, String value) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return null; // Try adding instead of creating
        NbtCompound tag = comp.copyNbt(); // Add existing data
        tag.putString(key, value);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(ItemStack stack, String name, String result) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return null;
        return switch (result.toLowerCase()) {
            case "boolean" -> (T) comp.copyNbt().getBoolean(name).orElse(null);
            case "integer" -> (T) comp.copyNbt().getInt(name).orElse(null);
            case "double" -> (T) comp.copyNbt().getDouble(name).orElse(null);
            case "string" -> (T) comp.copyNbt().getString(name).orElse(null);
            default -> null;
        };
    }

    public static String getType(NbtElement element) {
        if (element == null) return "null";
        return switch (element.getType()) {
            case 1 -> "byte";
            case 2 -> "short";
            case 3 -> "integer";
            case 4 -> "long";
            case 5 -> "float";
            case 6 -> "double";
            case 7 -> "byte_array";
            case 8 -> "string";
            case 9 -> "list";
            case 10 -> "compound";
            case 11 -> "int_array";
            case 12 -> "long_array";
            default -> "unknown";
        };
    }

    public static HashMap<String, Object> toMap(ItemStack stack) {
        HashMap<String, Object> map = new HashMap<>();
        String itemId = Registries.ITEM.getId(stack.getItem()).toString();
        map.put("itemId", itemId);
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp != null) {
            NbtCompound tag = comp.copyNbt();
            for (String key : tag.getKeys()) {
                String type = getType(tag.get(key));
                switch (type) {
                    case "byte" -> map.put(key, tag.getBoolean(key).orElse(null));
                    case "integer" -> map.put(key, tag.getInt(key).orElse(null));
                    case "double" -> map.put(key, tag.getDouble(key).orElse(null));
                    case "string" -> map.put(key, tag.getString(key).orElse(null));
                    default -> map.put(key, tag.get(key));
                }

            }
        }
        return map;
    }

    public static ItemStack fromMap(HashMap<String, Object> map) {
        Identifier id = Identifier.of((String) map.get("itemId"));
        ItemStack stack = new ItemStack(Registries.ITEM.get(id));
        NbtCompound tag = new NbtCompound();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                tag.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer) {
                tag.putInt(key, (Integer) value);
            } else if (value instanceof Double) {
                tag.putDouble(key, (Double) value);
            } else if (value instanceof String) {
                tag.putString(key, (String) value);
            }
        }

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
        return stack;
    }

}