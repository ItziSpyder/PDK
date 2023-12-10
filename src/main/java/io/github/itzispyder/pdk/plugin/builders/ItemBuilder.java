package io.github.itzispyder.pdk.plugin.builders;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ItemBuilder {

    private ItemStack stack;
    private ItemMeta meta;

    public ItemBuilder() {
        this(new ItemStack(Material.STONE));
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
        this.meta = this.stack.getItemMeta();
    }

    public ItemBuilder material(Material m) {
        stack.setType(m);
        return this;
    }

    public ItemBuilder name(String s) {
        meta.setDisplayName(s);
        return this;
    }

    public ItemBuilder lore(String s) {
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.add(s);
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder lore(Iterable<String> s) {
        s.forEach(this::lore);
        return this;
    }

    public ItemBuilder count(int c) {
        stack.setAmount(c);
        return this;
    }

    public ItemBuilder enchant(Enchantment e, int level) {
        meta.addEnchant(e,level,true);
        return this;
    }

    public ItemBuilder flag(ItemFlag... f) {
        meta.addItemFlags(f);
        return this;
    }

    public ItemBuilder attribute(Attribute a, AttributeModifier am) {
        meta.addAttributeModifier(a,am);
        return this;
    }

    public ItemBuilder unbreakable(boolean b) {
        meta.setUnbreakable(b);
        return this;
    }

    public ItemBuilder customModelData(int d) {
        meta.setCustomModelData(d);
        return this;
    }

    public ItemBuilder runTaskItem(Function<ItemStack, ItemStack> task) {
        this.stack = task.apply(build());
        return this;
    }

    public ItemBuilder runTaskMeta(Function<ItemMeta, ItemMeta> task) {
        this.meta = task.apply(meta);
        return this;
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemBuilder create() {
        return new ItemBuilder();
    }

    public static ItemBuilder create(ItemStack item) {
        return new ItemBuilder(item);
    }
}