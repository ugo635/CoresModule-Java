package com.me.coresmodule.utils.events.processor;

import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.events.EventBus.EventBus;
import com.me.coresmodule.utils.events.annotations.CmEvent;
import com.me.coresmodule.utils.events.impl.AfterHudRenderer;
import io.github.classgraph.*;

import java.lang.reflect.Method;

public final class EventProcessor {
    public static int count = 0;

    private EventProcessor() {}

    public static void registerAll() {
        try (ScanResult scan = new ClassGraph()
                .acceptPackages("com.me.coresmodule")
                .enableAnnotationInfo()
                .enableMethodInfo()
                .scan()) {

            Helper.print("Scanning for @CmEvent...");

            for (ClassInfo classInfo : scan.getClassesWithMethodAnnotation(CmEvent.class.getName())) {

                for (MethodInfo methodInfo : classInfo.getMethodInfo()) {
                    if (methodInfo.hasAnnotation(CmEvent.class.getName())) {

                        Class<?> eventClass = getEventClassFromMethod(methodInfo);
                        String eventName = eventClass.getSimpleName();

                        Method finalMethod = Class.forName(methodInfo.getClassInfo().getName()).getDeclaredMethod(
                                methodInfo.getName(),
                                eventClass
                        );

                        EventBus.on(eventName, data -> {
                            try {
                                finalMethod.invoke(null, data);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        });
                        count++;
                        Helper.print("Found @CmEvent method: " + classInfo.getSimpleName() + "." + methodInfo.loadClassAndGetMethod().getName() + "() for event '" + eventName + "'");
                    }

                }
            }

            Helper.print("Registered " + count + " @CmEvent methods");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getEventClassFromMethod(MethodInfo method) throws ClassNotFoundException, IllegalArgumentException {
        Method m = method.loadClassAndGetMethod();
        Class<?>[] params = m.getParameterTypes();
        if (params.length != 1) {
            throw new IllegalArgumentException("Event listener methods must have exactly one parameter");
        }
        return Class.forName(params[0].getName());
    }
}
