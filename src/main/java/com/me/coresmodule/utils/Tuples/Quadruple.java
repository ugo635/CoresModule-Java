package com.me.coresmodule.utils.Tuples;

import com.me.coresmodule.utils.ItemHelper;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class Quadruple<T, S, U, V> {
    public T first;
    public S second;
    public U third;
    public V fourth;

    public Quadruple(T first, S second, U third, V fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first", first);
        map.put("second", second);
        map.put("third", third);
        map.put("fourth", fourth);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T, S, U, V> Quadruple<T, S, U, V> fromMap(HashMap<String, Object> map) {
        T first = (T) map.get("first");
        S second = (S) map.get("second");
        U third = (U) map.get("third");
        V fourth = (V) map.get("fourth");
        return new Quadruple<>(first, second, third, fourth);
    }

    public HashMap<String, Object> toMapItemStack() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first", ItemHelper.toMap((ItemStack) first));
        map.put("second", ItemHelper.toMap((ItemStack) second));
        map.put("third", third);
        map.put("fourth", fourth);
        return map;
    }

}
