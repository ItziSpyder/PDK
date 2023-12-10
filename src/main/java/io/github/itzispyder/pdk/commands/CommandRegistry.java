package io.github.itzispyder.pdk.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandRegistry {

    String value();
    String usage() default "none";
    Permission permission() default @Permission("");
    boolean printStackTrace() default false;
    boolean playersOnly() default false;
}
