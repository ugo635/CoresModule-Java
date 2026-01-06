package com.me.coresmodule.utils.events.processor;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.events.annotations.CmEvent;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;

import java.lang.reflect.Method;

/**
 * Scans classes at runtime for methods annotated with @CmEvent and registers them with the string-based EventBus.
 * Call EventProcessorRuntime.registerAll() from your ModInitializer (CoresModule.onInitialize()).
 */
public final class EventProcessorRuntime {
    // runtime count of discovered annotated methods
    public static int count = 0;

    private EventProcessorRuntime() {}

    public static void registerAll() {
        try (ScanResult scan = new ClassGraph()
                .acceptPackages("com.me.coresmodule") // limit to your package
                .enableAllInfo()
                .scan()) {

            Helper.print("Scanning for @CmEvent...");
            Helper.print("Found classes: " + scan.getAllClasses().size());
            for (ClassInfo classInfo : scan.getAllClasses()) {
                for (MethodInfo methodInfo : classInfo.getMethodInfo()) {
                    if (methodInfo.hasAnnotation(CmEvent.class.getName())) {
                        // load Method and annotation
                        Method method = methodInfo.loadClassAndGetMethod();
                        CmEvent ann = method.getAnnotation(CmEvent.class);
                        if (ann == null) continue;
                        
                        String eventName = ann.value(); // may be empty

                        // Only support static methods for simplicity (as your processor had static examples)
                        if ((method.getModifiers() & java.lang.reflect.Modifier.STATIC) == 0) {
                            // optionally warn and skip non-static
                            continue;
                        }

                        // Register a wrapper that invokes the found method when the event fires
                        EventBus.on(eventName, data -> {
                            try {
                                method.invoke(null, data);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        });

                        Helper.print("Found a @CmEvent");
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}