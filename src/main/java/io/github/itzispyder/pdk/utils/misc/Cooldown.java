package io.github.itzispyder.pdk.utils.misc;

import java.util.HashMap;
import java.util.Map;

public class Cooldown<T> {

    private final Map<T, Long> timer;

    public Cooldown() {
        this.timer = new HashMap<>();
    }

    private <O> O getOrDefault(O value, O def) {
        return value != null ? value : def;
    }

    public long getCooldown(T obj) {
        return Math.max(getOrDefault(timer.get(obj), 0L) - System.currentTimeMillis(), 0L);
    }

    public double getCooldownSec(T obj) {
        final long cooldown = this.getCooldown(obj);
        return Math.floor(cooldown / 10.0) / 100.0;
    }

    public boolean isOnCooldown(T obj) {
        return getCooldown(obj) > 0L;
    }

    public void setCooldown(T obj, long millis) {
        timer.put(obj, System.currentTimeMillis() + millis);
    }

    public void addCooldown(T obj, long millis) {
        setCooldown(obj, getCooldown(obj) + millis);
    }
}
