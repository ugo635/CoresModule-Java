package com.me.coresmodule.utils;

import java.util.HashMap;

public class Triple<T, S, U> {
    public T first;
    public S second;
    public U third;

    public Triple(T first, S second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first", first);
        map.put("second", second);
        map.put("third", third);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T, S, U> Triple<T, S, U> fromMap(HashMap<String, Object> map) {
        T first = (T) map.get("first");
        S second = (S) map.get("second");
        U third = (U) map.get("third");
        return new Triple<>(first, second, third);
    }
}
