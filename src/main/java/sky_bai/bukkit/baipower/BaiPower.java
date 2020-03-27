package sky_bai.bukkit.baipower;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class BaiPower extends JavaPlugin {
	private static BaiPower instance;
	private static ProtocolManager protocolManager;

	@Override
	public void onEnable() {
		instance = this;
		protocolManager = ProtocolLibrary.getProtocolManager();

		getCommand("BaiPower").setExecutor(new BaiPowerCommand());
		Bukkit.getPluginManager().registerEvents(new Event(), this);

		Config.reload();
		ConfigPlayerDate.loadConfig();

		new BaiPowerPlaceholderAPI().register();

		onPlayerSprint();
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

	private void refreshBossBer() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : BaiPowerBossBar.players) {
					if (player.isOnline() == false) {
						continue;
					}
					BaiPowerBossBar.hide(player);
					BaiPowerBossBar.refresh(player);
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 5);
	}

	private void regainPower() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : BaiPowerBossBar.players) {
					if (player.isOnline() == false) {
						continue;
					}
					PlayerDate.Date date = PlayerDate.get(player);
					if (date.getPower() > date.getMaxPower()) {
						date.setPower(date.getMaxPower());
					}
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
				for (Player player : BaiPowerBossBar.players) {
					if (player.isOnline() == false) {
						continue;
					}
					PlayerDate.Date date = PlayerDate.get(player);
					if (date.getPower() <= 0 && date.getRegainPower_CoolDownTime() == 0) {
						date.setRegainPower_CoolDownTime(Config.Date.PowerRegain_CoolDownTime);
					}
					date.setRegainPower(true);
					if (date.getTimeSprint() > 0) {
						date.subTimeSprint();
						date.setRegainPower(false);
					}
					if (date.getTimeAtack() > 0) {
						date.subTimeAtack();
						date.setRegainPower(false);
					}
					if (date.getTimeBreak() > 0) {
						date.subTimeBreak();
						date.setRegainPower(false);
					}
					if (date.getRegainPower_CoolDownTime() > 0) {
						date.subRegainPower_CoolDownTime();
						date.setRegainPower(false);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 20);
	}

	private void refreshPotion() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : BaiPowerBossBar.players) {
					if (player.isOnline() == false) {
						continue;
					}
					PlayerDate.Date date = PlayerDate.get(player);
					for (Integer integer : Config.Date.PotionList.keySet()) {
						if ((float) date.getPower() / (float) date.getMaxPower() * (float) 100 < (float) integer) {
							Collection<PotionEffect> potions = Config.Date.PotionList.get(integer);
							player.addPotionEffects(potions);
							break;
						}
					}
				}
			}
		}.runTaskTimer(this, 0, 5);
	}

	private void onPlayerSprint() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : BaiPowerBossBar.players) {
					if (player.isOnline() == false) {
						continue;
					}
					if (Config.Date.WorldWhiteList.contains(player.getWorld()) == false) {
						continue;
					}
					if (player.isSprinting() == false) {
						continue;
					}
					PlayerDate.Date date = PlayerDate.get(player);
					if (date.getPower() < Config.Date.Event_Sprint_Power) {
						player.setSprinting(false);
						continue;
					}
					date.subPower(Config.Date.Event_Sprint_Value);
					date.setTimeSprint(Config.Date.Event_Sprint_CoolDownTime);
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 20);
	}

	public static class Event implements Listener {
		@EventHandler
		public void onPlayerJoinServer(PlayerJoinEvent event) {
			Player player = event.getPlayer();
			PlayerDate.add(player);
			ConfigPlayerDate.setPlayerQuitPower(player, -1);
			BaiPowerBossBar.add(player);
		}

		@EventHandler
		public void onPlayerQuitServer(PlayerQuitEvent event) {
			Player player = event.getPlayer();
			ConfigPlayerDate.setPlayerQuitPower(player, PlayerDate.get(player).getPower());
			// PlayerDate.del(player);
			// BaiPowerBossBar.del(player);
		}

		@EventHandler
		public void onPlyaerBreakBlock(BlockBreakEvent event) {
			Player player = event.getPlayer();
			PlayerDate.Date date = PlayerDate.get(player);
			if (Config.Date.WorldWhiteList.contains(player.getWorld()) == false) {
				return;
			}
			Block block = event.getBlock();
			if (BaiPowerCommand.debug && player.isOp()) {
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
}
