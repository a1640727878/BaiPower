package sky_bai.bukkit.baipower;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Potion {
	Collection<PotionEffect> Potions = new HashSet<PotionEffect>();

	public static Potion newInstance() {
		return new Potion();
	}

	public Potion add(PotionEffectType potionType, Integer level) {
		PotionEffect potionEffect = new PotionEffect(potionType, 40, level, false, false, false);
		Potions.add(potionEffect);
		return this;
	}

	public Collection<PotionEffect> getPotions() {
		return Potions;
	}
}
