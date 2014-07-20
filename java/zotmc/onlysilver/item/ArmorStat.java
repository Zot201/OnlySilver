package zotmc.onlysilver.item;

import static com.google.common.base.Preconditions.checkArgument;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

public final class ArmorStat {
	
	public final int durability, enchantability;
	public ImmutableList<Integer> reductionAmounts;

	private ArmorStat(int durability, ImmutableList<Integer> reductionAmounts, int enchantability) {
		this.durability = durability;
		checkArgument(reductionAmounts.size() == 4);
		this.reductionAmounts = reductionAmounts;
		this.enchantability = enchantability;
	}
	
	public ArmorStat() {
		this(0, 0, 0, 0, 0, 0);
	}
	public ArmorStat(int durability, int r0, int r1, int r2, int r3, int enchantability) {
		this(durability, ImmutableList.of(r0, r1, r2, r3), enchantability);
	}
	public ArmorStat(int durability, String reductionAmounts, int enchantability) {
		this(durability,
				FluentIterable.from(Splitter.on(',').trimResults().split(reductionAmounts))
					.transform(Utils.parseInt())
					.toList(),
				enchantability
		);
	}
	
	public String reductionAmountString() {
		return Joiner.on(", ").join(reductionAmounts);
	}
	
	public ArmorMaterial addArmorMaterial(String name) {
		return EnumHelper.addArmorMaterial(name, durability, Ints.toArray(reductionAmounts), enchantability);
	}
	
}
