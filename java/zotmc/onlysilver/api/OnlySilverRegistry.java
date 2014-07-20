package zotmc.onlysilver.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class OnlySilverRegistry {
	
	public interface InUseWeapon {
		
		public Optional<EntityLivingBase> getUser();
		
		public Optional<ItemStack> getItem();
		
		public void update(ItemStack item);
		
	}
	
	private static final ListMultimap<String, Function<DamageSource, InUseWeapon>>
	weaponFunctions = ArrayListMultimap.create();
	
	private static final List<Predicate<ItemStack>>
	silverItemPredicates = Lists.newArrayList();
	
	
	
	public static void registerWeaponFunction(
			String damageType, Function<DamageSource, InUseWeapon> function) {
		weaponFunctions.put(damageType, function);
	}
	
	@Deprecated public static void registerSilverItemPredicate(Predicate<ItemStack> isSilverItem) {
		registerSilverItems(isSilverItem);
	}
	public static void registerSilverItems(Predicate<ItemStack> isSilverItem) {
		silverItemPredicates.add(checkNotNull(isSilverItem));
	}
	
	
	
	public static InUseWeapon getWeapon(DamageSource damage) {
		InUseWeapon ret;
		for (Function<DamageSource, InUseWeapon> f : weaponFunctions.get(damage.damageType))
			if ((ret = f.apply(damage)) != null)
				return ret;
		
		return ABSENT_WEAPON;
	}
	
	public static boolean isSilverItem(ItemStack item) {
		if (item == null)
			return false;
		
		for (Predicate<ItemStack> p : silverItemPredicates)
			if (p.apply(item))
				return true;
		return false;
	}
	
	
	
	
	
	private static final InUseWeapon ABSENT_WEAPON = new InUseWeapon() {
		@Override public Optional<EntityLivingBase> getUser() {
			return Optional.absent();
		}
		@Override public Optional<ItemStack> getItem() {
			return Optional.absent();
		}
		@Override public void update(ItemStack item) { }
		
		@Override public String toString() {
			return "[No Weapons Defined]";
		}
	};
	
	static {
		Function<DamageSource, InUseWeapon> f = new Function<DamageSource, InUseWeapon>() {
			@Override public InUseWeapon apply(DamageSource input) {
				Entity sod = input.getSourceOfDamage();
				
				if (sod instanceof EntityLivingBase) {
					final EntityLivingBase elb = (EntityLivingBase) sod;
					
					return new InUseWeapon() {
						@Override public Optional<EntityLivingBase> getUser() {
							return Optional.of(elb);
						}
						@Override public Optional<ItemStack> getItem() {
							return Optional.fromNullable(elb.getHeldItem());
						}
						@Override public void update(ItemStack item) {
							// TODO: find a way to disarm a mob without causing a ConcurrentModificationException.
							if (elb instanceof EntityPlayer)
								elb.setCurrentItemOrArmor(0, item);
							else if (item == null)
								elb.getHeldItem().stackSize = 0;
							else
								elb.setCurrentItemOrArmor(0, item);
						}
						@Override public String toString() {
							return String.format("[%s held by %s]",
									getItem().orNull(), getUser().orNull());
						}
					};
				}
				
				return null;
			}
		};
		
		registerWeaponFunction("player", f);
		registerWeaponFunction("mob", f);
		
	}
	
	
}
