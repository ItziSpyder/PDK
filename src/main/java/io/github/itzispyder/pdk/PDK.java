package io.github.itzispyder.pdk;

import io.github.itzispyder.pdk.events.listeners.RegistryListeners;
import org.bukkit.plugin.java.JavaPlugin;

public final class PDK {

    private static boolean active = false;

    public static synchronized void init(JavaPlugin plugin) {
        if (!active) {
            active = true;
            new RegistryListeners().register(plugin.getClass());
        }
    }
}
