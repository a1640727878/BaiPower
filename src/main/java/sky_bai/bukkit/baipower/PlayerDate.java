package sky_bai.bukkit.baipower;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class PlayerDate {

	public static Map<Player, Date> playerDate = new HashMap<Player, PlayerDate.Date>();

	public static void add(Player player) {
		playerDate.put(player, new Date(player, ConfigPlayerDate.getPlayerQuitPower(player), ConfigPlayerDate.getMaxPower(player), ConfigPlayerDate.getRegainPowerValue(player)));
	}

	public static void del(Player player) {
		playerDate.remove(player);
	}

	public static Date get(Player player) {
		if (playerDate.keySet().contains(player)) {
			return playerDate.get(player);
		} else {
			return null;
		}
	}

	public static class Date {
		final Player player;

		Integer power;
		Integer maxPower;
		Integer regainPowerValue;
		Integer regainPower_CoolDownTime = 0;
		Integer timeAtack = 0;
		Integer timeBreak = 0;
		Integer timeSprint = 0;
		Boolean hideBossBar = false;
		Boolean regainPower = true;

		Date(Player player, Integer power, Integer maxPower, Integer regainPowerValue) {
			this.player = player;
			this.maxPower = maxPower;
			this.power = power;
			this.regainPowerValue = regainPowerValue;
		}

		public Player getPlayer() {
			return player;
		}

		public Integer getPower() {
			return power;
		}

		public void setPower(Integer power) {
			this.power = power;
		}

		public void addPower(Integer power) {
			this.power = this.power + power;
		}

		public void subPower(Integer power) {
			this.power = this.power - power;
		}

		public Integer getMaxPower() {
			return maxPower;
		}

		public void setMaxPower(Integer maxPower) {
			this.maxPower = maxPower;
			ConfigPlayerDate.setMaxPower(player, maxPower);
		}

		public Integer getRegainPowerValue() {
			return regainPowerValue;
		}

		public void setRegainPowerValue(Integer regainPowerValue) {
			this.regainPowerValue = regainPowerValue;
			ConfigPlayerDate.setRegainPowerValue(player, regainPowerValue);
		}

		public Integer getRegainPower_CoolDownTime() {
			return regainPower_CoolDownTime;
		}

		public void setRegainPower_CoolDownTime(Integer regainPower_CoolDownTime) {
			this.regainPower_CoolDownTime = regainPower_CoolDownTime;
		}

		public void subRegainPower_CoolDownTime() {
			regainPower_CoolDownTime = regainPower_CoolDownTime - (regainPower_CoolDownTime < 1 ? 0 : 1);
		}

		public Integer getTimeAtack() {
			return timeAtack;
		}

		public void setTimeAtack(Integer timeAtack) {
			this.timeAtack = timeAtack;
		}

		public void subTimeAtack() {
			timeAtack = timeAtack - (timeAtack < 1 ? 0 : 1);
		}

		public Integer getTimeBreak() {
			return timeBreak;
		}

		public void setTimeBreak(Integer timeBreak) {
			this.timeBreak = timeBreak;
		}

		public void subTimeBreak() {
			timeBreak = timeBreak - (timeBreak < 1 ? 0 : 1);
		}

		public Integer getTimeSprint() {
			return timeSprint;
		}

		public void setTimeSprint(Integer timeSprint) {
			this.timeSprint = timeSprint;
		}

		public void subTimeSprint() {
			timeSprint = timeSprint - (timeSprint < 1 ? 0 : 1);
		}

		public Boolean getHideBossBar() {
			return hideBossBar;
		}

		public void setHideBossBar() {
			hideBossBar = !hideBossBar;
		}

		public Boolean getRegainPower() {
			return regainPower;
		}

		public void setRegainPower(Boolean regainPower) {
			this.regainPower = regainPower;
		}
	}
}
