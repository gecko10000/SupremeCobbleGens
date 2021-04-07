package io.github.levtey.SupremeCobbleGens;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;

public class ClickListener implements Listener {

	private SupremeCobbleGens plugin;
	
	public ClickListener(SupremeCobbleGens plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void on(InventoryClickEvent evt) {
		HumanEntity ent = evt.getWhoClicked();
		if (!(ent instanceof Player)) return;
		InventoryHolder holder = evt.getInventory().getHolder();
		if (!(holder instanceof GenMenu)) return;
		evt.setCancelled(true);
		Inventory clickedInv = evt.getClickedInventory();
		if (clickedInv == null || clickedInv instanceof PlayerInventory) return;
		GenMenu menu = (GenMenu) holder;
		Player player = (Player) ent;
		int clickedSlot = evt.getSlot();
		if (menu.allowedGens.size() <= clickedSlot) return;
		if (plugin.setTier(player, menu.allowedGens.get(clickedSlot))) {
			new GenMenu(plugin, player);
		}
	}
	
}
