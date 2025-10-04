package com.me.coresmodule.utils.events.EventBus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Simple string-based event bus for emitting and listening to events.
 */
public class EventBus {

    private static final Map<String, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    public static void on(String eventName, Consumer<Object> callback) {
        listeners
                .computeIfAbsent(eventName, key -> new CopyOnWriteArrayList<>())
                .add(callback);
    }

    public void emit(String eventName, Object data) {
        List<Consumer<Object>> callbacks = listeners.get(eventName);
        if (callbacks != null) {
            for (Consumer<Object> cb : callbacks) {
                cb.accept(data);
            }
        }
    }

    public void clear(String eventName) {
        listeners.remove(eventName);
    }

    public void clearAll() {
        listeners.clear();
    }
}
