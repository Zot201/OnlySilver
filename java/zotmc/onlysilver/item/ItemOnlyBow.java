package zotmc.onlysilver.item;

import static net.minecraft.enchantment.Enchantment.flame;
import static net.minecraft.enchantment.Enchantment.infinity;
import static net.minecraft.enchantment.Enchantment.power;
import static net.minecraft.enchantment.Enchantment.punch;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.init.Items.arrow;
import static net.minecraft.util.MathHelper.cos;
import static net.minecraft.util.MathHelper.sin;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
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

import zotmc.onlysilver.Config;
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
		boolean inf = (player.capabilities.isCreativeMode)
				|| (getEnchantmentLevel(infinity.effectId, stack) > 0);
		if (!inf && !player.inventory.hasItem(arrow))
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
		
		int pow = getEnchantmentLevel(power.effectId, stack);
		if (pow > 0)
			entityarrow.setDamage(entityarrow.getDamage() + pow * 0.5D + 0.5D);
		
		entityarrow.setKnockbackStrength(
				getEnchantmentLevel(punch.effectId, stack) + 2); // +2 for silver bow
		
		if (getEnchantmentLevel(flame.effectId, stack) > 0)
			entityarrow.setFire(100);
		
		stack.damageItem(1, player);
		world.playSoundAtEntity(player, "random.bow", 1.0F,
				1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
		
		if (inf)
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
	
	@Override public boolean hitEntity(ItemStack stack, EntityLivingBase victim, EntityLivingBase user) {
		if (Config.enableMeleeBow.get()) {
			int pun = getEnchantmentLevel(punch.effectId, stack) + 2;
			
			double
			x = -sin(user.rotationYaw * (float) Math.PI / 180.0F) * pun * 0.5,
			z = cos(user.rotationYaw * (float) Math.PI / 180.0F) * pun * 0.5;
	
			victim.addVelocity(x, 0.1, z);
		}
		
		return super.hitEntity(stack, victim, user);
	}
	
}
