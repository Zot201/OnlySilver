package zotmc.onlysilver.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public interface DamageSourceHandler {

  public String[] getTargetDamageTypes();

  public ItemStack getItem(DamageSource damage);

  public void updateItem(DamageSource damage, ItemStack item);

}
