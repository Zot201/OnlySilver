package zotmc.onlysilver;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.api.OnlySilverRegistry.InUseWeapon;
import ckathode.weaponmod.entity.projectile.EntityMaterialProjectile;

import com.google.common.base.Function;
import com.google.common.base.Optional;

class ContentsBalkon {
	/*
	private static WeaponModConfig modConfig = BalkonsWeaponMod.instance.modConfig;
	
	private enum Weapon {
		SPEAR		(silverSpear,		new MeleeCompSpear(toolSilver.get())),
		HALBERD		(silverHalberd,		new MeleeCompHalberd(toolSilver.get())),
		BATTLEAXE	(silverBattleaxe,	new MeleeCompBattleaxe(toolSilver.get())),
		KNIFE		(silverKnife,		new MeleeCompKnife(toolSilver.get())),
		WARHAMMER	(silverWarhammer,	new MeleeCompWarhammer(toolSilver.get())),
		KATANA		(silverKatana,		new MeleeComponent(MeleeSpecs.KATANA, toolSilver.get())),
		BOOMERANG	(silverBoomerang,	new MeleeCompBoomerang(toolSilver.get())),
		
		FLAIL {
			@Override public void init() {
				if (isEnabled());
					silverFlail.set(
							new ItemFlail(itemId(), toolSilver.get()) {
								{
									setCreativeTab(TAB_ONLY_SILVER);
									setTextureName(textureName());
								}
								@Override public boolean getIsRepairable(ItemStack a, ItemStack b) {
									return isSilverIngot(b) || super.getIsRepairable(a, b);
								}
							}
					);
			}
		},
		MUSKETBAYONET {
			@Override public void init() {
				if (modConfig.isEnabled("musket") && KNIFE.isEnabled())
					silverBayonetMusket.set(
							new ItemMusket(itemId(), new MeleeCompKnife(toolSilver.get()), silverKnife.get()) {
								{
									setCreativeTab(TAB_ONLY_SILVER);
									setTextureName(textureName());
								}
								@Override public boolean getIsRepairable(ItemStack a, ItemStack b) {
									return isSilverIngot(b) || super.getIsRepairable(a, b);
								}
							}
					);
			}
			@Override public String itemId() {
				return "silverBayonetMusket";
			}
		};
		
		

		private Content<Item> content;
		private MeleeComponent comp;
		
		private Weapon() { }
		private Weapon(Content<Item> content, MeleeComponent comp) {
			this.content = content;
			this.comp = comp;
		}
		
		public void init() {
			if (comp instanceof MeleeCompBattleaxe)
				((MeleeCompBattleaxe) comp).ignoreArmourAmount = 1;

			if (isEnabled());
				content.set(
						new ItemMelee(itemId(), comp) {
							{
								setCreativeTab(TAB_ONLY_SILVER);
								setTextureName(textureName());
							}
							@Override public boolean getIsRepairable(ItemStack a, ItemStack b) {
								return isSilverIngot(b) || super.getIsRepairable(a, b);
							}
						}
				);
			
			reset();
		}
		
		public void reset() {
			content = null;
			comp = null;
		}
		
		
		public static boolean isSilverIngot(ItemStack item) {
			for (ItemStack i : OreDictionary.getOres(Recipes.SILVER_INGOT))
				if (OreDictionary.itemMatches(i, item, false))
					return true;
			return false;
		}
		
		public boolean isEnabled() {
			return modConfig.isEnabled(toString());
		}
		
		public String itemId() {
			return "silver" + name().charAt(0) + toString().substring(1, name().length());
		}
		public String textureName() {
			return OnlySilver.MODID + ":" + itemId();
		}
		
		@Override public String toString() {
			return name().toLowerCase(Locale.ENGLISH);
		}
		
	}
	*/
	
	static void init() {
		/*
		for (Weapon weapon : Weapon.values())
			if (enableBalkon.get())
				weapon.init();
			else
				weapon.reset();
		*/
		
		OnlySilverRegistry.registerWeaponFunction("weapon", new Function<DamageSource, InUseWeapon>() {
			@Override public InUseWeapon apply(DamageSource input) {
				Entity sod = input.getSourceOfDamage();
				
				if (sod instanceof EntityMaterialProjectile) {
					final EntityMaterialProjectile emp = (EntityMaterialProjectile) sod;
					
					return new InUseWeapon() {
						@Override public Optional<EntityLivingBase> getUser() {
							Entity thrower = emp.getThrower();
							return Optional.fromNullable(
									thrower instanceof EntityLivingBase ?
											(EntityLivingBase) thrower : null);
						}
						@Override public Optional<ItemStack> getItem() {
							return Optional.fromNullable(emp.getPickupItem());
						}
						@Override public void update(ItemStack item) {
							if (item == null)
								emp.setDead();
						}
					};
					
				}
				
				return null;
			}
		});
		
	}
	
	
}
