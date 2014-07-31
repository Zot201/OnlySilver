package zotmc.onlysilver.ench;

import net.minecraft.enchantment.Enchantment;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Reserve;

public final class EnchData {
	
	public final boolean isEnabled;
	public final int enchId;
	
	public EnchData() {
		this(false, -1);
	}
	public EnchData(boolean isEnabled, int enchantmentId) {
		this.isEnabled = isEnabled;
		this.enchId = enchantmentId;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enchantment> void addEnchantment(Reserve<? super T> ench, Class<T> clz, String name) {
		if (isEnabled) {
			if (Enchantment.enchantmentsList[enchId] != null)
				throw new IllegalArgumentException(String.format(
						"Duplicated id (%d) between %s and enchantment.%s",
						enchId, Enchantment.enchantmentsList[enchId].getName(), name
				));
			
			ench.set((T) Dynamic.construct(clz).viaInt(enchId).get().setName(name));
		}
	}
	
}
