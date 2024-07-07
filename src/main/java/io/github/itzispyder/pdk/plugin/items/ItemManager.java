package io.github.itzispyder.pdk.plugin.items;

import com.google.gson.Gson;
import io.github.itzispyder.pdk.Global;
import io.github.itzispyder.pdk.plugin.builders.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemManager {

    private static final Gson gson = new Gson();
    private static final Map<NamespacedKey, Supplier<CustomItem>> itemRegistry = new HashMap<>();

    public static NamespacedKey registerItem(String namespace, Supplier<CustomItem> context) {
        NamespacedKey key = new NamespacedKey(Global.instance.getPlugin(), "items/" + namespace);
        itemRegistry.put(key, context);
        return key;
    }

    public static ItemStack createItemContext(String namespace) {
        NamespacedKey key = new NamespacedKey(Global.instance.getPlugin(), "items/" + namespace);
        Supplier<CustomItem> registry = itemRegistry.get(key);
        if (registry == null)
            throw new IllegalArgumentException("context not found in item registry");

        CustomItem context = registry.get();
        ItemBuilder builder = ItemBuilder.create();
        context.createItem(builder);

        ItemStack result = builder.build();
        if (result == null || !result.hasItemMeta())
            throw new IllegalArgumentException("the result item cannot be used as a custom item");

        ItemMeta meta = result.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        data.set(key, PersistentDataType.STRING, gson.toJson(context));
        context.updateMeta(meta);
        result.setItemMeta(meta);

        return result;
    }

    public static void setItemContext(ItemStack item, CustomItem context) {
        if (item == null || !item.hasItemMeta())
            return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        for (NamespacedKey key : data.getKeys()) {
            Supplier<CustomItem> contextSupplier = itemRegistry.get(key);
            if (contextSupplier == null)
                continue;

            CustomItem customItem = contextSupplier.get();
            if (customItem != null && customItem.getClass() == context.getClass()) {
                data.set(key, PersistentDataType.STRING, gson.toJson(context));
                context.updateMeta(meta);
                item.setItemMeta(meta);
                return;
            }
        }
    }

    @SuppressWarnings({ "unchecked", "unused" })
    public static <C extends CustomItem> C getItemContext(ItemStack item, Class<C> type) {
        if (item == null || !item.hasItemMeta())
            return null;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        for (NamespacedKey key : data.getKeys()) {
            Supplier<CustomItem> contextSupplier = itemRegistry.get(key);
            if (contextSupplier == null)
                continue;

            CustomItem customItem = contextSupplier.get();
            if (customItem != null) {
                CustomItem parsed = gson.fromJson(data.get(key, PersistentDataType.STRING), customItem.getClass());
                if (parsed != null)
                    return (C)parsed;
            }
        }

        return null;
    }

    public static <C extends CustomItem> C getOrDefItemContext(ItemStack item, Class<C> type, C fallback) {
        C context = getItemContext(item, type);
        if (context != null)
            return context;

        NamespacedKey key = getKeyOf(fallback);
        if (key == null)
            return fallback;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();

        data.set(key, PersistentDataType.STRING, gson.toJson(fallback));
        fallback.updateMeta(meta);
        item.setItemMeta(meta);
        return fallback;
    }

    public static NamespacedKey getKeyOf(CustomItem context) {
        if (context == null)
            return null;

        for (Map.Entry<NamespacedKey, Supplier<CustomItem>> entry : itemRegistry.entrySet())
            if (entry.getValue().get().getClass() == context.getClass())
                return entry.getKey();
        return null;
    }

    public static CustomItem getItemContext(ItemStack item) {
        return getItemContext(item, CustomItem.class);
    }

    public static boolean isHolding(Player player, NamespacedKey item) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        return isItemMatching(item, stack);
    }

    public static boolean isOffHolding(Player player, NamespacedKey item) {
        ItemStack stack = player.getInventory().getItemInOffHand();
        return isItemMatching(item, stack);
    }

    public static boolean isHoldingAny(Player player, NamespacedKey item) {
        return isHolding(player, item) || isOffHolding(player, item);
    }

    public static boolean isItemMatching(NamespacedKey key, ItemStack item) {
        if (item == null || !item.hasItemMeta())
            return false;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(key);
    }

    public static String[] collectNames() {
        String[] str = new String[itemRegistry.size()];
        int i = 0;
        for (NamespacedKey key : itemRegistry.keySet())
            str[i++] = key.getKey().replaceFirst("items/", "");
        return str;
    }
}