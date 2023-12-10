package io.github.itzispyder.pdk.utils;

import io.github.itzispyder.pdk.Global;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ServerUtils {

    public static List<Player> players() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static List<? extends Player> players(Predicate<Player> targets) {
        return Bukkit.getOnlinePlayers().stream().filter(targets).toList();
    }

    public static List<? extends Player> staffs() {
        return players(Player::isOp);
    }

    public static List<? extends Player> staffs(Predicate<Player> targets) {
        return players(player -> player.isOp() && targets.test(player));
    }

    public static void forEachPlayer(Predicate<Player> targets, Consumer<Player> action) {
        players(targets).forEach(action);
    }

    public static void forEachPlayer(Consumer<Player> action) {
        Bukkit.getOnlinePlayers().forEach(action);
    }

    public static void dmEachPlayer(Predicate<Player> targets, String message) {
        players(targets).forEach(player -> player.sendMessage(message));
    }

    public static void dmEachPlayer(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    public static void sendActionBar(Predicate<Player> targets, String message) {
        players(targets).forEach(player -> sendActionBar(player, message));
    }

    public static void sendActionBar(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> sendActionBar(player, message));
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public static void sendTitle(Player player, String title, String subtitle, int in, int stay, int out) {
        player.sendTitle(title, subtitle, in, stay, out);
    }

    public static void sendTitle(Player player, String title, int in, int stay, int out) {
        sendTitle(player, title, "", in, stay, out);
    }

    public static void sendTitle(Player player, String title) {
        sendTitle(player, title, "", 20, 60, 20);
    }

    public static void sendTitle(Predicate<Player> targets, String title, String subtitle, int in, int stay, int out) {
        players(targets).forEach(player -> sendTitle(player, title, subtitle, in, stay, out));
    }

    public static void sendTitle(Predicate<Player> targets, String title, int in, int stay, int out) {
        sendTitle(targets, title, "", in, stay, out);
    }

    public static void dispatch(CommandSender sender, String command) {
        Bukkit.getScheduler().runTask(Global.instance.getPlugin(), () -> Bukkit.dispatchCommand(sender, command));
    }

    public static void dispatch(String command) {
        dispatch(Bukkit.getConsoleSender(), command);
    }

    public static void dispatchf(String command, Object... args) {
        dispatch(command.formatted(args));
    }
}
