package io.github.itzispyder.pdk.plugin.items;

import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomItem {

    private static final Map<Class<? extends CustomItem>, CustomItem> items = new HashMap<>();
    private static final Map<String, CustomItem> registry = new HashMap<>();
    private ItemStack item;
    private String name;

    public CustomItem(String name) {
        this.name = name;
    }

    public abstract void buildItem(ItemBuilder builder);
    public abstract void onInteract(Player player, Action action, ItemStack item, PlayerInteractEvent event);

    public static void handleInteraction(PlayerInteractEvent event) {
        String display = getDisplay(event.getItem());
        if (registry.containsKey(display)) {
            registry.get(display).onInteract(event.getPlayer(), event.getAction(), event.getItem(), event);
        }
    }

    public ItemStack register() {
        return register(this);
    }

    public static <T extends CustomItem> T get(Class<T> key) {
        return (T)items.get(key);
    }

    private static ItemStack register(CustomItem customItem) {
        ItemBuilder builder = ItemBuilder.create();
        customItem.buildItem(builder);
        ItemStack item = builder.build();

        customItem.setItem(item);
        items.put(customItem.getClass(), customItem);
        registry.put(getDisplay(item), customItem);
        return item;
    }

    public ItemStack getItem() {
        return item;
    }

    public static String getDisplay(ItemStack item) {
        if (item == null) {
            return "";
        }
        else if (item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        else {
            return item.getType().name().toLowerCase();
        }
    }

    public String getName() {
        return name;
    }

    private void setItem(ItemStack item) {
        this.item = item;
    }

    public static Map<String, CustomItem> getRegistry() {
        return new HashMap<>(registry);
    }

    public static boolean matchDisplay(ItemStack a, ItemStack b) {
        return getDisplay(a).equals(getDisplay(b));
    }

    public static boolean matchDisplay(String a, ItemStack b) {
        return a.equals(getDisplay(b));
    }

    public static boolean matchDisplay(ItemStack a, String b) {
        return getDisplay(a).equals(b);
    }
}
