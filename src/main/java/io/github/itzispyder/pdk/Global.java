package io.github.itzispyder.pdk;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BooleanSupplier;

public interface Global {

    Global instance = new Global(){};

    default JavaPlugin getPlugin() {
        return getPlugin(PDK.getRegisteredPlugin().getOrThrow("plugin is not registered, make sure PDK.init() is called!"));
    }

    default <T extends JavaPlugin> T getPlugin(Class<T> pluginClass) {
        return JavaPlugin.getPlugin(pluginClass);
    }

    default FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    default void saveConfig() {
        getPlugin().saveDefaultConfig();
    }

    default void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(getPlugin(), task);
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

    default void checkPre(boolean check, String msg, Object... args) {
        if (!check) {
            throw new IllegalArgumentException(msg.formatted(args));
        }
    }

    default void checkPre(BooleanSupplier check, String msg, Object... args) {
        checkPre(check.getAsBoolean(), msg, args);
    }
}
