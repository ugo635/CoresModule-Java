package com.me.coresmodule.utils.Tuples;

import com.me.coresmodule.utils.ItemHelper;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class Pair<T, S> {
    public T first;
    public S second;

    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first", first);
        map.put("second", second);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T, S> Pair<T, S> fromMap(HashMap<String, Object> map) {
        T first = (T) map.get("first");
        S second = (S) map.get("second");
        return new Pair<>(first, second);
    }

    public HashMap<String, Object> toMapItemStack() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first", ItemHelper.toMap((ItemStack) first));
        map.put("second", ItemHelper.toMap((ItemStack) second));
        return map;
    }
}
