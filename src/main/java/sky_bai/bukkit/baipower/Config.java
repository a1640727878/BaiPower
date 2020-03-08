package sky_bai.bukkit.baipower;

import java.io.File;
import java.util.ArrayList;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Config {
	private final static File configFile = new File(BaiPower.getInstance().getDataFolder(), "config.yml");
	private static FileConfiguration config = new YamlConfiguration();

	public static File getConfigFile() {
		return configFile;
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static void reload() {
		loadConfig();
		if (config.getKeys(false).isEmpty()) {
			reset();
			return;
		}

		Date.DefaultMaxPower = config.getInt("DefaultMaxPower", Date.DefaultMaxPower);

		Date.PowerRegain_Value = config.getInt("PowerRegain.Value", Date.PowerRegain_Value);
		Date.PowerRegain_Time = config.getInt("PowerRegain.Time", Date.PowerRegain_Time);
		Date.PowerRegain_FoodLevel = config.getInt("PowerRegain.FoodLevel", Date.PowerRegain_FoodLevel);

		Date.WorldWhiteList.clear();
		List<String> worldWhiteListsStrings = config.getStringList("WorldWhiteList");
		for (String worldName : worldWhiteListsStrings) {
			World world = Bukkit.getWorld(worldName);
			if (world != null) {
				Date.WorldWhiteList.add(world);
			}
		}

		Date.Event_Attack_PVE_Value = config.getInt("Event.Attack.PVE.Value", Date.Event_Attack_PVE_Value);
		Date.Event_Attack_PVE_CoolDownTime = config.getInt("Event.Attack.PVE.CoolDownTime", Date.Event_Attack_PVE_CoolDownTime);
		Date.Event_Attack_PVP_Value = config.getInt("Event.Attack.PVP.Value", Date.Event_Attack_PVP_Value);
		Date.Event_Attack_PVP_CoolDownTime = config.getInt("Event.Attack.PVP.CoolDownTime", Date.Event_Attack_PVP_CoolDownTime);

		Date.Event_Break_Value = config.getInt("Event.Break.Value", Date.Event_Break_Value);
		Date.Event_Break_CoolDownTime = config.getInt("Event.Break.CoolDownTime", Date.Event_Break_CoolDownTime);
		Date.Event_Break_ValueList.clear();
		Set<String> Event_Break_ValueListKeys = config.getConfigurationSection("Event.Break.ValueList").getKeys(false);
		for (String key : Event_Break_ValueListKeys) {
			Material material = Material.getMaterial(key);
			if (material == null || material.isBlock() == false) {
				continue;
			}
			Date.Event_Break_ValueList.put(material, config.getConfigurationSection("Event.Break.ValueList").getInt(key));
		}

		Date.Event_Sprint_Value = config.getInt("Event.Sprint.Value", Date.Event_Sprint_Value);
		Date.Event_Sprint_CoolDownTime = config.getInt("Event.Sprint.CoolDownTime", Date.Event_Sprint_CoolDownTime);
		
		Date.PotionList.clear();
		Set<String> PotionListKeys = config.getConfigurationSection("PotionList").getKeys(false);
		for (String key : PotionListKeys) {
			Integer i = Integer.valueOf(key);
			Set<String> PotionKeys = config.getConfigurationSection(key).getKeys(false);
			List<PotionEffect> potions = new ArrayList<PotionEffect>();
			for (String key2 : PotionKeys) {
				potions.add(new PotionEffect(PotionEffectType.getByName(key2), 2, i));
			}
			Date.PotionList.put(i, potions);
		}
	}

	private static void reset() {
		config.set("DefaultMaxPower", Date.DefaultMaxPower);

		config.set("PowerRegain.Value", Date.PowerRegain_Value);
		config.set("PowerRegain.Time", Date.PowerRegain_Time);
		config.set("PowerRegain.FoodLevel", Date.PowerRegain_FoodLevel);

		List<String> worldWhiteListsStrings = new ArrayList<String>();
		for (World world : Date.WorldWhiteList) {
			worldWhiteListsStrings.add(world.getName());
		}
		config.set("WorldWhiteList", worldWhiteListsStrings);

		config.set("Event.Attack.PVE.Value", Date.Event_Attack_PVE_Value);
		config.set("Event.Attack.PVE.CoolDownTime", Date.Event_Attack_PVE_CoolDownTime);
		config.set("Event.Attack.PVP.Value", Date.Event_Attack_PVP_Value);
		config.set("Event.Attack.PVP.CoolDownTime", Date.Event_Attack_PVP_CoolDownTime);

		config.set("Event.Break.Value", Date.Event_Break_Value);
		config.set("Event.Break.CoolDownTime", Date.Event_Break_CoolDownTime);
		for (Material material : Date.Event_Break_ValueList.keySet()) {
			config.set("Event.Break.ValueList." + material.name(), Date.Event_Break_ValueList.get(material));
		}

		config.set("Event.Sprint.Value", Date.Event_Sprint_Value);
		config.set("Event.Sprint.CoolDownTime", Date.Event_Sprint_CoolDownTime);

		for (Integer integer : Date.PotionList.keySet()) {
			for (PotionEffect potion : Date.PotionList.get(integer)) {
				config.set("PotionList."+integer+"."+potion.getType().getName(), potion.getAmplifier());
			}
		}
		
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

	public static class Date {
		public static Integer DefaultMaxPower = 100;

		public static Integer PowerRegain_Value = 1;
		public static Integer PowerRegain_Time = 20;
		public static Integer PowerRegain_FoodLevel = 6;

		public static Set<World> WorldWhiteList = new HashSet<World>();
		static {
			WorldWhiteList.addAll(Bukkit.getWorlds());
		}

		public static Integer Event_Attack_PVE_Value = 1;
		public static Integer Event_Attack_PVE_CoolDownTime = 5;
		public static Integer Event_Attack_PVP_Value = 1;
		public static Integer Event_Attack_PVP_CoolDownTime = 5;

		public static Integer Event_Break_Value = 1;
		public static Integer Event_Break_CoolDownTime = 5;
		public static Map<Material, Integer> Event_Break_ValueList = new HashMap<Material, Integer>();
		static {
			Event_Break_ValueList.put(Material.COAL_ORE, 2);
			Event_Break_ValueList.put(Material.DIAMOND_ORE, 10);
		}

		public static Integer Event_Sprint_Value = 1;
		public static Integer Event_Sprint_CoolDownTime = 5;

		public static Map<Integer, List<PotionEffect>> PotionList = new HashMap<Integer, List<PotionEffect>>();
		static {
			PotionEffect pe1 = new PotionEffect(PotionEffectType.SLOW_DIGGING, 2, 3);
			PotionEffect pe2 = new PotionEffect(PotionEffectType.SLOW, 2, 3);
			List<PotionEffect> pes1 = Arrays.asList(pe1,pe2);
			PotionList.put(10, pes1);
			PotionEffect pe3 = new PotionEffect(PotionEffectType.SLOW_DIGGING, 2, 2);
			PotionEffect pe4 = new PotionEffect(PotionEffectType.SLOW, 2, 2);
			List<PotionEffect> pes2 = Arrays.asList(pe3,pe4);
			PotionList.put(20, pes2);
			PotionEffect pe5 = new PotionEffect(PotionEffectType.SLOW_DIGGING, 2, 1);
			PotionEffect pe6 = new PotionEffect(PotionEffectType.SLOW, 2, 1);
			List<PotionEffect> pes3 = Arrays.asList(pe5,pe6);
			PotionList.put(30, pes3);
		}
	}
}
