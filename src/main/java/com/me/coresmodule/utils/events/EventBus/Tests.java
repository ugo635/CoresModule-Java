package com.me.coresmodule.utils.events.EventBus;

import com.me.coresmodule.utils.events.annotations.CmEvent;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Method;

public class Tests {

    @CmEvent("EntityLoadEvent")
    public static void test(EntityLoadEvent e) {
        System.out.println("Entity loaded: " + e.getEntity().getName().getString());
    }

    public static void main(String[] args) {
        try (ScanResult scan = new ClassGraph()
                .acceptPackages("example.app")
                .enableAllInfo()
                .scan()) {

            for (Class<?> c : scan.getAllClasses().loadClasses()) {
                for (Method m : c.getDeclaredMethods()) {
                    if (m.isAnnotationPresent(CmEvent.class)) {
                        System.out.println(c.getName() + "." + m.getName());
                    }
                }
            }
        }
    }
}
