package zotmc.onlysilver.data;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import zotmc.onlysilver.api.DamageSourceHandler;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Dynamic.Chain;
import zotmc.onlysilver.util.Dynamic.Invoke;
import zotmc.onlysilver.util.Utils;
import zotmc.onlysilver.util.Utils.Dependency;
import zotmc.onlysilver.util.Utils.Modid;
import zotmc.onlysilver.util.Utils.Requirements;

import com.google.common.base.Supplier;
import com.google.common.collect.Multiset;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

@MCVersion(Loader.MC_VERSION)
@Requirements("1.8 = 1.8")
public class ModData {
	
	public static class OnlySilvers {
		public static final String
		MODID = "onlysilver",
		DEPENDENCIES = "after:weaponmod",
		GUI_FACTORY = "zotmc.onlysilver.config.ConfigGui";
	}
	
	
	@Dependency
	@Requirements("1.8 = 11.14.1.1336")
	public static class Forge {
		@Modid public static final String MODID = "Forge";
	}
	
	public static class MoCreatures {
		@Modid public static final String MODID = "mocreatures";
		
		public static final String
		MOC_ENTITY_WEREWOLF = "drzhark.mocreatures.entity.monster.MoCEntityWerewolf",
		GET_IS_HUMAN_FORM = "getIsHumanForm";
	}
	
	public static class Thaumcraft {
		@Modid public static final String MODID = "Thaumcraft";
		
		private static final String
		THAUMCRAFT_API = "thaumcraft.api.ThaumcraftApi",
		ASPECT_LIST = "thaumcraft.api.aspects.AspectList",
		ASPECT = "thaumcraft.api.aspects.Aspect";
		
		public enum Aspect {
			METAL, GREED, EARTH;
			
			private static Supplier<?> adapt(Multiset<Aspect> aspects) {
				Chain<?> ret = Dynamic.construct(ASPECT_LIST);
				for (Multiset.Entry<Aspect> entry : aspects.entrySet())
					ret = ret.invoke("add")
						.via(ASPECT, Dynamic.refer(ASPECT, entry.getElement().name()))
						.viaInt(entry.getCount());
				return ret;
			}
		}
		
		public static void registerEntityTag(Class<? extends Entity> entity, Multiset<Aspect> aspects) {
			Dynamic.<Void>invoke(THAUMCRAFT_API, "registerEntityTag")
				.via(Utils.getEntityString(entity))
				.via(ASPECT_LIST, Aspect.adapt(aspects))
				.via(Utils.newArray(THAUMCRAFT_API + "$EntityTagsNBT", 0))
				.get();
		}
		
		public static void registerObjectTag(ItemStack item, Multiset<Aspect> aspects) {
			Dynamic.<Void>invoke(THAUMCRAFT_API, "registerObjectTag")
				.via(ItemStack.class, item)
				.via(ASPECT_LIST, Aspect.adapt(aspects))
				.get();
		}
	}
	
	public static class WeaponMod {
		@Modid public static final String MODID = "weaponmod";
		public static final String MELEE_COMPONENT = "ckathode.weaponmod.item.MeleeComponent";
		
		public static final Invoke<Boolean> isEnabled = Dynamic
				.refer("ckathode.weaponmod.BalkonsWeaponMod", "instance")
				.refer("modConfig")
				.invoke("isEnabled");
		
		public static class ItemMeleeSupplier implements Supplier<Item> {
			private final String itemId;
			private final Supplier<?> meleeComp;
			
			public ItemMeleeSupplier(String itemId, Supplier<?> meleeComp) {
				this.itemId = itemId;
				this.meleeComp = meleeComp;
			}
			
			@Override public Item get() {
				return Dynamic.<Item>construct("ckathode.weaponmod.item.ItemMelee")
						.via(itemId)
						.via(MELEE_COMPONENT, meleeComp)
						.assemble(Instrumenti.GET_IS_REPAIRABLE_SILVER)
						.get();
			}
		}
		
		public static class ProjectileHandler<EntityMaterialProjectile> implements DamageSourceHandler {
			private final Class<EntityMaterialProjectile> empType;
			private final Invokable<EntityMaterialProjectile, ItemStack> getPickupItem;
			private final Invokable<EntityMaterialProjectile, Void> setThrownItemStack;
			
			@SuppressWarnings("unchecked")
			public ProjectileHandler() throws Throwable {
				empType = (Class<EntityMaterialProjectile>) Class.forName(
						"ckathode.weaponmod.entity.projectile.EntityMaterialProjectile");
				
				getPickupItem = TypeToken.of(empType)
						.method(empType.getMethod("getPickupItem"))
						.returning(ItemStack.class);
				
				setThrownItemStack = TypeToken.of(empType)
						.method(empType.getMethod("setThrownItemStack", ItemStack.class))
						.returning(void.class);
			}
			
			@Override public String[] getTargetDamageTypes() {
				return new String[] {"weapon"};
			}
			
			@Override public ItemStack getItem(DamageSource damage) {
				Entity sourceOfDamage = damage.getSourceOfDamage();
				
				if (empType.isInstance(sourceOfDamage))
					try {
						return getPickupItem.invoke(empType.cast(sourceOfDamage));
						
					} catch (Throwable t) {
						Utils.propagate(t);
					}
				
				return null;
			}
			
			@Override public void updateItem(DamageSource damage, ItemStack item) {
				try {
					setThrownItemStack.invoke(empType.cast(damage.getSourceOfDamage()), item);
					
				} catch (Throwable t) {
					Utils.propagate(t);
				}
			}
		}
	}
	
}
