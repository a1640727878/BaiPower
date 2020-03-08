package sky_bai.bukkit.baipower;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class BaiPower extends JavaPlugin {
	private static BaiPower instance;
	private static ProtocolManager protocolManager;
	private static KeyedBossBar bossBar;

	private static Boolean debug = false;

	@Override
	public void onEnable() {
		instance = this;
		protocolManager = ProtocolLibrary.getProtocolManager();
		bossBar = Bukkit.createBossBar(new NamespacedKey(this, "BaiPower"), "$BaiPower$", BarColor.BLUE, BarStyle.SOLID);

		getCommand("BaiPower").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(new Event(), this);

		Config.reload();
		ConfigPlayerDate.reload();

		protocolManager.addPacketListener(new PacketListener(this, ListenerPriority.HIGH, PacketType.Play.Server.BOSS));
		refreshBossBer();
		regainPower();
		countdown();
		refreshPotion();
	}

	public static BaiPower getInstance() {
		return instance;
	}

	public static ProtocolManager getProtocolManager() {
		return protocolManager;
	}

	public static BossBar getBossBar() {
		return bossBar;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String str, String[] strs) {
		if (strs[0].equalsIgnoreCase("reload")) {
			Config.reload();
			ConfigPlayerDate.reload();
			return true;
		} else if (strs[0].equalsIgnoreCase("debug")) {
			debug = !debug;
			return true;
		} else if (strs[0].equalsIgnoreCase("setmaxpower")) {
			if (strs.length > 3) {
				Player player = Bukkit.getPlayer(strs[1]);
				Integer i = Integer.valueOf(strs[2]);
				PlayerDate.get(player).setMaxPower(i);
				return true;
			}
		} else if (strs[0].equalsIgnoreCase("setregainPower")) {
			if (strs.length > 3) {
				Player player = Bukkit.getPlayer(strs[1]);
				Integer i = Integer.valueOf(strs[2]);
				PlayerDate.get(player).setRegainPowerValue(i);
				return true;
			}
		}
		return false;
	}

	private void refreshBossBer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Float f = (float) (bossBar.getProgress() > 0 ? 0 : 1);
				bossBar.setProgress(f);
				bossBar.setTitle("$BaiPower$" + f);
			}
		}.runTaskTimerAsynchronously(this, 0, 5);
	}

	private void regainPower() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : bossBar.getPlayers()) {
					PlayerDate.Date date = PlayerDate.get(player);
					if (date.getRegainPower() && date.getPower() < date.getMaxPower() && player.getFoodLevel() > Config.Date.PowerRegain_FoodLevel) {
						date.addPower(date.getRegainPowerValue());
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 0, Config.Date.PowerRegain_Time);
	}

	private void countdown() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : bossBar.getPlayers()) {
					PlayerDate.Date date = PlayerDate.get(player);
					Boolean b1 = true;
					if (date.getPower() <= 0) {
						player.setSprinting(false);
						b1 = false;
					}
					if (b1 && player.isSprinting()) {
						date.subPower(Config.Date.Event_Sprint_Value);
						date.setRegainPower(false);
					} else {
						if (date.getTimeSprint() > 0) {
							date.subTimeSprint();
							date.setRegainPower(false);
						} else {
							date.setRegainPower(true);
						}
					}
					if (date.getTimeAtack() > 0) {
						date.subTimeAtack();
						date.setRegainPower(false);
					} else {
						date.setRegainPower(true);
					}
					if (date.getTimeBreak() > 0) {
						date.subTimeBreak();
						date.setRegainPower(false);
					} else {
						date.setRegainPower(true);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 20);
	}

	private void refreshPotion() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : bossBar.getPlayers()) {
					PlayerDate.Date date = PlayerDate.get(player);
					for (Integer integer : Config.Date.PotionList.keySet()) {
						if ((float) date.getPower() / (float) date.getMaxPower() * 100 < integer) {
							List<PotionEffect> potions = Config.Date.PotionList.get(integer);
							player.addPotionEffects(potions);
							break;
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 5);
	}

	public enum Action {
		ADD, REMOVE, UPDATE_PCT, UPDATE_NAME, UPDATE_STYLE, UPDATE_PROPERTIES
	}

	public static class Event implements Listener {
		@EventHandler
		public void onPlayerJoinServer(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			PlayerDate.add(player);
			bossBar.addPlayer(player);
		}

		@EventHandler
		public void onPlayerQuitServer(PlayerQuitEvent event) {
			Player player = event.getPlayer();
			PlayerDate.del(player);
			bossBar.removePlayer(player);
		}

		@EventHandler
		public void onPlyaerBreakBlock(BlockBreakEvent event) {
			Player player = event.getPlayer();
			PlayerDate.Date date = PlayerDate.get(player);
			if (Config.Date.WorldWhiteList.contains(player.getWorld()) == false) {
				return;
			}
			Block block = event.getBlock();
			if (debug && player.isOp()) {
				player.sendMessage(block.getType().name());
			}
			if (date.getPower() <= 0) {
				event.setCancelled(true);
				return;
			}
			if (Config.Date.Event_Break_ValueList.keySet().contains(block.getType())) {
				date.subPower(Config.Date.Event_Break_ValueList.get(block.getType()));
			} else {
				date.subPower(Config.Date.Event_Break_Value);
			}
			date.setTimeBreak(Config.Date.Event_Break_CoolDownTime);
		}

		@EventHandler
		public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
			Player player = event.getPlayer();
			PlayerDate.Date date = PlayerDate.get(player);
			if (Config.Date.WorldWhiteList.contains(player.getWorld()) == false) {
				return;
			}
			if (event.isSprinting() == true) {
				date.setTimeSprint(Config.Date.Event_Sprint_CoolDownTime);
			}
		}

		@EventHandler
		public void onPlayerAttack(EntityDamageByEntityEvent event) {
			if (event.getDamager() instanceof Player == false) {
				return;
			}
			Player player = (Player) event.getDamager();
			PlayerDate.Date date = PlayerDate.get(player);
			if (Config.Date.WorldWhiteList.contains(player.getWorld()) == false) {
				return;
			}
			if (date.getPower() <= 0) {
				event.setCancelled(true);
				return;
			}
			if (event.getEntity() instanceof Player) {
				date.subPower(Config.Date.Event_Attack_PVP_Value);
				date.setTimeAtack(Config.Date.Event_Attack_PVP_CoolDownTime);
			} else {
				date.subPower(Config.Date.Event_Attack_PVE_Value);
				date.setTimeAtack(Config.Date.Event_Attack_PVE_CoolDownTime);
			}
		}
	}

	public static class PacketListener extends PacketAdapter {

		public PacketListener(Plugin plugin, ListenerPriority listenerPriority, PacketType type) {
			super(plugin, listenerPriority, type);
		}

		@Override
		public void onPacketSending(PacketEvent event) {
			Player player = event.getPlayer();
			PlayerDate.Date date = PlayerDate.get(player);
			PacketContainer packet = event.getPacket();
			if (packet.getChatComponents().getValues().get(0).getJson().contains("$BaiPower$") == false) {
				return;
			}
			if (date.getPower() > date.getMaxPower() && date.getHideBossBar() == false) {
				date.setHideBossBar();
				packet.getEnumModifier(Action.class, 1).write(0, Action.REMOVE);
			}
			if (date.getPower() > date.getMaxPower() && date.getHideBossBar() == true) {
				date.setHideBossBar();
				packet.getEnumModifier(Action.class, 1).write(0, Action.ADD);
			}
			String str1 = date.getPower().toString();
			String str2 = date.getMaxPower().toString();
			packet.getChatComponents().write(0, WrappedChatComponent.fromText("体力值: " + str1 + "/" + str2));
			packet.getFloat().write(0, (float) date.getPower() / (float) date.getMaxPower());
		}
	}
}
