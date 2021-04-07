package io.github.levtey.SupremeCobbleGens;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import world.bentobox.bentobox.BentoBox;

public class GenMenu implements InventoryHolder {

	private final SupremeCobbleGens plugin;
	private final Inventory inv;
	private final UUID uuid;
	public final List<String> allowedGens;
	
	public GenMenu(SupremeCobbleGens plugin, Player player) {
		this.plugin = plugin;
		this.uuid = player.getUniqueId();
		allowedGens = allowedGens(player);
		inv = Bukkit.createInventory(this, Math.max(9, ((int) ((allowedGens.size() + 8)/9)) * 9), plugin.makeReadable(plugin.getConfig().getString("gui.name")));
		initInv(player);
		player.openInventory(inv);
	}
	
	private void initInv(Player player) {
		ItemStack fillerItem = plugin.configItem("gui.filler");
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, fillerItem);
		}
		String islandTier = plugin.getTier(BentoBox.getInstance().getIslands().getIslandLocation(plugin.mainWorld, uuid));
		for (int i = 0; i < allowedGens.size(); i++) {
			String allowedGen = allowedGens.get(i);
			Material material = Material.getMaterial(plugin.getConfig().getString("generators." + allowedGen + ".icon").toUpperCase());
			if (material == null) material = Material.COBBLESTONE;
			ItemStack item = new ItemStack(material);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(plugin.makeReadable(plugin.getConfig().getString("generators." + allowedGen + ".name")));
			List<String> lore = new ArrayList<>();
			for (String materialName : plugin.getConfig().getConfigurationSection("generators." + allowedGen + ".blocks").getKeys(false)) {
				lore.add(plugin.makeReadable(
						plugin.getConfig().getString("gui.materialColor") + formatMaterial(materialName) + ": "
						+ plugin.getConfig().getString("gui.chanceColor") + plugin.getConfig().getDouble("generators." + allowedGen + ".blocks." + materialName)
						+ "%"
				));
			}
			meta.setLore(lore);
			if (islandTier == null && i == 0) {
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			} else if (islandTier != null && islandTier.equals(allowedGen)) {
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}
	}
	
	private List<String> allowedGens(Player player) {
		List<String> gens = new ArrayList<>();
		for (String genName : plugin.getConfig().getConfigurationSection("generators").getKeys(false)) {
			if (player.hasPermission("generator." + genName)) gens.add(genName);
		}
		return gens;
	}
	
	private String formatMaterial(String material) {
		String[] split = material.toString().split("_");
		StringJoiner joiner = new StringJoiner(" ");
		for (String word : split) {
			joiner.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
		}
		return joiner.toString();
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
	
}
