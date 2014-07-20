package zotmc.onlysilver.ench;

import static cpw.mods.fml.common.eventhandler.EventPriority.LOWEST;
import static cpw.mods.fml.common.gameevent.TickEvent.Phase.START;
import static cpw.mods.fml.common.network.FMLOutboundHandler.FML_MESSAGETARGET;
import static cpw.mods.fml.common.network.FMLOutboundHandler.FML_MESSAGETARGETARGS;
import static cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget.DIMENSION;
import static cpw.mods.fml.relauncher.Side.CLIENT;
import static cpw.mods.fml.relauncher.Side.SERVER;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.enchantment.EnumEnchantmentType.all;
import static net.minecraft.enchantment.EnumEnchantmentType.breakable;
import static zotmc.onlysilver.api.OnlySilverRegistry.getWeapon;
import static zotmc.onlysilver.api.OnlySilverRegistry.isSilverItem;
import static zotmc.onlysilver.handler.JoinWorldHandler.HANDLED_KEY;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import zotmc.onlysilver.Obfuscations;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.api.OnlySilverRegistry.InUseWeapon;
import zotmc.onlysilver.handler.ChannelHandler.EverlastingMessage;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.SideOnly;

public class EnchEverlasting extends Enchantment {

	public EnchEverlasting(int id) {
		super(id, 5, all);
		
		MinecraftForge.EVENT_BUS.register(this);
		OnlySilver.proxy.fmlEventBusClientRegister(this);
	}
	
	@Override public EnchEverlasting setName(String par1Str) {
		super.setName(par1Str);
		return this;
	}
	
	@Override public int getMinLevel() {
		return 1;
	}
	@Override public int getMaxLevel() {
		return 1;
	}
	
	@Override public int getMinEnchantability(int lvl) {
		return 5 + 20 * (lvl - 1);
	}
	@Override public int getMaxEnchantability(int lvl) {
		return super.getMaxEnchantability(lvl) + 50;
	}
	
	@Override public boolean canApplyAtEnchantingTable(ItemStack item) {
		return breakable.canEnchantItem(item.getItem()) && isSilverItem(item);
	}
	
	@Override public boolean isAllowedOnBooks() {
		return false;
	}

	
	
	
	
	@SubscribeEvent(priority = LOWEST)
	public void onHarvestDrops(HarvestDropsEvent event) {
		if (event.world.isRemote || event.harvester == null)
			return;
		
		if (getEnchantmentLevel(effectId, event.harvester.getHeldItem()) > 0) {
			synchronized (this) {
				onHarvestDropsEffects = true;
				
				for (ItemStack drop : event.drops)
					if (event.world.rand.nextFloat() <= event.dropChance)
						dropBlockAsItem(event.block, event.world, event.x, event.y, event.z, drop);
				
				onHarvestDropsEffects = false;
			}
			
			event.drops.clear();
		}
		
	}
	
	private static void dropBlockAsItem(Block block, World world, int x, int y, int z, ItemStack drop) {
		try {
			Obfuscations.DROP_BLOCK_AS_ITEM.invoke(block, world, x, y, z, drop);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	

	@SubscribeEvent(priority = LOWEST) public void onLivingDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote)
			return;
		
		InUseWeapon iuw = getWeapon(event.source);
		Optional<ItemStack> weapon = iuw.getItem();
		
		if (weapon.isPresent() && weapon.get().stackSize > 0) {
			int lvl = getEnchantmentLevel(effectId, weapon.get());
			
			if (lvl > 0)
				for (EntityItem ei : event.drops) {
					processEntityItem(false, ei);
					dispatchMessage(false, ei);
				}
			
		}

	}
	
	@SubscribeEvent public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!(event.entity instanceof EntityItem))
			return;
		
		EntityItem ei = (EntityItem) event.entity;
		
		if (!event.world.isRemote)
			onEntityItemJoinWorld(ei);
		else {
			Boolean increaseLifespan = pending.get(ei.getEntityId());
			if (increaseLifespan != null)
				processEntityItem(increaseLifespan, ei);
		}
	}
	
	
	
	private boolean onHarvestDropsEffects;
	
	private void onEntityItemJoinWorld(EntityItem ei) {
		ItemStack item = ei.getEntityItem();
		
		if (!item.isStackable() && getEnchantmentLevel(effectId, item) > 0) {
			
			if (!ei.getEntityData().getBoolean(HANDLED_KEY)) {
				
				ei.getEntityData().setBoolean(HANDLED_KEY, true);
				processEntityItem(true, ei);
				dispatchMessage(true, ei);
			}
		}
		else
			synchronized (this) {
				if (onHarvestDropsEffects) {
					processEntityItem(false, ei);
					dispatchMessage(false, ei);
				}
			}
		
	}
	
	
	public void processEntityItem(boolean increaseLifespan, EntityItem ei) {
		Utils.<Boolean>set(Obfuscations.INVULNERABLE, ei, true);
		
		if (increaseLifespan)
			ei.lifespan *= 3;
		
	}
	
	
	private void dispatchMessage(boolean increaseLifespan, EntityItem ei) {
		OnlySilver.channels().get(SERVER)
			.attr(FML_MESSAGETARGET).set(DIMENSION);
		OnlySilver.channels().get(SERVER)
			.attr(FML_MESSAGETARGETARGS).set(ei.worldObj.provider.dimensionId);
		
		OnlySilver.channels().get(SERVER)
			.writeOutbound(new EverlastingMessage(increaseLifespan, ei.getEntityId()));
		
	}
	
	

	@SideOnly(CLIENT)
	private Map<Integer, Boolean> pending;
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == CLIENT)
			pending = Maps.newConcurrentMap();
	}

	@SideOnly(CLIENT)
	public void addClientPending(boolean increaseLifespan, int entityId) {
		pending.put(entityId, increaseLifespan);
	}
	
	@SideOnly(CLIENT)
	@SubscribeEvent public void onClientTick(ClientTickEvent event) {
		if (event.phase != START)
			return;
		
		pending.clear();
	}
	
}
