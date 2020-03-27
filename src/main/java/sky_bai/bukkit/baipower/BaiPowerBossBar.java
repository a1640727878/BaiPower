package sky_bai.bukkit.baipower;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class BaiPowerBossBar {

	public static List<Player> players = new ArrayList<Player>();

	public static void add(Player player) {
		players.add(player);
		new BossBar(player, Action.ADD).sendServerPacket();
	}
	
	public static void del(Player player) {
		players.remove(player);
		// new BossBar(player, Action.REMOVE).sendServerPacket();
	}

	public static void hide(Player player) {
		PlayerDate.Date date = PlayerDate.get(player);
		if (date.getPower() == date.getMaxPower() && date.getHideBossBar() == false) {
			date.setHideBossBar();
			new BossBar(player, Action.REMOVE).sendServerPacket();
		}
		if (date.getPower() != date.getMaxPower() && date.getHideBossBar() == true) {
			date.setHideBossBar();
			new BossBar(player, Action.ADD).sendServerPacket();
		}
	}
	
	public static void refresh(Player player) {
		new BossBar(player, Action.UPDATE_NAME).sendServerPacket();
		new BossBar(player, Action.UPDATE_PCT).sendServerPacket();
		new BossBar(player, Action.UPDATE_PROPERTIES).sendServerPacket();
	}
	
	private static class BossBar {
		Player player;

		UUID uuid;
		Action action;
		WrappedChatComponent chatComponent;
		Float f;
		BarColor bColor = BarColor.BLUE;
		BarStyle bStyle = BarStyle.PROGRESS;

		private BossBar(Player player, Action action) {
			this.player = player;
			PlayerDate.Date date = PlayerDate.get(player);
			uuid = player.getUniqueId();
			this.action = action;
			chatComponent = WrappedChatComponent.fromText("体力值: " + date.getPower() + "/" + date.getMaxPower());
			f = (float) date.getPower() / (float) date.getMaxPower();
		}

		public void sendServerPacket() {
			PacketContainer p = new PacketContainer(PacketType.Play.Server.BOSS);
			p.getUUIDs().write(0, uuid);
			p.getEnumModifier(Action.class, 1).write(0, action);
			p.getChatComponents().write(0, chatComponent);
			p.getFloat().write(0, f);
			p.getEnumModifier(BarColor.class, 4).write(0, bColor);
			p.getEnumModifier(BarStyle.class, 5).write(0, bStyle);
			p.getBooleans().write(0, false).write(1, false).write(2, false);
			try {
				BaiPower.getProtocolManager().sendServerPacket(player, p);
			} catch (InvocationTargetException e) {
			}
		}
	}

	public enum Action {
		ADD, REMOVE, UPDATE_PCT, UPDATE_NAME, UPDATE_STYLE, UPDATE_PROPERTIES
	}
	
	public enum BarStyle{
		PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20;
	}
}
