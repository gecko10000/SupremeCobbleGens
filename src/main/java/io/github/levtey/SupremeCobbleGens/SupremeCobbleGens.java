package io.github.levtey.SupremeCobbleGens;

import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.database.objects.Island;

public class SupremeCobbleGens extends JavaPlugin {
	
	public final String metaKey = "generator";
	public final Random random = new Random();
	protected World mainWorld;
	
	public void onEnable() {
		Bukkit.getScheduler().runTask(this, () -> {
			mainWorld = Bukkit.getWorld("skyblock");
		});
		saveDefaultConfig();
		new ClickListener(this);
		new GenerationListener(this);
		new GenCommand(this);
	}
	
	public String getTier(Location location) {
		Island generatingIsland = BentoBox.getInstance().getIslands().getProtectedIslandAt(location).orElse(null);
		if (generatingIsland == null) return null;
		Island mainIsland = BentoBox.getInstance().getIslands().getIsland(mainWorld, generatingIsland.getOwner());
		if (mainIsland == null) return null;
		MetaDataValue value = mainIsland.getMetaData(metaKey).orElse(null);
		return value == null ? null : value.asString();
	}
	
	public boolean setTier(OfflinePlayer player, String tier) {
		if (tier == null) return false;
		Island island = BentoBox.getInstance().getIslands().getIsland(mainWorld, player.getUniqueId());
		if (island == null) return false;
		island.putMetaData(metaKey, new MetaDataValue(tier));
		return true;
	}
	
	public ItemStack configItem(String path) {
		Material material = Material.getMaterial(getConfig().getString(path + ".material").toUpperCase());
		if (material == null) material = Material.COBBLESTONE;
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(makeReadable(getConfig().getString(path + ".name")));
		meta.setLore(getConfig().getStringList(path + ".lore").stream().map(this::makeReadable).collect(Collectors.toList()));
		item.setItemMeta(meta);
		return item;
	}
	
	public String makeReadable(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

}
