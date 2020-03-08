package sky_bai.bukkit.baipower;

import java.io.File;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ConfigPlayerDate {
	private final static File configFile = new File(BaiPower.getInstance().getDataFolder(), "PlayerDate.yml");
	private static FileConfiguration config = new YamlConfiguration();

	public static File getConfigfile() {
		return configFile;
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static void setMaxPower(Player player, Integer i) {
		String uuid = player.getUniqueId().toString();
		config.set(uuid+".MaxPower", i);
		save();
	}

	public static void setRegainPowerValue(Player player, Integer i) {
		String uuid = player.getUniqueId().toString();
		config.set(uuid+".PowerRegain", i);
		save();
	}

	public static void reload() {
		loadConfig();
		Set<String> keys = config.getKeys(false);
		for (String key : keys) {
			Player player = Bukkit.getPlayer(key);
			PlayerDate.Date date = PlayerDate.get(player);
			date.setMaxPower(config.getInt(key+".MaxPower", Config.Date.DefaultMaxPower));
			date.setRegainPowerValue(config.getInt(key + ".PowerRegain", Config.Date.PowerRegain_Value));
		}
	}

	public static void save() {
		try {
			config.save(configFile);
		} catch (Throwable e) {
		}
	}

	private static void loadConfig() {
		BaiPower.getInstance().getDataFolder().mkdirs();
		try {
			configFile.createNewFile();
			config.load(configFile);
		} catch (Throwable e) {
		}
	}
}
