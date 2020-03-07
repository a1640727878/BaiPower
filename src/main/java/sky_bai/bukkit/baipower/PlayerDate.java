package sky_bai.bukkit.baipower;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public class PlayerDate {
	public static Map<Player, Integer> playerPower = new HashMap<Player, Integer>();
	public static Map<Player, Integer> playerSprint = new HashMap<Player, Integer>();
	public static Map<Player, Integer> playerAttack = new HashMap<Player, Integer>();
	public static Map<Player, Integer> playerBreak = new HashMap<Player, Integer>();
	public static Map<Player, Boolean> playerHideBossBar = new HashMap<Player, Boolean>();

	public static void add(Player player) {
		playerPower.put(player, BPConfig.maxPower);
		playerSprint.put(player, 0);
		playerAttack.put(player, 0);
		playerBreak.put(player, 0);
		playerHideBossBar.put(player, false);
	}

	public static void del(Player player) {
		playerPower.remove(player);
		playerSprint.remove(player);
		playerAttack.remove(player);
		playerBreak.remove(player);
		playerHideBossBar.remove(player);
	}

	public static Boolean getHideBossBar(Player player) {
		return playerHideBossBar.get(player);
	}

	public static void setHideBossBar(Player player, Boolean b) {
		playerHideBossBar.put(player, b);
	}

	public static Integer getPower(Player player) {
		return playerPower.get(player);
	}

	public static void setPower(Player player, Integer power) {
		playerPower.put(player, power);
	}

	public static void addPower(Player player, Integer power) {
		playerPower.put(player, playerPower.get(player) + power);
	}

	public static void subPower(Player player, Integer power) {
		Integer i = playerAttack.get(player) > 0 ? 2 : 1;
		playerPower.put(player, playerPower.get(player) - (power * i));
	}
}
