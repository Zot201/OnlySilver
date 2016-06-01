package zotmc.onlysilver.item;

import java.util.List;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.LangData;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.util.Utils;

public class ItemOnlyBow extends ItemBow {
	
	@SideOnly(Side.CLIENT)
	private static class Models {
		static final ModelResourceLocation[] inventoryModels = {
			new ModelResourceLocation(new ResourceLocation(OnlySilvers.MODID, "silver_bow_pulling_0"), "inventory"),
			new ModelResourceLocation(new ResourceLocation(OnlySilvers.MODID, "silver_bow_pulling_1"), "inventory"),
			new ModelResourceLocation(new ResourceLocation(OnlySilvers.MODID, "silver_bow_pulling_2"), "inventory")
		};
	}
	
	public static final String ARROW_FX = OnlySilvers.MODID + "-arrowFx";
	private final ToolMaterial material;
	
	public ItemOnlyBow(ToolMaterial material) {
		this.material = material;
		setFull3D();
		setMaxDamage(material.getMaxUses() * 2 + 1);
	}
	
	@SideOnly(Side.CLIENT)
	@Override public ModelResourceLocation getModel(ItemStack item, EntityPlayer player, int useRemaining) {
		if (player.getItemInUse() != null) {
			if (useRemaining >= 18) return Models.inventoryModels[2];
			if (useRemaining > 13) return Models.inventoryModels[1];
			if (useRemaining > 0) return Models.inventoryModels[0];
		}
		return null;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(LangData.KNOCKBACK_TOOLTIP.get());
	}
	
	@Override public void onPlayerStoppedUsing(ItemStack item, World world, EntityPlayer player, int timeLeft) {
		int charge = getMaxItemUseDuration(item) - timeLeft;
		ArrowLooseEvent event = new ArrowLooseEvent(player, item, charge);
		if (MinecraftForge.EVENT_BUS.post(event))
			return;
		
		charge = event.charge;
		boolean infinity = player.capabilities.isCreativeMode || Utils.getEnchLevel(item, Enchantment.infinity) > 0;
		if (!infinity && !player.inventory.hasItem(Items.arrow))
			return;
		
		float f = charge / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;
		if (f < 0.1D)
			return;
		
		if (f > 1.0F) f = 1.0F;
		
		EntityArrow entityarrow = new EntityArrow(world, player, f * 2.0F);
		entityarrow.getEntityData().setBoolean(ARROW_FX, true);
		if (f == 1.0F)
			entityarrow.setIsCritical(true);
		
		int power = Utils.getEnchLevel(item, Enchantment.power);
		if (power > 0)
			entityarrow.setDamage(entityarrow.getDamage() + power * 0.5D + 0.5D);
		
		entityarrow.setKnockbackStrength(Utils.getEnchLevel(item, Enchantment.punch) + 2); // +2 for silver bow
		
		if (Utils.getEnchLevel(item, Enchantment.flame) > 0)
			entityarrow.setFire(100);
		
		item.damageItem(1, player);
		world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
		
		if (infinity)
			entityarrow.canBePickedUp = 2;
		else
			player.inventory.consumeInventoryItem(Items.arrow);
		
		if (!world.isRemote)
			world.spawnEntityInWorld(entityarrow);
	}
	
	@Override public int getItemEnchantability() {
		return material.getEnchantability() / 3;
	}
	
	@Override public boolean hitEntity(ItemStack item, EntityLivingBase target, EntityLivingBase attacker) {
		if (Config.current().meleeBowKnockback.get()) {
			int punch = Utils.getEnchLevel(item, Enchantment.punch) + 3;
			double x = -MathHelper.sin(attacker.rotationYaw * Utils.PI / 180.0F) * punch * 0.5;
			double z = MathHelper.cos(attacker.rotationYaw * Utils.PI / 180.0F) * punch * 0.5;
			target.addVelocity(x, 0.2, z);
			
			item.damageItem(2, attacker);
			return true;
		}
		
		return false;
	}
	
	public static boolean shotBySilverBow(DamageSource source) {
		if ("arrow".equals(source.damageType)) {
			Entity arrow = source.getSourceOfDamage();
			return arrow != null && arrow.getEntityData().getBoolean(ARROW_FX);
		}
		return false;
	}
	
}
