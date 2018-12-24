package me.tango.killstreaks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;


public class Events implements Listener {

	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	boolean killstreak_enabled = plugin.getInstance().getConfig().getBoolean("killstreaks-enabled");
	boolean killreward_enabled = plugin.getInstance().getConfig().getBoolean("killreward-enabled");
	boolean reset = plugin.getInstance().getConfig().getBoolean("reset-killstreak-enabled");
	int x = plugin.getInstance().getConfig().getInt("reset-seconds");
	String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getInstance().getConfig().getString("prefix"));

	@EventHandler
	public void onKill(PlayerDeathEvent e) {

		if(killreward_enabled) giveKillReward(e.getEntity().getKiller());
		if(!killstreak_enabled) return;

		if(e.getEntity().getKiller() != null && e.getEntity().getKiller().getType() == EntityType.PLAYER) {
			Player killer = e.getEntity().getKiller();
			Player dead = e.getEntity();

			if(plugin.getInstance().kills.containsKey(killer.getUniqueId())) {
				plugin.getInstance().kills.put(killer.getUniqueId(), (plugin.getInstance().kills.get(killer.getUniqueId()) + 1));
			} else {
				plugin.getInstance().kills.put(killer.getUniqueId(), 1);
			}

			for(String s : plugin.getInstance().getConfig().getConfigurationSection("KillStreaks").getKeys(false)) {
				if(plugin.getInstance().kills.get(killer.getUniqueId()) == Integer.parseInt(s))  {
					if(plugin.getInstance().getConfig().contains("KillStreaks." + s + ".commands")) {
						List<String> cmds = plugin.getInstance().getConfig().getStringList("KillStreaks." + s + ".commands");
						for(int i = 0; i < cmds.size(); i++) {
							String replacePlayer = cmds.get(i).replace("%player%", killer.getName());
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacePlayer);
						}
					}

					if(plugin.getInstance().getConfig().contains("KillStreaks." + s + ".items")) {
						List<String> items = plugin.getInstance().getConfig().getStringList("KillStreaks." + s + ".items");
						for (String data1 : items) {
							String[] data2 = data1.split(" : ");
							killer.getInventory().addItem(new ItemStack(Material.getMaterial(data2[0]), Integer.parseInt(data2[1])));
						}
					}
				}
			}
			if(reset) setStreak(killer);
		}
	}

	public void setStreak(Player p) {
		int lastkills = plugin.getInstance().kills.get(p.getUniqueId());		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(plugin.getInstance().kills.containsKey(p.getUniqueId()) && plugin.getInstance().kills.get(p.getUniqueId()) == lastkills) {
					if(plugin.getInstance().kills.get(p.getUniqueId()) > 1) p.sendMessage(prefix + "§7You got a KillStreak of §b§l" + lastkills + " §7!");
					plugin.getInstance().kills.remove(p.getUniqueId());
				} else {
					return;
				}
			}
		}.runTaskLater(plugin.getInstance(), x * 20);

	}

	public void giveKillReward(Player p) {
		if(plugin.getInstance().getConfig().contains("KillReward.commands")) {
			List<String> cmds = plugin.getInstance().getConfig().getStringList("KillReward.commands");
			for(int i = 0; i < cmds.size(); i++) {
				String replacePlayer = cmds.get(i).replace("%player%", p.getName());
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), replacePlayer);
			}
		}

		if(plugin.getInstance().getConfig().contains("KillReward.items")) {
			List<String> items = plugin.getInstance().getConfig().getStringList("KillReward.items");
			for (String data1 : items) {
				String[] data2 = data1.split(" : ");
				p.getInventory().addItem(new ItemStack(Material.getMaterial(data2[0]), Integer.parseInt(data2[1])));
			}
		}
	}
	/*
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if(e.getBlock().getType() == Material.CARPET && e.getBlock().getData() == 4){
			Bukkit.broadcastMessage("Carpet broken");
		}

	}
	*/
	
}
