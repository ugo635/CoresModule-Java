package com.me.coresmodule.utils.render.CustomItem;

import com.me.coresmodule.CoresModule;
import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.ItemHelper;
import com.me.coresmodule.utils.Pair;
import com.me.coresmodule.utils.Triple;
import com.me.coresmodule.utils.events.annotations.CmEvent;
import com.me.coresmodule.utils.events.impl.OnDisconnect;
import com.me.coresmodule.utils.render.overlay.OverlayValues;
import net.minecraft.item.ItemStack;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SaveAndLoad {
    public static void register() throws IOException {
        FilesHandler.createFile("CustomItemRenderer.json");
        CoresModule.overrides = load();
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Triple<ItemStack, ItemStack, Boolean>> load() {
        System.out.println("[CoresModule] Loading...");
        try {
            HashMap<String, Triple<ItemStack, ItemStack, Boolean>> items = new HashMap<>();
            String content = FilesHandler.getContent("CustomItemRenderer.json").trim();
            if (!content.isEmpty() && !content.equals("{}")) {
                JSONObject json = new JSONObject(content);
                Map<String, Object> temp = json.toMap();
                for (String key : temp.keySet()) {
                    HashMap<String, Object> values = (HashMap<String, Object>) temp.get(key);
                    ItemStack first = ItemHelper.fromMap((HashMap<String, Object>) values.get("first"));
                    ItemStack second = ItemHelper.fromMap((HashMap<String, Object>) values.get("second"));
                    Boolean third = (Boolean) values.get("third");
                    values.put("first", first);
                    values.put("second", second);
                    values.put("third", third);
                    items.put(key, Triple.<ItemStack, ItemStack, Boolean>fromMap(values));
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
            Triple<ItemStack, ItemStack, Boolean> triple = CoresModule.overrides.get(key);
            map.put(key, triple.toMapItemStack());
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
