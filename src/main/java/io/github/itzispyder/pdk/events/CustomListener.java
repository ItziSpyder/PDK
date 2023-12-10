package io.github.itzispyder.pdk.events;

import io.github.itzispyder.pdk.Global;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public interface CustomListener extends Listener, Global {

    default CustomListener register() {
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        return this;
    }
}
