package io.github.itzispyder.pdk.plugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CustomGui {

    private static final Map<String, CustomGui> registry = new HashMap<>();
    private final Map<Integer, InvAction> slotActions;
    private final Map<Integer, ItemStack> slotDisplays;
    private final InvAction mainAction;
    private final CreateAction createAction;
    private final CloseAction closeAction;
    private final String title;
    private final int size;

    public CustomGui(String title, int size, InvAction mainAction, Map<Integer, InvAction> slotActions, Map<Integer, ItemStack> slotDisplays, CreateAction createAction, CloseAction closeAction) {
        this.slotActions = slotActions;
        this.slotDisplays = slotDisplays;
        this.mainAction = mainAction;
        this.createAction = createAction;
        this.closeAction = closeAction;
        this.title = title;
        this.size = size;
    }

    public static CustomGui register(CustomGui gui) {
        if (gui != null) {
            registry.put(gui.getTitle(), gui);
        }
        return gui;
    }

    public static Map<String, CustomGui> registries() {
        return new HashMap<>(registry);
    }

    public static void handleRegistriesClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (registry.containsKey(title)) {
            registry.get(title).onInventoryClick(event);
        }
    }

    public static void handleRegistriesClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (registry.containsKey(title)) {
            registry.get(title).onInventoryClose(event);
        }
    }

    public Inventory getInventory() {
        int size = this.size;

        if (size % 9 != 0) {
            int max = slotActions.keySet().stream().sorted(Comparator.comparing(i -> (int)i).reversed()).toList().get(0);
            int add = max % 9 == 0 ? 0 : 1;
            size = (int)(Math.floor(max / 9.0) + add) * 9;
        }

        Inventory inv = Bukkit.createInventory(null, size, title);
        createAction.onCreate(inv);
        slotDisplays.forEach(inv::setItem);

        return inv;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.isCancelled() && event.getClickedInventory() != null && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            mainAction.onClick(event);
            if (slotActions.containsKey(event.getSlot())) {
                slotActions.get(event.getSlot()).onClick(event);
            }
        }
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        closeAction.onClose(event);
    }

    public String getTitle() {
        return title;
    }

    public Map<Integer, InvAction> getSlotActions() {
        return slotActions;
    }

    public Map<Integer, ItemStack> getSlotDisplays() {
        return slotDisplays;
    }

    public CreateAction getCreateAction() {
        return createAction;
    }

    public CloseAction getCloseAction() {
        return closeAction;
    }

    public int getInvSize() {
        return size;
    }



    public static GuiBuilder create() {
        return new GuiBuilder();
    }

    public static class GuiBuilder {
        private InvAction mainAction;
        private CreateAction createAction;
        private CloseAction closeAction;
        private final Map<Integer, InvAction> slotActions;
        private final Map<Integer, ItemStack> slotDisplay;
        private String title;
        private int size;

        public GuiBuilder() {
            this.title = "Untitled Inventory";
            this.size = -1;
            this.mainAction = event -> {};
            this.createAction = inv -> {};
            this.closeAction = event -> {};
            this.slotActions = new HashMap<>();
            this.slotDisplay = new HashMap<>();
        }

        public GuiBuilder title(String text) {
            title = text;
            return this;
        }

        public GuiBuilder size(int size) {
            this.size = size;
            return this;
        }

        public GuiBuilder onDefine(CreateAction action) {
            createAction = action;
            return this;
        }

        public GuiBuilder onClose(CloseAction action) {
            closeAction = action;
            return this;
        }

        public GuiBuilder defineMain(InvAction mainAction) {
            this.mainAction = mainAction;
            return this;
        }

        public GuiBuilder define(int slot, ItemStack display, InvAction action) {
            if (slot < 0 || slot >= 54 || display == null || action == null) return this;
            slotActions.put(slot, action);
            slotDisplay.put(slot, display);
            return this;
        }

        public GuiBuilder define(int slot, ItemStack display) {
            return define(slot, display, event -> {});
        }

        public CustomGui build() {
            CustomGui gui = new CustomGui(title, size, mainAction, slotActions, slotDisplay, createAction, closeAction);
            CustomGui.register(gui);
            return gui;
        }
    }

    @FunctionalInterface
    public interface InvAction {
        void onClick(InventoryClickEvent event);
    }

    @FunctionalInterface
    public interface CloseAction {
        void onClose(InventoryCloseEvent event);
    }

    @FunctionalInterface
    public interface CreateAction {
        void onCreate(Inventory inv);
    }
}
