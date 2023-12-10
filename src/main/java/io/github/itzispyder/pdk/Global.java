package io.github.itzispyder.pdk;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public interface Global {

    default <T extends JavaPlugin> T getPlugin(Class<T> pluginClass) {
        return JavaPlugin.getPlugin(pluginClass);
    }

    default <T extends JavaPlugin> FileConfiguration getConfig(Class<T> pluginClass) {
        return getPlugin(pluginClass).getConfig();
    }

    default <T extends JavaPlugin> void saveConfig(Class<T> pluginClass) {
        getPlugin(pluginClass).saveDefaultConfig();
    }

    default <T extends JavaPlugin> void runSync(Class<T> pluginClass, Runnable task) {
        Bukkit.getScheduler().runTask(getPlugin(pluginClass), task);
    }

    default String colorChar(char c, String msg) {
        return msg.replace(c, '§');
    }

    default String color(String msg) {
        return colorChar('&', msg);
    }

    default void info(CommandSender sender, String msg) {
        sender.sendMessage(color(msg));
    }

    default void error(CommandSender sender, String msg) {
        info(sender, "§c" + msg);
    }

    default void except(CommandSender sender, Exception ex) {
        String type = ex.getClass().getSimpleName();
        String msg = ex.getMessage();
        error(sender, """
                    §cError:
                    §7-§c Type: §7%s
                    §7-§c Message: §7%s
                    """.formatted(type, msg));
    }

    static Global get() {
        return new Global(){};
    }
}
