package sky_bai.bukkit.baipower;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class BaiPowerPlaceholderAPI extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "sky_bai";
	}

	@Override
	public String getIdentifier() {
		return "baipower";
	}

	@Override
	public String getVersion() {
		return "0.0.1";
	}

	@Override
	public String onPlaceholderRequest(Player player, String params) {
		PlayerDate.Date date = PlayerDate.get(player);
		if (params.startsWith("date")) {
			setPlayerDate(date, "date", params);
			return "体力: " + date.getPower() + "/" + date.getMaxPower() + ",战斗冷却: " + date.timeAtack + "/" + Config.Date.Event_Attack_PVE_Value + "(PVE)/" + Config.Date.Event_Attack_PVP_CoolDownTime + "(PVP),挖掘冷却: " + date.getTimeBreak() + "/" + Config.Date.Event_Break_CoolDownTime + ",跑步冷却: " + date.getTimeSprint() + "/" + Config.Date.Event_Sprint_CoolDownTime;
		} else if (params.startsWith("powe")) {
			setPlayerDate(date, "powe", params);
			return date.getPower().toString();
		} else if (params.startsWith("time_")) {
			String str1 = params.replace("time_", "");
			if (str1.isEmpty() == false) {
				if (str1.startsWith("atack")) {
					setPlayerDate(date, "atack", str1);
					return date.getTimeAtack().toString();
				} else if (str1.startsWith("break")) {
					setPlayerDate(date, "break", str1);
					return date.getTimeBreak().toString();
				} else if (str1.startsWith("sprint")) {
					setPlayerDate(date, "sprint", str1);
					return date.getTimeSprint().toString();
				}
			}
		}
		return super.onPlaceholderRequest(player, params);
	}

	private void setPlayerDate(PlayerDate.Date date, String str, String params) {
		String str1 = params.replace(str, "");
		if (str1.isEmpty() == false) {
			Player player2 = Bukkit.getPlayer(str.substring(1));
			date = PlayerDate.get(player2);
		}
	}

}
