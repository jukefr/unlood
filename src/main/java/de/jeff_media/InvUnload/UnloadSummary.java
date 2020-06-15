package de.jeff_media.InvUnload;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnloadSummary {
	
	Player p;
	HashMap<Location,EnumMap<Material,Integer>> unloads;
	
	UnloadSummary(Player p) {
		this.p=p;
		unloads = new HashMap<Location,EnumMap<Material,Integer>>();
	}
	
	void unloaded(Location loc, Material mat, int amount) {
		if(!unloads.containsKey(loc)) {
			unloads.put(loc, new EnumMap<>(Material.class));
			unloads.get(loc).put(mat, amount);
		} else {
			if(unloads.get(loc).containsKey(mat)) {
				unloads.get(loc).put(mat, unloads.get(loc).get(mat)+amount);
			} else {
				unloads.get(loc).put(mat, amount);
			}
		}
	}
	
	String loc2str(Location loc) {
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		Material type = loc.getBlock().getType();
		return String.format(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "%s @ §aX: §f%d §aY: §f%d §aZ: §f%d", type.name(),x,y,z);
	}
	
	String amount2str(int amount) {
		return String.format("%6dx  ", amount);
	}
	
	void print(PrintRecipient recipient, Player p) {
		for(Entry<Location,EnumMap<Material,Integer>> entry : unloads.entrySet()) {
			printTo(recipient,p,loc2str(entry.getKey()));
			EnumMap<Material,Integer> map = entry.getValue();
			for(Entry<Material,Integer> entry2 : map.entrySet()) {
				printTo(recipient,p,
						ChatColor.DARK_GRAY + amount2str(entry2.getValue()) + ChatColor.GRAY + entry2.getKey().name());
			}
		}
	}
	
	enum PrintRecipient {
		PLAYER, CONSOLE
	}
	
	void printTo(PrintRecipient recipient, Player p, String text) {
		if(recipient == PrintRecipient.CONSOLE) {
			System.out.println(text);
		} else {
			p.sendMessage(text);
		}
	}
	
	

}
