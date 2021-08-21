package io.github.levtey.SupremeCobbleGens;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.database.objects.Island;

public class GenerationListener implements Listener {
	
	private SupremeCobbleGens plugin;
	private final Set<BlockFace> waterFaces = EnumSet.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP);
	private final Set<BlockFace> lavaFaces = EnumSet.of(BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
	
	public GenerationListener(SupremeCobbleGens plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onLavaFlow(BlockFromToEvent evt) {
		final Block source = evt.getBlock();
		if (source.getType() != Material.LAVA) return;
		final Block dest = evt.getToBlock();
		final Material destType = dest.getType();
		boolean validCobble = false;
		if (destType == Material.WATER) {
			validCobble = true;
		} else if (destType == Material.AIR
				|| destType == Material.CAVE_AIR
				|| destType == Material.VOID_AIR) {
			for (BlockFace face : waterFaces) {
				Block relative = dest.getRelative(face);
				if (relative.getBlockData() instanceof Waterlogged && ((Waterlogged) relative.getBlockData()).isWaterlogged()) {
					validCobble = true;
					break;
				}
				if (relative.getType() != Material.WATER) continue;
				validCobble = true;
				break;
			}
		}
		if (!validCobble) return;
		String tier = plugin.getTier(dest.getLocation());
		Material toSet = pickMaterial(tier);
		if (toSet == null) return;
		evt.setCancelled(true);
		dest.setType(toSet);
	}
	
	@EventHandler
	public void onWaterFlow(BlockFromToEvent evt) {
		final Block source = evt.getBlock();
		if (source.getType() != Material.WATER) return;
		final Block dest = evt.getToBlock();
		// go over NESW faces and down
		for (BlockFace face : lavaFaces) {
			Block relative = dest.getRelative(face);
			// ignore non-lava
			if (relative.getType() != Material.LAVA) continue;
			Levelled lava = (Levelled) relative.getBlockData();
			//check if it's a source block and will make obsidian
			if (lava.getLevel() == 0) continue;
			String tier = plugin.getTier(relative.getLocation());
			Material toSet = pickMaterial(tier);
			if (toSet == null) continue;
			relative.setType(toSet);
		}
	}
	
	private Material pickMaterial(String tier) {
		if (tier == null) return null;
		double runningTotal = 0;
		double choice = plugin.random.nextDouble() * 100;
		Set<String> keys = plugin.getConfig().getConfigurationSection("generators." + tier + ".blocks").getKeys(false);
		for (String materialName : keys) {
			runningTotal += plugin.getConfig().getDouble("generators." + tier + ".blocks." + materialName);
			if (choice < runningTotal) {
				return Material.getMaterial(materialName.toUpperCase());
			}
		}
		return null;
	}

}
