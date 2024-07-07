package io.github.itzispyder.pdk.utils.nbt;

import io.github.itzispyder.pdk.Global;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentData {

    private final PersistentDataContainer data;

    public PersistentData(PersistentDataContainer data) {
        this.data = data;
    }

    public void write(String namespace, PersistentDataSerializable obj) {
        if (namespace == null || namespace.isEmpty())
            return;
        if (obj == null) {
            remove(namespace);
            return;
        }

        NamespacedKey key = new NamespacedKey(Global.instance.getPlugin(), namespace);
        String json = obj.serialize();

        data.set(key, PersistentDataType.STRING, json);
    }

    public void remove(String namespace) {
        NamespacedKey key = new NamespacedKey(Global.instance.getPlugin(), namespace);
        data.remove(key);
    }

    public <T extends PersistentDataSerializable> T read(String namespace, Class<T> type) {
        return read(namespace, type, null);
    }

    public <T extends PersistentDataSerializable> T read(String namespace, Class<T> type, T fallback) {
        try {
            NamespacedKey key = new NamespacedKey(Global.instance.getPlugin(), namespace);
            String json = data.get(key, PersistentDataType.STRING);
            T obj = PersistentDataSerializable.gson.fromJson(json, type);
            return obj != null ? obj : fallback;
        }
        catch (Exception ex) {
            return fallback;
        }
    }

    public <T extends PersistentDataSerializable> boolean valid(String namespace, Class<T> type) {
        return read(namespace, type, null) != null;
    }

    public boolean has(String namespace) {
        NamespacedKey key = new NamespacedKey(Global.instance.getPlugin(), namespace);
        return data.has(key);
    }
}