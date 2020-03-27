package sky_bai.bukkit.baipower;

import java.io.File;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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

	public static Integer getPlayerQuitPower(Player player) {
		loadConfig();
		Integer I = config.getInt(player.getName() + ".PlayerQuitPower", Config.Date.DefaultMaxPower);
		return I == -1 ? Config.Date.DefaultMaxPower : I;
	}

	public static void setPlayerQuitPower(Player player, Integer i) {
		config.set(player.getName() + ".PlayerQuitPower", i);
		save();
	}

	public static Integer getMaxPower(Player player) {
		loadConfig();
		return config.getInt(player.getName() + ".MaxPower", Config.Date.DefaultMaxPower);
	}

	public static void setMaxPower(Player player, Integer i) {
		config.set(player.getName() + ".MaxPower", i);
		save();
	}

	public static Integer getRegainPowerValue(Player player) {
		loadConfig();
		return config.getInt(player.getName() + ".PowerRegain", Config.Date.PowerRegain_Value);
	}

	public static void setRegainPowerValue(Player player, Integer i) {
		config.set(player.getName() + ".PowerRegain", i);
		save();
	}

	public static void reload() {
		loadConfig();
		Set<String> keys = config.getKeys(false);
		for (String key : keys) {
			ConfigurationSection cSection = config.getConfigurationSection(key);
			Player player = Bukkit.getPlayer(key);
			PlayerDate.Date date = PlayerDate.get(player);
			date.setMaxPower(cSection.getInt("MaxPower", Config.Date.DefaultMaxPower));
			date.setRegainPowerValue(cSection.getInt("PowerRegain", Config.Date.PowerRegain_Value));
		}
	}

	public static void save() {
		try {
			config.save(configFile);
		} catch (Throwable e) {
		}
	}

	public static void loadConfig() {
		BaiPower.getInstance().getDataFolder().mkdirs();
		try {
			configFile.createNewFile();
			config.load(configFile);
		} catch (Throwable e) {
		}
	}
}
