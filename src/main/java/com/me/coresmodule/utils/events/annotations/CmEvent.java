package com.me.coresmodule.utils.events.annotations;

import java.lang.annotation.*;

/**
 * Marks a method as a CoresModule event listener for the (compile-time) event processor.
 *
 * This mirrors the SBO project's @SboEvent design:
 * - Target: methods
 * - Retention: SOURCE (intended for a compile-time processor that generates registration code)
 *
 * Usage:
 * <pre>
 * @CmEvent
 * public static void onSomeEvent(SomeEvent e) {
 *     // handle
 * }
 * </pre>
 *
 * Note: If you don't implement a compile-time processor, this is a harmless marker
 * useful for documentation and future automation.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface CmEvent {
    /**
     * Optional descriptive value for the annotated listener.
     */
    String value() default "";
}