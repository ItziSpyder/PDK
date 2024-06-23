package io.github.itzispyder.pdk.commands;

import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.commands.completions.CompletionBuilder;
import io.github.itzispyder.pdk.commands.completions.CompletionNode;
import io.github.itzispyder.pdk.utils.misc.Voidable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public interface CustomCommand extends TabExecutor, Global {

    void dispatchCommand(CommandSender sender, Args args);

    void dispatchCompletions(CompletionBuilder b);

    default void register() {
        CommandRegistry registry = this.getClass().getAnnotation(CommandRegistry.class);
        PluginCommand command = getPlugin().getCommand(registry.value());

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
        if (!(sender instanceof Player) && registry.playersOnly()) {
            info(sender, "This command is for players only!");
            return true;
        }

        try {
            String perm = registry.permission().value();
            if (perm != null && !perm.isEmpty() && !sender.hasPermission(perm)) {
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

            String end = args[args.length - 1];
            List<String> a = new ArrayList<>(node.getOptions());

            if (node.isOptionsRegex()) {
                List<String> regexResult = new ArrayList<>();
                for (CompletionNode option : node.getNextOptions()) {
                    boolean regexMatches = CompletionNode.containsRegex(option, end) || end.isEmpty();
                    for (String s : option.getValues())
                        regexResult.add((regexMatches ? "§d" : "§c") + s + "§r");
                }
                return regexResult;
            }
            else {
                a.removeIf(s -> !s.toLowerCase().contains(end.toLowerCase()));
                return a;
            }
        }
        catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    default Voidable<CommandRegistry> getRegistry() {
        return Voidable.of(this.getClass().getAnnotation(CommandRegistry.class));
    }
}
