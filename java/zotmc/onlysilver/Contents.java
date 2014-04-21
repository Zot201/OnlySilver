package zotmc.onlysilver;

import static net.minecraftforge.common.util.EnumHelper.addArmorMaterial;
import static net.minecraftforge.common.util.EnumHelper.addToolMaterial;
import static zotmc.onlysilver.OnlySilver.MODID;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import zotmc.onlysilver.Content.ItemContent;
import zotmc.onlysilver.api.OnlySilverAPI;
import zotmc.onlysilver.block.BlockOnlySilver;
import zotmc.onlysilver.block.BlockOnlyStorage;
import zotmc.onlysilver.enchantment.EnchEverlasting;
import zotmc.onlysilver.enchantment.EnchIncantation;
import zotmc.onlysilver.item.ItemOnlyArmor;
import zotmc.onlysilver.item.ItemOnlyAxe;
import zotmc.onlysilver.item.ItemOnlyBow;
import zotmc.onlysilver.item.ItemOnlyHoe;
import zotmc.onlysilver.item.ItemOnlyIngot;
import zotmc.onlysilver.item.ItemOnlyPickaxe;
import zotmc.onlysilver.item.ItemOnlySpade;
import zotmc.onlysilver.item.ItemOnlySword;
import cpw.mods.fml.common.registry.GameRegistry;

public class Contents extends Config {
	
	static void init() {
		initBlocks();
		initItems();
		initEnchs();
		
		/*
		if (enableBalkon.get() && Loader.isModLoaded("weaponmod"))
			try {
				ContentsBalkon.init();
			} catch (Exception e) {
				FMLLog.log(Level.ERROR, e,
						"[%s] An error occurred while trying to access the weapon mod contents!", NAME);
			}
		*/
		
		
		try {
			Method m = OnlySilverAPI.class.getDeclaredMethod("init");
			m.setAccessible(true);
			m.invoke(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	
	
	public static final Content<Block>
	silverOre = Content.absent(),
	silverBlock = Content.absent();
	
	
	private static void initBlocks() {
		silverOre.set(register(
				new BlockOnlySilver(Material.rock, "silverOre")
					.setHardness(silverOreHardness.get())
					.setResistance(silverOreResistance.get())
					.setBlockName("silverOre")));
		silverOre.get().setHarvestLevel("pickaxe", silverOreHarvestLevel.get());
		
		silverBlock.set(register(
				new BlockOnlyStorage(Material.iron, "silverBlock")
					.setHardness(silverBlockHardness.get())
					.setResistance(silverBlockResistance.get())
					.setBlockName("silverBlock")));
	}
	
	private static Block register(Block block) {
		return GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5)); // without "tile."
	}
	
	
	
	
	public static Content<ToolMaterial>
	toolSilver = Content.absent();
	
	public static Content<ArmorMaterial>
	armorSilver = Content.absent();
	
	public static final ItemContent<Item>
	silverIngot = Content.absentItem(),
	silverRod = Content.absentItem(),
	silverPick = Content.absentItem(),
	silverAxe = Content.absentItem(),
	silverShovel = Content.absentItem(),
	silverSword = Content.absentItem(),
	silverHoe = Content.absentItem(),
	silverBow = Content.absentItem(),
	silverHelm = Content.absentItem(),
	silverChest = Content.absentItem(),
	silverLegs = Content.absentItem(),
	silverBoots = Content.absentItem(),
	
	silverSpear = Content.absentItem(),
	silverHalberd = Content.absentItem(),
	silverBattleaxe = Content.absentItem(),
	silverKnife = Content.absentItem(),
	silverWarhammer = Content.absentItem(),
	silverFlail = Content.absentItem(),
	silverKatana = Content.absentItem(),
	silverBoomerang = Content.absentItem(),
	silverBayonetMusket = Content.absentItem();
	
	
	
	private static void initItems() {
		toolSilver.set(addToolMaterial("SILVER",
				silverHarvestLevel.get(),
				silverMaxUses.get(),
				silverEfficiency.get(),
				silverDamage.get(),
				silverEnchant.get()));
		armorSilver.set(addArmorMaterial("SILVER",
				silverArmorDurability.get(),
				silverArmorReduction.get(),
				silverArmorEnchant.get()));
		
		
		silverIngot.set(register(
				new ItemOnlyIngot("silverIngot").setUnlocalizedName("silverIngot")));
		silverRod.set(register(
				new ItemOnlyIngot("silverRod").setUnlocalizedName("silverRod")));
		
		silverPick.set(register(
				new ItemOnlyPickaxe(toolSilver.get(), "silverPick").setUnlocalizedName("silverPick")));
		silverAxe.set(register(
				new ItemOnlyAxe(toolSilver.get(), "silverAxe").setUnlocalizedName("silverAxe")));
		silverShovel.set(register(
				new ItemOnlySpade(toolSilver.get(), "silverShovel").setUnlocalizedName("silverShovel")));
		silverSword.set(register(
				new ItemOnlySword(toolSilver.get(), "silverSword").setUnlocalizedName("silverSword")));
		silverHoe.set(register(
				new ItemOnlyHoe(toolSilver.get(), "silverHoe").setUnlocalizedName("silverHoe")));
		silverBow.set(register(
				new ItemOnlyBow(500).setFull3D().setUnlocalizedName("silverBow")));
		
		
		int render = OnlySilver.proxy.addArmor("silver");
		silverHelm.set(register(
				new ItemOnlyArmor(armorSilver.get(), render, 0, "silverHelm").setUnlocalizedName("silverHelm")));
		silverChest.set(register(
				new ItemOnlyArmor(armorSilver.get(), render, 1, "silverChest").setUnlocalizedName("silverChest")));
		silverLegs.set(register(
				new ItemOnlyArmor(armorSilver.get(), render, 2, "silverLegs").setUnlocalizedName("silverLegs")));
		silverBoots.set(register(
				new ItemOnlyArmor(armorSilver.get(), render, 3, "silverBoots").setUnlocalizedName("silverBoots")));
		
		
		armorSilver.get().customCraftingMaterial = silverIngot.get();
        toolSilver.get().customCraftingMaterial = silverIngot.get();
        
	}
	
	private static Item register(Item item) {
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5)); // without "item."
		return item;
	}
	
	
	
	
	public static final Content<EnchEverlasting>
	everlasting = Content.absent();
	
	public static final Content<Enchantment>
	incantation = Content.absent();
	
	
	private static void initEnchs() {
		if (enableEverlasting.get())
			everlasting.set(new EnchEverlasting(everlastingID.get()).setName(MODID + ".everlasting"));
		
		if (enableIncantation.get())
			incantation.set(new EnchIncantation(incantationID.get()).setName(MODID + ".incantation"));
		
	}
	
	

}
