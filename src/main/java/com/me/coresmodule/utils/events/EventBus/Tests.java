package com.me.coresmodule.utils.events.EventBus;

import com.me.coresmodule.utils.events.annotations.CmEvent;

public class Tests {
    public static void register() {}

    @CmEvent("EntityLoadEvent")
    public static void test(EntityLoadEvent e) {
        System.out.println("Entity loaded: " + e.getEntity().getName().getString());
    }
}
