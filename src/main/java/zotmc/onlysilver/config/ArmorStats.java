package zotmc.onlysilver.config;

import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import zotmc.onlysilver.util.RawTypeAdapterFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

public final class ArmorStats {

  public final int durability;
  public final List<Integer> reductionAmounts;
  public final int  enchantability;

  public ArmorStats(int durability, int r0, int r1, int r2, int r3, int enchantability) {
    this(durability, ImmutableList.of(r0, r1, r2, r3), enchantability);
  }
  private ArmorStats(int durability, List<Integer> reductionAmounts, int enchantability) {
    this.durability = durability;
    this.reductionAmounts = reductionAmounts;
    this.enchantability = enchantability;
  }

  public ArmorMaterial addArmorMaterial(String name, String rendererPrefix) {
    return EnumHelper.addArmorMaterial(name, rendererPrefix, durability, Ints.toArray(reductionAmounts), enchantability);
  }

  @Override public int hashCode() {
    return Objects.hash(durability, reductionAmounts, enchantability);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof ArmorStats) {
      ArmorStats o = (ArmorStats) obj;
      return durability == o.durability && enchantability == o.enchantability && reductionAmounts.equals(o.reductionAmounts);
    }
    return false;
  }


  public static class AdapterFactory extends RawTypeAdapterFactory<ArmorStats> {
    @Override protected Class<? super ArmorStats> targetType() {
      return ArmorStats.class;
    }
    @Override protected ArmorStats postProcessing(ArmorStats in) {
      if (in != null) {
        List<?> list = in.reductionAmounts;
        if (list == null || list.size() != 4 || Iterables.any(list, Predicates.isNull())) return null;
        return new ArmorStats(in.durability, ImmutableList.copyOf(in.reductionAmounts), in.enchantability);
      }
      return in;
    }
  }

}
