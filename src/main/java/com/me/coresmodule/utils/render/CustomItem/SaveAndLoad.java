package com.me.coresmodule.utils.render.CustomItem;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.Tuples.Quadruple;
import com.me.coresmodule.utils.Tuples.Triple;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveAndLoad {
    public static void register() throws IOException {
        FilesHandler.createFile("CustomItemRenderer.json");
        CoresModule.overrides = load();
        for (String key : CoresModule.overrides.keySet()) {
            Quadruple<ItemStack, ItemStack, Boolean, String> quadruple = CoresModule.overrides.get(key);
            ItemTooltipCallback.EVENT.register((stack, ctx, type, list) -> {
                String uuid = ItemHelper.getUUID(stack);
                if (uuid != null && uuid.equals(key)) {
                    ItemHelper.replaceTooltipAt(0, list, quadruple.fourth);
                }
            });
        }

    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Quadruple<ItemStack, ItemStack, Boolean, String>> load() {
        System.out.println("[CoresModule] Loading...");
        try {
            HashMap<String, Quadruple<ItemStack, ItemStack, Boolean, String>> items = new HashMap<>();
            String content = FilesHandler.getContent("CustomItemRenderer.json").trim();
            if (!content.isEmpty() && !content.equals("{}")) {
                JSONObject json = new JSONObject(content);
                Map<String, Object> temp = json.toMap();
                for (String key : temp.keySet()) {
                    HashMap<String, Object> values = (HashMap<String, Object>) temp.get(key);
                    ItemStack first = ItemHelper.fromMap((HashMap<String, Object>) values.get("first"));
                    ItemStack second = ItemHelper.fromMap((HashMap<String, Object>) values.get("second"));
                    Boolean third = (Boolean) values.get("third");
                    String fourth = (String) values.get("fourth");
                    values.put("first", first);
                    values.put("second", second);
                    values.put("third", third);
                    values.put("fourth", fourth);
                    items.put(key, Quadruple.<ItemStack, ItemStack, Boolean, String>fromMap(values));
                }

                return items;
            } else {
                FilesHandler.writeToFile("CustomItemRenderer.json", "{}");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public static void save() {

        System.out.println("[CoresModule] Saving...");
        HashMap<String, Object> map = new HashMap<>();
        for (String key : CoresModule.overrides.keySet()) {
            Quadruple<ItemStack, ItemStack, Boolean, String> quadruple = CoresModule.overrides.get(key);
            map.put(key, quadruple.toMapItemStack());
        }


        try {
            System.out.println(map);
            JSONObject json = new JSONObject(map);
            FilesHandler.writeToFile("CustomItemRenderer.json", json.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
