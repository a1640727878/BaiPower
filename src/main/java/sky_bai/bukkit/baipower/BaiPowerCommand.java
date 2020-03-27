package sky_bai.bukkit.baipower;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BaiPowerCommand implements CommandExecutor {
	public static Boolean debug = false;

	public boolean onCommand(CommandSender sender, Command command, String str, String[] strs) {
		if (strs[0].toLowerCase().equalsIgnoreCase("reload")) {
			Config.reload();
			ConfigPlayerDate.reload();
			return true;
		}
		if (strs[0].toLowerCase().equalsIgnoreCase("debug")) {
			debug = !debug;
			return true;
		}
		if (strs[0].toLowerCase().equalsIgnoreCase("setMaxP".toLowerCase())) {
			Player player = Bukkit.getPlayer(strs[1]);
			Integer i = Integer.valueOf(strs[2]);
			PlayerDate.get(player).setMaxPower(i);
			sender.sendMessage(player.getName() + "的 MaxPower 设置为" + PlayerDate.get(player).getMaxPower());
			return true;
		}
		if (strs[0].toLowerCase().equalsIgnoreCase("setRP".toLowerCase())) {
			Player player = Bukkit.getPlayer(strs[1]);
			Integer i = Integer.valueOf(strs[2]);
			PlayerDate.get(player).setRegainPowerValue(i);
			sender.sendMessage(player.getName() + "的 RegainPower 设置为" + PlayerDate.get(player).getRegainPowerValue());
			return true;
		}
		return false;
	}
}
