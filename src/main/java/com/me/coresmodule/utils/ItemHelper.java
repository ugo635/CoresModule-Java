package com.me.coresmodule.utils;

import com.google.common.hash.HashCode;
import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.chat.Chat;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    /*
    public static void copyNBTTagToClipboard(Tag nbtTag, String message) {
        if (nbtTag == null) {
            Chat.chat("§cThis item has no NBT data!");
            return;
        }
        mc.keyboard.setClipboard(prettyPrintNBT(nbtTag), message); // Make in helper
    }
    */

    public static NbtElement encodeItemStack(ItemStack itemStack) {
        RegistryWrapper.WrapperLookup registryAccess = registryAccess();

        return ItemStack.CODEC.encodeStart(
                registryAccess.getOps(NbtOps.INSTANCE), itemStack
        ).getOrThrow();
    }

    private static RegistryWrapper.WrapperLookup registryAccess() {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) {
            throw new IllegalStateException("World not available");
        } else {
            return world.getRegistryManager();
        }
    }

    public static String prettyPrintNBT(NbtElement nbt, ItemStack stack) {
        final String INDENT = "    ";
        int tagID = nbt.getType();
        StringBuilder stringBuilder = new StringBuilder();

        if (tagID == NbtElement.END_TYPE) {
            stringBuilder.append('}');
        } else if (tagID == NbtElement.BYTE_ARRAY_TYPE || tagID == NbtElement.INT_ARRAY_TYPE) {
            stringBuilder.append('[');
            if (tagID == NbtElement.BYTE_ARRAY_TYPE) {
                NbtByteArray arr = (NbtByteArray) nbt;
                byte[] bytes = arr.getByteArray();
                for (int i = 0; i < bytes.length; i++) {
                    stringBuilder.append(bytes[i]);
                    if (i < bytes.length - 1) stringBuilder.append(", ");
                }
            } else {
                NbtIntArray arr = (NbtIntArray) nbt;
                int[] ints = arr.getIntArray();
                for (int i = 0; i < ints.length; i++) {
                    stringBuilder.append(ints[i]);
                    if (i < ints.length - 1) stringBuilder.append(", ");
                }
            }
            stringBuilder.append(']');
        } else if (tagID == NbtElement.LIST_TYPE) {
            NbtList list = (NbtList) nbt;
            stringBuilder.append('[');
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(prettyPrintNBT(list.get(i), stack));
                if (i < list.size() - 1) stringBuilder.append(", ");
            }
            stringBuilder.append(']');
        } else if (tagID == NbtElement.COMPOUND_TYPE) {
            NbtCompound compound = (NbtCompound) nbt;
            stringBuilder.append('{');
            if (!compound.isEmpty()) {
                Iterator<String> keys = compound.getKeys().iterator();
                stringBuilder.append(System.lineSeparator());

                while (keys.hasNext()) {
                    String key = keys.next();
                    NbtElement elem = compound.get(key);

                    // Handle full lore array with § codes
                    if (key.equals("minecraft:lore") && elem instanceof NbtList) {
                        List<Text> lore = ItemHelper.getItemTooltip(stack);
                        stringBuilder.append(key).append(": ").append("[").append(System.lineSeparator());
                        for (int i = 0; i < lore.size(); i++) {
                            String loreLine = TextHelper.getFormattedString(lore.get(i));
                            stringBuilder.append(INDENT).append(INDENT).append("\"").append(loreLine).append("\"");
                            if (i < lore.size() - 1) stringBuilder.append(",").append(System.lineSeparator());
                        }
                        stringBuilder.append(System.lineSeparator()).append(INDENT).append("]");
                        continue;
                    } else {
                        stringBuilder.append(key).append(": ").append(prettyPrintNBT(elem, stack));
                    }


                    // Handle backpack_data decompression
                    if (key.contains("backpack_data") && elem instanceof NbtByteArray) {
                        try {
                            NbtCompound backpackData = NbtIo.readCompressed(
                                    new ByteArrayInputStream(((NbtByteArray) elem).getByteArray()),
                                    new NbtSizeTracker(Long.MAX_VALUE, 512)
                            );
                            stringBuilder.append(",").append(System.lineSeparator());
                            stringBuilder.append(key).append("(decoded): ").append(prettyPrintNBT(backpackData, stack));
                        } catch (IOException e) {
                            System.out.println("Couldn't decompress backpack data into NBT, skipping!" + e);
                        }
                    }

                    if (keys.hasNext()) stringBuilder.append(",").append(System.lineSeparator());
                }

                String indented = stringBuilder.toString().replaceAll(System.lineSeparator(), System.lineSeparator() + INDENT);
                stringBuilder = new StringBuilder(indented);
            }
            stringBuilder.append(System.lineSeparator()).append('}');
        } else {
            stringBuilder.append(nbt);
        }

        return stringBuilder.toString();
    }
}