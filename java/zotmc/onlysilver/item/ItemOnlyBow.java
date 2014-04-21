package zotmc.onlysilver.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.Recipes;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemOnlyBow extends ItemBow {
	public static IIcon silverBow;
	public static IIcon silverBow1;
	public static IIcon silverBow2;
	public static IIcon silverBow3;

	public ItemOnlyBow(int dam) {
		maxStackSize = 1;
		canRepair = true;
		setCreativeTab(OnlySilver.TAB_ONLY_SILVER);
		bFull3D = true;
		setMaxDamage(dam);
	}

	@Override @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon("onlysilver:" + "silverBow");

		silverBow = iconRegister.registerIcon("onlysilver:" + "silverBow");
		silverBow1 = iconRegister.registerIcon("onlysilver:" + "silverBow1");
		silverBow2 = iconRegister.registerIcon("onlysilver:" + "silverBow2");
		silverBow3 = iconRegister.registerIcon("onlysilver:" + "silverBow3");
	}

	@Override public IIcon getIcon(
			ItemStack itemStack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
			GL11.glTranslatef(0.0F, -0.6F, -0.025F);
			GL11.glRotatef(-17.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(14.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(4.5F, 0.0F, 1.0F, 0.0F);
		}

		if (player.getItemInUse() == null)
			return this.itemIcon;
		int var8 = itemStack.getMaxItemUseDuration() - useRemaining;
		if (var8 >= 18)
			return silverBow3;
		if (var8 > 13)
			return silverBow2;
		if (var8 > 0)
			return silverBow1;
		return this.itemIcon;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("tips.knockbackTooltip"));
	}

	@Override public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int par4) {
		int j = getMaxItemUseDuration(stack) - par4;
		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled())
			return;
		
		j = event.charge;
		boolean flag = (player.capabilities.isCreativeMode) || (EnchantmentHelper.getEnchantmentLevel(
						Enchantment.infinity.effectId, stack) > 0);
		if (!flag && !player.inventory.hasItem(Items.arrow))
			return;
		
		float f = j / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f < 0.1D)
			return;

		if (f > 1.0F)
			f = 1.0F;
		
		EntityArrow entityarrow = new EntityArrow(world, player, f * 2.0F);
		if (f == 1.0F)
			entityarrow.setIsCritical(true);
		
		int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
		if (k > 0)
			entityarrow.setDamage(entityarrow.getDamage() + k * 0.5D + 0.5D);
		
		int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
		if (l > 0)
			entityarrow.setKnockbackStrength(l + 2); // +2 for silver bow
		
		if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
			entityarrow.setFire(100);
		
		stack.damageItem(1, player);
		world.playSoundAtEntity(player, "random.bow", 1.0F,
				1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
		
		if (flag)
			entityarrow.canBePickedUp = 2;
		else
			player.inventory.consumeInventoryItem(Items.arrow);

		if (world.isRemote)
			return;
		world.spawnEntityInWorld(entityarrow);
	}

	@Override public int getItemEnchantability() {
		return 10;
	}
	
	@Override public boolean getIsRepairable(ItemStack toolToRepair, ItemStack material) {
		for (ItemStack i : OreDictionary.getOres(Recipes.SILVER_INGOT))
			if (OreDictionary.itemMatches(i, material, false))
				return true;
		return super.getIsRepairable(toolToRepair, material);
	}
	
}
