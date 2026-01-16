package com.me.coresmodule.utils.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class TickScheduler {

    public static final List<ScheduledTask> tasks = new ArrayList<>();

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Iterator<ScheduledTask> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                ScheduledTask task = iterator.next();
                task.counter++;
                if (task.counter >= task.tick) {
                    task.action.accept(iterator::remove);
                    task.counter = 0;
                }
            }
        });
    }

    public static class ScheduledTask {
        public final int tick;
        public final Consumer<Runnable> action; // (() -> Unit) -> Unit
        public int counter = 0;

        public ScheduledTask(int tick, Consumer<Runnable> action) {
            this.tick = tick;
            this.action = action;
        }
    }

}
