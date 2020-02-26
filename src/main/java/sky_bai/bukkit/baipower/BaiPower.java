package sky_bai.bukkit.baipower;

import org.bukkit.plugin.java.JavaPlugin;

public final class BaiPower extends JavaPlugin {

	private static BaiPower instance;

	@Override
	public void onEnable() {}

	public static BaiPower getInstance() {
		return instance;
	}
}
