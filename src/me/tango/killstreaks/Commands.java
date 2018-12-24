package me.tango.killstreaks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	public static Main plugin;
	public static Main getInstance(){
		return plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (sender instanceof Player) {
			Player p = (Player)sender;
			if (cmd.getName().equalsIgnoreCase("killstreaks")) {
				sendCenteredMessage(p, "§9§m-------------------§9§l[§b§lKillStreaks§9§l]§9§m--------------------");	
				p.sendMessage("");
				
				for(String s : plugin.getInstance().getConfig().getConfigurationSection("KillStreaks").getKeys(false)) {
					String name = plugin.getInstance().getConfig().getString("KillStreaks." + s + ".name");		
					if(Integer.parseInt(s) > 1) {
						sendCenteredMessage(p, "§7[&b&l" + s + " kills§7]" + " " + ChatColor.translateAlternateColorCodes('&', name));
					} else {
						sendCenteredMessage(p, "§7[&b&l" + s + " kill§7]" + " " + ChatColor.translateAlternateColorCodes('&', name));
					}
				}
				
				p.sendMessage("");
				sendCenteredMessage(p, "§9§m---------------------------------------------------");	
				return true;
			}
		}
		return false;
	}
	
	private final int CENTER_PX = 154;

	public void sendCenteredMessage(Player player, String message){
		if(message == null || message.equals("")) player.sendMessage("");
		message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for(char c : message.toCharArray()){
			if(c == '§'){
				previousCode = true;
				continue;
			}else if(previousCode == true){
				previousCode = false;
				if(c == 'l' || c == 'L'){
					isBold = true;
					continue;
				}else isBold = false;
			}else{
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while(compensated < toCompensate){
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb.toString() + message);
	}
	
}
