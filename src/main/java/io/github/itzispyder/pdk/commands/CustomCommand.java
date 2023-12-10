package io.github.itzispyder.pdk.commands;

import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.pdk.commands.completions.CompletionNode;
import io.github.itzispyder.pdk.utils.misc.Voidable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public interface CustomCommand extends TabExecutor, Global {

    void dispatchCommand(CommandSender sender, Args args);

    void dispatchCompletions(CompletionBuilder b);

    default <T extends JavaPlugin> void register(Class<T> pluginClass) {
        CommandRegistry registry = this.getClass().getAnnotation(CommandRegistry.class);
        PluginCommand command = getPlugin(pluginClass).getCommand(registry.value());

        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    default boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandRegistry registry = this.getClass().getAnnotation(CommandRegistry.class);
        if (registry == null) {
            return true;
        }

        try {
            if (!sender.hasPermission(registry.permission().value())) {
                error(sender, registry.permission().message());
                return true;
            }
            dispatchCommand(sender, new Args(args));
        }
        catch (Exception ex) {
            if (registry.printStackTrace()) {
                ex.printStackTrace();
            }
            info(sender, "&cCorrect Usage: &7" + registry.usage());
        }
        return true;
    }

    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        try {
            CompletionBuilder b = new CompletionBuilder(label);
            dispatchCompletions(b);
            CompletionNode node = b.getRootNode();

            if (args.length == 0) {
                return node.getOptions();
            }
            for (int i = 0; i < args.length - 1; i++) {
                node = node.next(args[i]);
            }

            List<String> a = new ArrayList<>(node.getOptions());
            a.removeIf(s -> !s.toLowerCase().contains(args[args.length - 1].toLowerCase()));
            return a;
        }
        catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    default Voidable<CommandRegistry> getRegistry() {
        return Voidable.of(this.getClass().getAnnotation(CommandRegistry.class));
    }
}
