package io.github.itzispyder.pdk.events.listeners;

import io.github.itzispyder.pdk.events.CustomListener;
import io.github.itzispyder.pdk.plugin.gui.CustomGui;
import io.github.itzispyder.pdk.plugin.items.CustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RegistryListeners implements CustomListener {

    @EventHandler
    private void onClick(InventoryClickEvent e) {
        try {
            CustomGui.handleRegistriesClick(e);
        }
        catch (Exception ignore) {}
    }

    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        try {
            CustomGui.handleRegistriesClose(e);
        }
        catch (Exception ignore) {}
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        try {
            CustomItem.handleInteraction(e);
        }
        catch (Exception ignore) {}
    }
}
