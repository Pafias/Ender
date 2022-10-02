package me.pafias.ender.gui;

import me.pafias.ender.Ender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiMenu implements Listener {

    public final Ender plugin = Ender.get();

    public Player player;
    private Inventory inventory;
    private String title;
    private int size;
    private List<ItemStack> items;
    private boolean closeOnClick = true;

    public GuiMenu(Player player, String title, int size) {
        this(player, title, size, new ArrayList<>());
    }

    public GuiMenu(Player player, String title, int size, List<ItemStack> items) {
        this.player = player;
        this.title = title;
        this.size = size;
        this.items = items;
        inventory = plugin.getServer().createInventory(null, size, title);
        items.forEach(item -> inventory.addItem(item));
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    public void open() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        player.openInventory(inventory);
    }

    public void update() {
        close();
        open();
    }

    public void close() {
        if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() == inventory)
            player.closeInventory();
        HandlerList.unregisterAll(this);
    }

    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
    }

    public abstract void clickHandler(ItemStack item, int slot);

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(inventory)) return;
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (event.getCurrentItem() == null) return;
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        clickHandler(event.getCurrentItem(), event.getSlot());
        if (closeOnClick)
            close();
    }

}
