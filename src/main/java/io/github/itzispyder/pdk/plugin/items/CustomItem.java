package io.github.itzispyder.pdk.plugin.items;

import io.github.itzispyder.pdk.utils.misc.Voidable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public interface CustomItem {

    Map<Class<? extends CustomItem>, CustomItem> items = new HashMap<>();
    Map<String, CustomItem> registry = new HashMap<>();

    ItemStack getItem();
    void onInteract(Player player, Action action, ItemStack item, PlayerInteractEvent event);

    default ItemStack register() {
        return register(this);
    }

    default String getName() {
        String[] name = {"unnamed-custom-item"};
        Voidable.of(this.getClass().getAnnotation(ItemRegistry.class)).accept(registry -> name[0] = registry.value());
        return name[0];
    }

    static Map<String, CustomItem> getRegistry() {
        return new HashMap<>(registry);
    }

    static boolean matchDisplay(ItemStack a, ItemStack b) {
        return getDisplay(a).equals(getDisplay(b));
    }

    static boolean matchDisplay(String a, ItemStack b) {
        return a.equals(getDisplay(b));
    }

    static boolean matchDisplay(ItemStack a, String b) {
        return getDisplay(a).equals(b);
    }

    static <T extends CustomItem> T get(Class<T> key) {
        return (T)items.get(key);
    }

    static String getDisplay(ItemStack item) {
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

    static void handleInteraction(PlayerInteractEvent event) {
        String display = getDisplay(event.getItem());
        if (registry.containsKey(display)) {
            registry.get(display).onInteract(event.getPlayer(), event.getAction(), event.getItem(), event);
        }
    }

    private static ItemStack register(CustomItem customItem) {
        ItemStack item = customItem.getItem();
        items.put(customItem.getClass(), customItem);
        registry.put(getDisplay(item), customItem);
        return item;
    }
}
