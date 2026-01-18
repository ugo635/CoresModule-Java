package com.me.coresmodule.utils;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
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

    public static HashMap<String, Object> getNbtMap(NbtElement nbt, ItemStack stack) {
        HashMap<String, Object> map = new HashMap<>();
        int tagID = nbt.getType();

        if (tagID == NbtElement.END_TYPE) {
            return map;
        } else if (tagID == NbtElement.BYTE_ARRAY_TYPE) {
            NbtByteArray arr = (NbtByteArray) nbt;
            List<Byte> list = new ArrayList<>();
            for (byte b : arr.getByteArray()) list.add(b);
            map.put("value", list);
        } else if (tagID == NbtElement.INT_ARRAY_TYPE) {
            NbtIntArray arr = (NbtIntArray) nbt;
            List<Integer> list = new ArrayList<>();
            for (int i : arr.getIntArray()) list.add(i);
            map.put("value", list);
        } else if (tagID == NbtElement.LIST_TYPE) {
            NbtList list = (NbtList) nbt;
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                result.add(getNbtMap(list.get(i), stack));
            }
            map.put("value", result);
        } else if (tagID == NbtElement.COMPOUND_TYPE) {
            NbtCompound compound = (NbtCompound) nbt;
            for (String key : compound.getKeys()) {
                NbtElement elem = compound.get(key);

                switch (elem) {
                    case NbtList nbtElements when key.equals("minecraft:lore") -> {
                        List<String> loreList = new ArrayList<>();
                        List<Text> lore = ItemHelper.getItemTooltip(stack);
                        for (Text line : lore) {
                            loreList.add(TextHelper.getFormattedString(line));
                        }
                        map.put(key, loreList);
                    }

                    case NbtCompound nbtCompound when key.equals("minecraft:custom_name") ->
                            map.put(key, TextHelper.getFormattedString(stack.getName()));

                    case NbtByteArray nbtElements when key.contains("backpack_data") -> {
                        try {
                            NbtCompound backpackData = NbtIo.readCompressed(
                                    new ByteArrayInputStream(nbtElements.getByteArray()),
                                    new NbtSizeTracker(Long.MAX_VALUE, 512)
                            );
                            map.put(key + "(decoded)", getNbtMap(backpackData, stack));
                        } catch (IOException e) {
                            System.out.println("Couldn't decompress backpack data, skipping! " + e);
                        }
                    }
                    case null, default -> map.put(key, getNbtMap(elem, stack));
                }
            }
        } else if (tagID == NbtElement.STRING_TYPE) {
            map.put("value", ((NbtString) nbt).asString().orElse("null"));
        } else if (tagID == NbtElement.BYTE_TYPE) {
            map.put("value", ((NbtByte) nbt).byteValue());
        } else if (tagID == NbtElement.SHORT_TYPE) {
            map.put("value", ((NbtShort) nbt).shortValue());
        } else if (tagID == NbtElement.INT_TYPE) {
            map.put("value", ((NbtInt) nbt).intValue());
        } else if (tagID == NbtElement.LONG_TYPE) {
            map.put("value", ((NbtLong) nbt).longValue());
        } else if (tagID == NbtElement.FLOAT_TYPE) {
            map.put("value", ((NbtFloat) nbt).floatValue());
        } else if (tagID == NbtElement.DOUBLE_TYPE) {
            map.put("value", ((NbtDouble) nbt).doubleValue());
        } else {
            map.put("value", nbt.toString());
        }

        return map;
    }

    public static String getUUID(ItemStack stack) {
        NbtElement root = ItemHelper.encodeItemStack(stack);
        if (!(root instanceof NbtCompound compound)) return null;

        if (!compound.contains("components")) return null;
        NbtCompound components = compound.getCompound("components").orElseThrow();

        if (!components.contains("minecraft:custom_data")) return null;
        NbtCompound customData = components.getCompound("minecraft:custom_data").orElseThrow();

        if (!customData.contains("uuid")) return null;

        return customData.getString("uuid").orElse("null");
    }

}