package zotmc.onlysilver.ench;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.enchantment.EnumEnchantmentType.all;
import static net.minecraft.enchantment.EnumEnchantmentType.digger;
import static zotmc.onlysilver.Contents.everlasting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.api.OnlySilverRegistry.InUseWeapon;

import com.google.common.base.Optional;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EnchIncantation extends Enchantment {

	public EnchIncantation(int id) {
		super(id, 1, all);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override public int getMinLevel() {
		return 1;
	}
	@Override public int getMaxLevel() {
		return 2;
	}
	
	@Override public int getMinEnchantability(int lvl) {
		return 15 + 9 * (lvl - 1);
	}
	@Override public int getMaxEnchantability(int lvl) {
		return super.getMaxEnchantability(lvl) + 50;
	}
	
	@Override public boolean canApply(ItemStack item) {
		return (item.getItem() instanceof ItemSword || digger.canEnchantItem(item.getItem()))
				&& OnlySilverRegistry.isSilverItem(item);
	}
	
	@Override public boolean isAllowedOnBooks() {
		return false;
	}
	
	
	
	private final Random rand = new Random();
	
	@SubscribeEvent public void onLivingDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote)
			return;
		
		InUseWeapon iuw = OnlySilverRegistry.getWeapon(event.source);
		Optional<ItemStack> weapon = iuw.getItem();
		
		if (weapon.isPresent() && weapon.get().stackSize > 0) {
			Optional<EntityLivingBase> user = iuw.getUser();
			int lvl = getEnchantmentLevel(effectId, weapon.get());
			
			if (lvl > 0) {
				int factor = lvl * 10 - 5;
				
				boolean damaged = false;
				for (EntityItem ei : event.drops) {
					ItemStack item = ei.getEntityItem();
					
					if (item != null && !item.isItemEnchanted() && item.isItemEnchantable()) {
						addRandomEnchantment(rand, item, factor);
						ei.setEntityItemStack(item);
						
						if (user.isPresent())
							weapon.get().damageItem(factor * 2, user.get());
						else
							weapon.get().attemptDamageItem(factor * 2, rand);
						
						damaged = true;
						
						if (weapon.get().stackSize <= 0)
							break;
					}
				}
				
				if (damaged)
					iuw.update(weapon.get().stackSize > 0 ?
							weapon.get() : null);
				
			}
		}
		
	}
	
	
	
	
	
	// copied codes
	
	@SuppressWarnings("rawtypes")
	public static ItemStack addRandomEnchantment(Random par0Random,
			ItemStack par1ItemStack, int par2) {
		List list = buildEnchantmentList(par0Random, par1ItemStack, par2);
		boolean flag = par1ItemStack.getItem() == Items.book;
		if (flag) {
			par1ItemStack.func_150996_a(Items.enchanted_book);
		}
		if (list != null) {
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				EnchantmentData enchantmentdata = (EnchantmentData) iterator
						.next();
				if (flag) {
					Items.enchanted_book.addEnchantment(par1ItemStack,
							enchantmentdata);
				} else {
					par1ItemStack.addEnchantment(
							enchantmentdata.enchantmentobj,
							enchantmentdata.enchantmentLevel);
				}
			}
		}
		return par1ItemStack;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List buildEnchantmentList(Random par0Random,
			ItemStack par1ItemStack, int par2) {
		Item item = par1ItemStack.getItem();
		int j = item.getItemEnchantability();
		if (j <= 0) {
			return null;
		}
		j /= 2;
		j = 1 + par0Random.nextInt((j >> 1) + 1)
				+ par0Random.nextInt((j >> 1) + 1);
		int k = j + par2;
		float f = (par0Random.nextFloat() + par0Random.nextFloat() - 1.0F) * 0.15F;
		int l = (int) (k * (1.0F + f) + 0.5F);
		if (l < 1) {
			l = 1;
		}
		ArrayList arraylist = null;
		Map map = mapEnchantmentData(l, par1ItemStack);
		if ((map != null) && (!map.isEmpty())) {
			EnchantmentData enchantmentdata = (EnchantmentData) WeightedRandom
					.getRandomItem(par0Random, map.values());
			if (enchantmentdata != null) {
				arraylist = new ArrayList();
				arraylist.add(enchantmentdata);
				for (int i1 = l; par0Random.nextInt(50) <= i1; i1 >>= 1) {
					Iterator iterator = map.keySet().iterator();
					while (iterator.hasNext()) {
						Integer integer = (Integer) iterator.next();
						boolean flag = true;
						Iterator iterator1 = arraylist.iterator();
						while (iterator1.hasNext()) {
							EnchantmentData enchantmentdata1 = (EnchantmentData) iterator1
									.next();
							if (!enchantmentdata1.enchantmentobj
									.canApplyTogether(Enchantment.enchantmentsList[integer
											.intValue()])) {
								flag = false;
							}
						}
						if (!flag) {
							iterator.remove();
						}
					}
					if (!map.isEmpty()) {
						EnchantmentData enchantmentdata2 = (EnchantmentData) WeightedRandom
								.getRandomItem(par0Random, map.values());
						arraylist.add(enchantmentdata2);
					}
				}
			}
		}
		return arraylist;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map mapEnchantmentData(int par0, ItemStack par1ItemStack) {
		Item item = par1ItemStack.getItem();
		HashMap hashmap = null;
		boolean flag = par1ItemStack.getItem() == Items.book;
		Enchantment[] aenchantment = Enchantment.enchantmentsList;
		int j = aenchantment.length;
		for (int k = 0; k < j; k++) {
			Enchantment enchantment = aenchantment[k];
			
			//---------------------------changes begin here----------------------------->>
			if (enchantment != null
					&& (enchantment == everlasting.orNull()
							||enchantment.canApplyAtEnchantingTable(par1ItemStack)
							|| item == Items.book && enchantment.isAllowedOnBooks())) {
			//----------------------------changes end here------------------------------>>
				
				for (int l = enchantment.getMinLevel(); l <= enchantment
						.getMaxLevel(); l++) {
					if ((par0 >= enchantment.getMinEnchantability(l))
							&& (par0 <= enchantment.getMaxEnchantability(l))) {
						if (hashmap == null) {
							hashmap = new HashMap();
						}
						hashmap.put(Integer.valueOf(enchantment.effectId),
								new EnchantmentData(enchantment, l));
					}
				}
			}
		}
		return hashmap;
	}
	
	
	
	
	
}
