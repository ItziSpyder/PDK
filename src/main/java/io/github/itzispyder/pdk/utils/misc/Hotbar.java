package io.github.itzispyder.pdk.utils.misc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;
import java.util.function.Predicate;

public class Hotbar {

    private ItemStack[] contents;
    private ItemStack offhandStack;
    private UUID owner;

    private Hotbar(Player player) {
        PlayerInventory inv = player.getInventory();
        this.contents = new ItemStack[9];
        this.offhandStack = inv.getItemInOffHand();
        this.owner = player.getUniqueId();

        for (int i = 0; i < 9; i ++) {
            ItemStack item = inv.getItem(i);
            contents[i] = item != null ? item : new ItemStack(Material.AIR);
        }
    }

    public Hotbar(ItemStack offhand, ItemStack... items) {
        this.contents = new ItemStack[9];
        this.offhandStack = offhand != null ? offhand : new ItemStack(Material.AIR);
        this.owner = null;

        int i = 0;
        for (ItemStack item : items) {
            if (i >= 9) {
                break;
            }
            contents[i] = item != null ? item : new ItemStack(Material.AIR);
            i++;
        }
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setOffhandStack(ItemStack offhandStack) {
        this.offhandStack = offhandStack;
    }

    public ItemStack getOffhandStack() {
        return offhandStack;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Voidable<Player> getOwningPlayer() {
        Player p = Bukkit.getPlayer(owner);
        if (p == null || !p.isOnline() || p.isDead()) {
            return Voidable.of(null);
        }
        return Voidable.of(p);
    }

    public boolean contains(Predicate<ItemStack> predicate) {
        if (offhandStack != null && predicate.test(offhandStack)) {
            return true;
        }
        for (ItemStack content : contents) {
            if (content != null && predicate.test(content)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Material type) {
        return contains(item -> item.getType() == type);
    }

    public boolean contains(ItemStack item) {
        return contains(item::equals);
    }

    public ItemStack search(Predicate<ItemStack> predicate) {
        for (ItemStack content : contents) {
            if (content != null && predicate.test(content)) {
                return content;
            }
        }
        if (offhandStack != null && predicate.test(offhandStack)) {
            return offhandStack;
        }
        return null;
    }

    public ItemStack search(Material type) {
        return search(item -> item.getType() == type);
    }

    public ItemStack search(ItemStack item) {
        return search(item::equals);
    }

    public void deduct(Predicate<ItemStack> predicate, int amount, boolean ignoreGamemode) {
        getOwningPlayer().accept(player -> {
            if (!ignoreGamemode && player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            Voidable.of(search(predicate)).accept(item -> item.setAmount(Math.max(item.getAmount() - amount, 0)));
        });
    }

    public void deduct(Material type, int amount, boolean ignoreGamemode) {
        deduct(item -> item.getType() == type, amount, ignoreGamemode);
    }

    public void deduct(Material type, boolean ignoreGamemode) {
        deduct(type, 1, ignoreGamemode);
    }

    public void deduct(ItemStack item, int amount, boolean ignoreGamemode) {
        deduct(item::equals, amount, ignoreGamemode);
    }

    /**
     * Does not include offhand
     * @return first slot to be empty, otherwise -1
     */
    public int firstEmpty() {
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public static void set(Player player, Hotbar hotbar) {
        PlayerInventory inv = player.getInventory();
        ItemStack[] hot = hotbar.getContents();

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, hot[i]);
        }
        inv.setItemInOffHand(hotbar.getOffhandStack());
    }

    public static Hotbar from(Player player) {
        return new Hotbar(player);
    }
}