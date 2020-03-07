package sky_bai.bukkit.baipower;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BPConfig {
	static BPConfig bpConfig;

	public final static File configFile = new File(BaiPower.getInstance().getDataFolder(), "config.yml");

	public static Integer maxPower = 100;
	public static Integer powerRegain = 1;
	public static Integer event_Break = 1;
	public static Integer event_Sprint = 1;
	public static Integer event_Attack = 1;

	public static Map<Material, Integer> materials = new HashMap<Material, Integer>();

	public static Set<World> worldBlackList = new HashSet<World>();

	private FileConfiguration config = new YamlConfiguration();

	public BPConfig() {
		try {
			reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void reload() {
		try {
			bpConfig.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reset() throws Exception {
		BaiPower.getInstance().getDataFolder().mkdirs();
		configFile.createNewFile();
		config.load(configFile);

		if (config.getKeys(false).size() > 0) {
			maxPower = config.getInt("MaxPower", 100);
			powerRegain = config.getInt("PowerRegain", 1);
			event_Break = config.getInt("Event.Break.Default", 1);
			Set<String> keys = config.getConfigurationSection("Event.Break.List").getKeys(false);
			materials.clear();
			for (String key : keys) {
				Material material = Material.getMaterial(key);
				if (material == null || material.isBlock() == false) {
					continue;
				}
				materials.put(material, config.getConfigurationSection("Event.Break.List").getInt(key));
			}
			worldBlackList.clear();
			List<String> worlds = config.getStringList("WorldBlackList");
			for (String worldName : worlds) {
				World world = Bukkit.getWorld(worldName);
				if (world != null) {
					worldBlackList.add(world);
				}
			}
			event_Sprint = config.getInt("Event.Sprint", 1);
			event_Attack = config.getInt("Event.Attack", 1);
			return;
		}

		config.set("MaxPower", maxPower);
		config.set("PowerRegain", powerRegain);
		config.set("Event.Break.Default", event_Break);

		config.set("Event.Break.List.COAL_ORE", 2);
		config.set("Event.Break.List.DIAMOND_ORE", 10);
		materials.clear();
		Set<String> keys = config.getConfigurationSection("Event.Break.List").getKeys(false);
		for (String key : keys) {
			Material material = Material.getMaterial(key);
			if (material == null || material.isBlock() == false) {
				continue;
			}
			materials.put(material, config.getConfigurationSection("Event.Break.List").getInt(key));
		}

		config.set("WorldBlackList", Arrays.asList("test", "world"));
		worldBlackList.clear();
		List<String> worlds = config.getStringList("WorldBlackList");
		for (String worldName : worlds) {
			World world = Bukkit.getWorld(worldName);
			if (world != null) {
				worldBlackList.add(world);
			}
		}

		config.set("Event.Sprint", event_Sprint);
		config.set("Event.Attack", event_Attack);

		config.save(configFile);
	}
}
