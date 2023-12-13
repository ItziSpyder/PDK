package io.github.itzispyder.pdk;

import io.github.itzispyder.pdk.events.listeners.RegistryListeners;
import io.github.itzispyder.pdk.utils.misc.Voidable;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TODO: (1) update version in gradle.properties
 * TODO: (2) update jar file version in README.md
 */
public final class PDK {

    private static boolean active = false;
    private static Class<? extends JavaPlugin> plugin;

    public static synchronized void init(JavaPlugin plugin) {
        if (plugin != null && !active) {
            active = true;
            PDK.plugin = plugin.getClass();
            new RegistryListeners().register();
        }
    }

    public static Voidable<Class<? extends JavaPlugin>> getRegisteredPlugin() {
        return Voidable.of(plugin);
    }
}
