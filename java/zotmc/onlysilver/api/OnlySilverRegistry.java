package zotmc.onlysilver.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class OnlySilverRegistry {
	
	private static final ListMultimap<String, Function<DamageSource, InUseWeapon>>
	weaponFunctions = ArrayListMultimap.create();
	
	private static final Map<Item, Predicate<ItemStack>>
	silverPredicates = Maps.newIdentityHashMap();
	
	private static final Map<Item, Function<ItemStack, Float>>
	werewolfDamages = Maps.newIdentityHashMap();
	
	
	
	/**
	 * Register your own weapon function to help OnlySilver determining the actual item in use from a DamageSource.
	 * The method is currently used for the implementation of the enchantments and WerewolfHandler in OnlySilver.
	 * The damageType act as a lookup key for the damage functions, and each weapon function associated
	 * with the same key will be applied until one of them give a non-null output.
	 * The output object is then used to determine the actual weapon item involved in this damage and
	 * as well as the user of the weapon.
	 * 
	 * Each damageType can be associated with multiple functions.
	 * A null MUST be returned by functions whenever an input DamageSource is not an interested type.
	 */
	public static void registerWeaponFunction(
			String damageType, Function<DamageSource, InUseWeapon> weaponFunction) {
		weaponFunctions.put(checkNotNull(damageType), checkNotNull(weaponFunction));
	}
	
	/**
	 * Register your own predicate to help OnlySilver determining whether or not an item is made from silver.
	 * The current purpose for this method is to determine which instrument should have silver enchantments.
	 * The first argument act as a lookup key for the predicate, and the predicate test whether or not
	 * the actual ItemStack is made from silver.
	 * 
	 * Each Item can only be mapped to a single predicate.
	 */
	public static void registerSilverPredicate(Item item, Predicate<ItemStack> silverPredicate) {
		silverPredicates.put(checkNotNull(item), checkNotNull(silverPredicate));
	}
	
	/**
	 * Register your own damage function to calculate how much damage should be inflicted on a werewolf.
	 * The first argument act as a lookup key for the function, and the damage function calculate damage
	 * from the actual ItemStack.
	 * 
	 * Each Item can only be mapped to a single function.
	 * A null output of a function can be used to indicate no changes to the damage.
	 */
	public static void registerWerewolfDamage(Item item, Function<ItemStack, Float> damageFunction) {
		werewolfDamages.put(checkNotNull(item), checkNotNull(damageFunction));
	}
	
	
	
	public static InUseWeapon getWeapon(DamageSource damage) {
		for (Function<DamageSource, InUseWeapon> func : weaponFunctions.get(damage.damageType)) {
			InUseWeapon ret = func.apply(damage);
			if (ret != null)
				return ret;
		}
		return AbsentWeapon.INSTANCE;
	}
	private enum AbsentWeapon implements InUseWeapon {
		INSTANCE;
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
	}
	
	public static boolean isSilverItem(ItemStack item) {
		if (item == null)
			return false;
		
		Predicate<ItemStack> p = silverPredicates.get(item.getItem());
		return p != null && p.apply(item);
	}
	
	public static Float getWerewolfDamage(ItemStack item) {
		if (item == null)
			return null;
		
		Function<ItemStack, Float> func = werewolfDamages.get(item.getItem());
		return func != null ? func.apply(item) : null;
	}

	
	
	public interface InUseWeapon {
		
		public Optional<EntityLivingBase> getUser();
		
		public Optional<ItemStack> getItem();
		
		public void update(ItemStack item);
		
	}
	
	
	
	static {
		Function<DamageSource, InUseWeapon> f = new Function<DamageSource, InUseWeapon>() {
			@Override public InUseWeapon apply(DamageSource input) {
				Entity sod = input.getSourceOfDamage();
				if (!(sod instanceof EntityLivingBase))
					return null;
				
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
		};
		
		registerWeaponFunction("player", f);
		registerWeaponFunction("mob", f);
	}
	
}
