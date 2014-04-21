package zotmc.onlysilver.api;

import static zotmc.onlysilver.api.OnlySilverAPI.silverIngot;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class OnlySilverRegistry {
	
	public interface InUseWeapon {
		
		public EntityLivingBase getUser();
		
		public ItemStack get();
		
		public void update(ItemStack item);
		
	}
	
	private static final ListMultimap<String, Function<DamageSource, InUseWeapon>>
	weaponFunctions = ArrayListMultimap.create();
	
	public static InUseWeapon getWeapon(DamageSource damage) {
		InUseWeapon ret;
		for (Function<DamageSource, InUseWeapon> f : weaponFunctions.get(damage.damageType))
			if ((ret = f.apply(damage)) != null)
				return ret;
		return null;
	}
	
	public static void registerWeaponFunction(String damageType, Function<DamageSource, InUseWeapon> function) {
		weaponFunctions.put(damageType, function);
	}
	
	static {
		Function<DamageSource, InUseWeapon> f = new Function<DamageSource, InUseWeapon>() {
			@Override public InUseWeapon apply(DamageSource input) {
				Entity sod = input.getSourceOfDamage();
				
				if (sod instanceof EntityLivingBase) {
					final EntityLivingBase elb = (EntityLivingBase) sod;
					
					return new InUseWeapon() {
						@Override public EntityLivingBase getUser() {
							return elb;
						}
						@Override public ItemStack get() {
							return elb.getHeldItem();
						}
						@Override public void update(ItemStack item) {
							// TODO: find a way to disarm a mob without causing a ConcurrentModificationException.
							/*
							if (item.stackSize <= 0 && !(elb instanceof EntityPlayer))
								elb.setCurrentItemOrArmor(0, null);
								*/
						}
					};
				}
				
				return null;
			}
		};
		
		registerWeaponFunction("player", f);
		registerWeaponFunction("mob", f);
		
	}
	
	
	
	
	private static final List<Predicate<ItemStack>>
	silverItemPredicates = Lists.newArrayList();
	
	public static boolean isSilverItem(ItemStack item) {
		for (Predicate<ItemStack> p : silverItemPredicates)
			if (p.apply(item))
				return true;
		return false;
	}
	
	public static void registerSilverItemPredicate(Predicate<ItemStack> predicate) {
		silverItemPredicates.add(predicate);
	}
	
	static {
		registerSilverItemPredicate(new Predicate<ItemStack>() {
			@Override public boolean apply(ItemStack input) {
				return input.getItem().getIsRepairable(input, new ItemStack(silverIngot.get()));
			}
		});
		
	}
	

}
