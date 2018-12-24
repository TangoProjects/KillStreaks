package me.tango.killstreaks;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
	
	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}
	
	public Map<UUID,Integer> kills = new HashMap<>();

	@Override
	public void onEnable() {
		plugin = this;
		
		loadConfig();
		
		getCommand("killstreaks").setExecutor((CommandExecutor) new Commands());
		getServer().getPluginManager().registerEvents(new Events(), this);

	}
	
	@Override
	public void onDisable() {
		
	}
	
	private void loadConfig() {
		try {
			if (!getDataFolder().exists()) getDataFolder().mkdirs();
			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) saveDefaultConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
