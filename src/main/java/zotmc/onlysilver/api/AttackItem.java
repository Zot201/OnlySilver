package zotmc.onlysilver.api;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class AttackItem {
	
	private final DamageSourceHandler handler;
	private final DamageSource damage;
	private ItemStack cachedItem;
	
	AttackItem(DamageSourceHandler handler, DamageSource damage, ItemStack cachedItem) {
		this.handler = handler;
		this.damage = damage;
		this.cachedItem = cachedItem;
	}
	
	public ItemStack getItem() {
		return cachedItem != null ? cachedItem : (cachedItem = handler.getItem(damage));
	}
	
	public void updateItem(ItemStack item) {
		handler.updateItem(damage, item);
		cachedItem = null;
	}
	
	public EntityLivingBase getAttacker() {
		Entity ret = damage.getEntity();
		return ret instanceof EntityLivingBase ? (EntityLivingBase) ret : null;
	}
	
	public int getEnchantmentLevel(Enchantment ench) {
		ItemStack item = getItem();
		return item == null ? 0 : EnchantmentHelper.getEnchantmentLevel(ench.effectId, item);
	}
	
	public void damageItem(int amount, Random rand) {
		EntityLivingBase attacker = getAttacker();
		if (attacker != null) getItem().damageItem(amount, attacker);
		else getItem().attemptDamageItem(amount, rand);
	}
	
	public boolean isItemRunOut() {
		ItemStack item = getItem();
		return item == null || item.stackSize <= 0;
	}
	
	public void updateItemUponConsumption() {
		ItemStack item = getItem();
		if (item != null && item.stackSize <= 0) item = null;
		updateItem(item);
	}
	
}
