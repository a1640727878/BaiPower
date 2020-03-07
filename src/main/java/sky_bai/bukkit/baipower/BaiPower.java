package sky_bai.bukkit.baipower;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public final class BaiPower extends JavaPlugin implements Listener {

	private static BaiPower instance;
	private static ProtocolManager protocolManager;
	private static KeyedBossBar bossBar;

	private static Boolean debug = false;

	@Override
	public void onEnable() {
		instance = this;
		BPConfig.bpConfig = new BPConfig();
		protocolManager = ProtocolLibrary.getProtocolManager();
		Bukkit.getPluginManager().registerEvents(this, this);
		bossBar = Bukkit.createBossBar(new NamespacedKey(this, "BaiPower"), "%体力值%", BarColor.BLUE, BarStyle.SOLID);

		getCommand("BaiPower").setExecutor(this);

		protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGH, PacketType.Play.Server.BOSS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				Player player = event.getPlayer();
				PacketContainer packet = event.getPacket();
				if (packet.getChatComponents().getValues().get(0).getJson().contains("%体力值%") == false) {
					return;
				}
				if (PlayerDate.getPower(player) >= BPConfig.maxPower && PlayerDate.getHideBossBar(player) == false) {
					PlayerDate.setHideBossBar(player, true);
					packet.getEnumModifier(Action.class, 1).write(0, Action.REMOVE);
				}
				if (PlayerDate.getPower(player) < BPConfig.maxPower && PlayerDate.getHideBossBar(player) == true) {
					PlayerDate.setHideBossBar(player, false);
					packet.getEnumModifier(Action.class, 1).write(0, Action.ADD);
				}
				String str1 = PlayerDate.getPower(player).toString();
				String str2 = BPConfig.maxPower.toString();
				packet.getChatComponents().write(0, WrappedChatComponent.fromText("体力值: " + str1 + "/" + str2));
				packet.getFloat().write(0, (float) PlayerDate.getPower(player) / (float) BPConfig.maxPower);
			}
		});
		new BukkitRunnable() {
			@Override
			public void run() {
				Float f = (float) (bossBar.getProgress() > 0 ? 0 : 1);
				bossBar.setProgress(f);
				bossBar.setTitle("%体力值%-" + f);
			}
		}.runTaskTimerAsynchronously(this, 0, 5);
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : bossBar.getPlayers()) {
					Boolean b1 = true;
					if (PlayerDate.getPower(player) <= 0) {
						player.setSprinting(false);
						b1 = false;
					}
					Boolean b2 = true;
					if (b1 && player.isSprinting()) {
						PlayerDate.subPower(player, BPConfig.event_Sprint);
						b2 = false;
					} else {
						if (PlayerDate.playerSprint.get(player) > 0) {
							PlayerDate.playerSprint.put(player, PlayerDate.playerSprint.get(player) - 1);
							b2 = false;
						}
					}
					if (PlayerDate.playerAttack.get(player) > 0) {
						PlayerDate.playerAttack.put(player, PlayerDate.playerAttack.get(player) - 1);
						b2 = false;
					}
					if (PlayerDate.playerBreak.get(player) > 0) {
						PlayerDate.playerBreak.put(player, PlayerDate.playerBreak.get(player) - 1);
						b2 = false;
					}
					if (b2 && PlayerDate.getPower(player) < BPConfig.maxPower) {
						PlayerDate.addPower(player, BPConfig.powerRegain);
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 0, 20);
	}

	public static BaiPower getInstance() {
		return instance;
	}

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
		if (BPConfig.worldBlackList.contains(player.getWorld())) {
			return;
		}
		Block block = event.getBlock();
		if (debug && player.isOp()) {
			player.sendMessage(block.getType().name());
		}
		if (PlayerDate.getPower(player) <= 0) {
			event.setCancelled(true);
			return;
		}
		if (BPConfig.materials.keySet().contains(block.getType())) {
			PlayerDate.subPower(player, BPConfig.materials.get(block.getType()));
		} else {
			PlayerDate.subPower(player, BPConfig.event_Break);
		}
		PlayerDate.playerBreak.put(player, 5);
	}

	@EventHandler
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if (BPConfig.worldBlackList.contains(player.getWorld())) {
			return;
		}
		if (event.isSprinting() == true) {
			PlayerDate.playerSprint.put(player, 5);
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		Player damdger = event.getDamager() instanceof Player ? (Player) event.getDamager() : null;
		if (damdger == null) {
			return;
		}
		if (BPConfig.worldBlackList.contains(damdger.getWorld())) {
			return;
		}
		if (PlayerDate.getPower(damdger) <= 0) {
			event.setCancelled(true);
			return;
		}
		PlayerDate.subPower(damdger, BPConfig.event_Attack);
		PlayerDate.playerAttack.put(damdger, 5);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player && args[0].equalsIgnoreCase("debug")) {
			debug = !debug;
		} else if (args[0].equalsIgnoreCase("reload")) {
			BPConfig.reload();
		}
		return false;
	}

	public enum Action {
		ADD, REMOVE, UPDATE_PCT, UPDATE_NAME, UPDATE_STYLE, UPDATE_PROPERTIES
	}

}
