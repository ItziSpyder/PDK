package io.github.itzispyder.pdk.utils.misc;

import io.github.itzispyder.pdk.Global;

import java.util.function.Consumer;
import java.util.function.Function;

public class Voidable<T> implements Global {

    private final T value;

    private Voidable(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public <U> Voidable<U> map(Function<T, U> function) {
        return isPresent() ? of(function.apply(value)) : of(null);
    }

    public void accept(Consumer<T> action) {
        if (isPresent()) {
            action.accept(value);
        }
    }

    public void accept(Consumer<T> action, Runnable orElse) {
        if (isPresent()) {
            action.accept(value);
        }
        else {
            orElse.run();
        }
    }

    public T getOrDef(T fallback) {
        return isPresent() ? value : fallback;
    }

    public T getOrThrow(String msg, Object... args) {
        checkPre(isPresent(), msg, args);
        return value;
    }

    public T getOrThrow() {
        return getOrThrow("value is not present.");
    }

    public static <T> Voidable<T> of(T value) {
        return new Voidable<>(value);
    }
}