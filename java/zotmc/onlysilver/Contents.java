package zotmc.onlysilver;

import static cpw.mods.fml.common.Loader.isModLoaded;
import static zotmc.onlysilver.item.Instrumentum.silverBow;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.api.OnlySilverRegistry.InUseWeapon;
import zotmc.onlysilver.block.BlockOnlyStorage;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.data.ModData.Thaumcraft;
import zotmc.onlysilver.data.ModData.WeaponMod;
import zotmc.onlysilver.ench.EnchEverlasting;
import zotmc.onlysilver.ench.EnchIncantation;
import zotmc.onlysilver.entity.EntitySilverGolem;
import zotmc.onlysilver.item.Instrumentum;
import zotmc.onlysilver.item.ItemOnlyIngot;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Dynamic.Refer;
import zotmc.onlysilver.util.Reserve;
import zotmc.onlysilver.util.Utils;
import zotmc.onlysilver.util.Utils.Uncheck;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class Contents {
	
	public static final Reserve<Block>
	silverOre = Reserve.absent(),
	silverBlock = Reserve.absent();
	
	public static final Reserve<Item>
	silverIngot = Reserve.absent(),
	silverRod = Reserve.absent();
	
	public static final Reserve<ToolMaterial>
	toolSilver = Reserve.absent();
	public static final Reserve<ArmorMaterial>
	armorSilver = Reserve.absent();
	public static final Reserve<Integer>
	rendererPrefix = Reserve.absent();
	
	public static final Reserve<EnchEverlasting>
	everlasting = Reserve.absent();
	public static final Reserve<Enchantment>
	incantation = Reserve.absent();
	
	public static final Reserve<Achievement>
	silverBowAchievement = Reserve.absent();
	
	
	static void init() {
		silverOre.set(register(
				new Block(Material.rock) { }
					.setBlockName("silverOre")
					.setBlockTextureName(OnlySilvers.MODID + ":silverOre")
					.setCreativeTab(OnlySilver.instance.tabOnlySilver)
		));
		Config.current().silverOre.get().setStatTo(silverOre, "pickaxe");
		
		silverBlock.set(register(
				new BlockOnlyStorage(Material.iron)
					.setBlockName("silverBlock")
					.setBlockTextureName(OnlySilvers.MODID + ":silverBlock")
					.setCreativeTab(OnlySilver.instance.tabOnlySilver)
		));
		Config.current().silverBlock.get().setStatTo(silverBlock, null);
		
		silverIngot.set(register(
				new ItemOnlyIngot("silverIngot")
		));
		silverRod.set(register(
				new ItemOnlyIngot("silverRod")
		));
		
		
		toolSilver.set(Config.current().toolSilver.get().addToolMaterial("SILVER"));
		armorSilver.set(Config.current().armorSilver.get().addArmorMaterial("SILVER"));
		rendererPrefix.set(OnlySilver.proxy.addArmor("silver"));
		
		armorSilver.get().customCraftingMaterial = silverIngot.get();
        toolSilver.get().customCraftingMaterial = silverIngot.get();
        
        
        init(Instrumentum.class);
        
        
		
        Config.current().everlasting.get()
        	.addEnchantment(everlasting, EnchEverlasting.class, OnlySilvers.MODID + ".everlasting");
        Config.current().incantation.get()
    		.addEnchantment(incantation, EnchIncantation.class, OnlySilvers.MODID + ".incantation");
		

		EntityRegistry.registerModEntity(EntitySilverGolem.class,
				"silverGolem", 0, OnlySilver.instance, 64, 1, true);
		Utils.EntityLists.stringToClassMapping()
			.put("onlysilver.onlysilver.silverGolem", EntitySilverGolem.class); // re-map a mistaken previous name
		
		
		silverBowAchievement.set(
				new Achievement("silverBowAch", "silverBowAch", 6, 5,
						silverBow.get(), AchievementList.acquireIron
				).registerStat()
		);
        
        
		if (isModLoaded(WeaponMod.MODID))
			initBalkon();
		
		if (isModLoaded(Thaumcraft.MODID))
			initThaum();
		
		
		
		init(InitApi.class);
		
	}
	
	private static Block register(Block block) {
		return GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5)); // without "tile."
	}
	private static Item register(Item item) {
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5)); // without "item."
		return item;
	}
	private static void init(Class<?> clz) {
		Dynamic.<Void>invoke(clz, "init").get();
	}
	
	
	
	
	private static final String ERR_MSG = "An error occurred while trying to access the %s contents!";
	
	private static void initBalkon() {
		try {
			final Class<? extends Entity>
			emp = Utils.getClassChecked("ckathode.weaponmod.entity.projectile.EntityMaterialProjectile");

			final Uncheck<Entity, Entity> getThrower = Utils.uncheck(
					Utils.upcast(emp)
						.method(emp.getMethod("getThrower"))
						.returning(Entity.class)
			);
			
			final Uncheck<Entity, ItemStack> getPickupItem = Utils.uncheck(
					Utils.upcast(emp)
						.method(emp.getMethod("getPickupItem"))
						.returning(ItemStack.class)
			);
			
			final Uncheck<Entity, Void> setThrownItemStack = Utils.uncheck(
					Utils.upcast(emp)
						.method(emp.getMethod("setThrownItemStack", ItemStack.class))
						.returning(void.class)
			);
			
			
			OnlySilverRegistry.registerWeaponFunction("weapon",
					new Function<DamageSource, InUseWeapon>() {
				
				@Override public InUseWeapon apply(DamageSource input) {
					final Entity projectile = input.getSourceOfDamage();
					if (!emp.isInstance(projectile))
						return null;
					
					return new InUseWeapon() {
						@Override public Optional<EntityLivingBase> getUser() {
							Entity thrower = getThrower.invoke(projectile);
							return Optional.fromNullable(
									thrower instanceof EntityLivingBase ?
											(EntityLivingBase) thrower : null
							);
						}
						@Override public Optional<ItemStack> getItem() {
							return Optional.fromNullable(getPickupItem.invoke(projectile));
						}
						@Override public void update(ItemStack item) {
							if (item == null)
								projectile.setDead();
							if (item != getPickupItem.invoke(projectile))
								setThrownItemStack.invoke(projectile, item);
						}
						
						@Override public String toString() {
							return String.format(
									"[Projectile %s thrown by %s]",
									getItem().orNull(), getUser().orNull()
							);
						}
					};
					
				}
			});
			
			
		} catch (Throwable e) {
			OnlySilver.instance.log.error(String.format(ERR_MSG, "weaponmod"), e);
		}
		
	}
	
	
	private static final String
	THAUMCRAFT_API = "thaumcraft.api.ThaumcraftApi",
	ASPECT_LIST = "thaumcraft.api.aspects.AspectList",
	ASPECT = "thaumcraft.api.aspects.Aspect",
	ADD = "add";
	
	private static final Refer<?>
	METAL = Dynamic.refer(ASPECT, "METAL"),
	GREED = Dynamic.refer(ASPECT, "GREED"),
	EARTH = Dynamic.refer(ASPECT, "EARTH");
	
	private static void initThaum() {
		try {
			Dynamic.<Void>invoke(THAUMCRAFT_API, "registerEntityTag")
				.via(Utils.getEntityString(EntitySilverGolem.class))
				.via(ASPECT_LIST, Dynamic.construct(ASPECT_LIST)
						.invoke(ADD).via(ASPECT, METAL).viaInt(4)
						.invoke(ADD).via(ASPECT, GREED).viaInt(3)
						.invoke(ADD).via(ASPECT, EARTH).viaInt(3))
				.via(Utils.newArray(THAUMCRAFT_API + "$EntityTagsNBT", 0))
				.get();
			
			Dynamic.<Void>invoke(THAUMCRAFT_API, "registerObjectTag")
				.via(Dynamic.construct(ItemStack.class)
						.via(Block.class, silverBlock))
				.via(ASPECT_LIST, Dynamic.construct(ASPECT_LIST)
						.invoke(ADD).via(ASPECT, METAL).viaInt(8)
						.invoke(ADD).via(ASPECT, GREED).viaInt(8))
				.get();
			
		} catch (Throwable e) {
			OnlySilver.instance.log.error(String.format(ERR_MSG, "Thaumcraft"), e);
		}
		
	}
	
	
}
