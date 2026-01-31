package com.me.coresmodule.utils.events.EventBus;

import com.me.coresmodule.utils.events.impl.AfterHudRenderer;
import com.me.coresmodule.utils.events.impl.Event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Simple string-based event bus for emitting and listening to events.
 */
public class EventBus {

    private static final Map<String, List<Consumer<Event>>> listeners = new ConcurrentHashMap<>();

    public static void on(String eventName, Consumer<Event> callback) {
        listeners
                .computeIfAbsent(eventName.toLowerCase(), key -> new CopyOnWriteArrayList<>())
                .add(callback);
    }

    public static void emit(Event data) {
        List<Consumer<Event>> callbacks = listeners.get(data.getName());
        if (callbacks != null) {
            for (Consumer<Event> cb : callbacks) {
                cb.accept(data);
            }
        }
    }

    public void clear(String eventName) {
        listeners.remove(eventName.toLowerCase());
    }

    public void clearAll() {
        listeners.clear();
    }
}
