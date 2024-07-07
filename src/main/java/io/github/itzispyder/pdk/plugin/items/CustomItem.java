package io.github.itzispyder.pdk.plugin.items;

import com.google.gson.Gson;
import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class CustomItem implements Global {

    public abstract void createItem(ItemBuilder item);

    public abstract void updateMeta(ItemMeta meta);

    public abstract void onInteract(PlayerInteractEvent e);

    public String serialize() {
        try {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
        catch (Exception ex) {
            return "{}";
        }
    }

    public String getRegistryKey() {
        ItemRegistry r = this.getClass().getAnnotation(ItemRegistry.class);
        if (r == null)
            throw new IllegalArgumentException("Custom items need to have @ItemRegistry annotation!");
        return r.value();
    }

    public void register(Object... initArgs) {
        try {
            Class<?>[] signature = Arrays.stream(initArgs).map(Object::getClass).toArray(Class<?>[]::new);
            CustomItem item = this.getClass().getDeclaredConstructor(signature).newInstance(initArgs);
            ItemManager.registerItem(getRegistryKey(), () -> item);
        }
        catch (Exception ex) {
            throw new RuntimeException("Failed to register custom item: " + ex.getMessage());
        }
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