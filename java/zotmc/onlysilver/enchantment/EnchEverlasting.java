package zotmc.onlysilver.enchantment;

import static cpw.mods.fml.common.eventhandler.EventPriority.LOWEST;
import static cpw.mods.fml.common.gameevent.TickEvent.Phase.START;
import static cpw.mods.fml.common.network.FMLOutboundHandler.FML_MESSAGETARGET;
import static cpw.mods.fml.common.network.FMLOutboundHandler.FML_MESSAGETARGETARGS;
import static cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget.DIMENSION;
import static cpw.mods.fml.relauncher.Side.CLIENT;
import static cpw.mods.fml.relauncher.Side.SERVER;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static zotmc.onlysilver.OnlySilver.channels;
import static zotmc.onlysilver.OnlySilver.proxy;
import static zotmc.onlysilver.Reflections.dropBlockAsItem;
import static zotmc.onlysilver.Reflections.Fields.INVULNERABLE;
import static zotmc.onlysilver.api.OnlySilverRegistry.getWeapon;
import static zotmc.onlysilver.api.OnlySilverRegistry.isSilverItem;
import static zotmc.onlysilver.handler.JoinWorldHandler.HANDLED_KEY;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import zotmc.onlysilver.api.OnlySilverRegistry.InUseWeapon;
import zotmc.onlysilver.handler.ChannelHandler.EverlastingMessage;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.SideOnly;

public class EnchEverlasting extends Enchantment {

	public EnchEverlasting(int id) {
		super(id, 5, EnumEnchantmentType.breakable);
		
		MinecraftForge.EVENT_BUS.register(this);
		proxy.fmlEventBusClientRegister(this);
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
		return canApply(item) && isSilverItem(item);
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
	

	@SubscribeEvent public void onLivingDrop(LivingDropsEvent event) {
		if (event.entity.worldObj.isRemote)
			return;
		
		InUseWeapon iuw = getWeapon(event.source);
		
		if (iuw != null) {
			ItemStack weapon = iuw.get();
			
			if (weapon != null && weapon.stackSize > 0) {
				int lvl = getEnchantmentLevel(effectId, weapon);
				
				if (lvl > 0)
					for (EntityItem ei : event.drops) {
						processEntityItem(false, ei);
						dispatchMessage(false, ei);
					}
				
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
		INVULNERABLE.set(ei, true);
		if (increaseLifespan)
			ei.lifespan *= 3;
		
	}
	
	private void dispatchMessage(boolean increaseLifespan, EntityItem ei) {
		channels.get(SERVER).attr(FML_MESSAGETARGET).set(DIMENSION);
		channels.get(SERVER).attr(FML_MESSAGETARGETARGS).set(ei.worldObj.provider.dimensionId);
		
		channels.get(SERVER).writeOutbound(new EverlastingMessage(increaseLifespan, ei.getEntityId()));
		
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
