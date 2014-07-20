package zotmc.onlysilver.handler;

import static net.minecraft.entity.SharedMonsterAttributes.attackDamage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.entity.EntitySilverGolem;
import zotmc.onlysilver.item.Instrumentum;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WerewolfHandler {
	
	private final Class<?> werewolf;
	private final Method getIsHumanForm;
	public WerewolfHandler() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		werewolf = Class.forName("drzhark.mocreatures.entity.monster.MoCEntityWerewolf");
		getIsHumanForm = werewolf.getMethod("getIsHumanForm");
	}
	
	protected boolean isWerewolfInWolfForm(EntityLivingBase entityLiving) {
		try {
			return werewolf.isInstance(entityLiving) && !(Boolean) getIsHumanForm.invoke(entityLiving);
		} catch (InvocationTargetException | IllegalAccessException e) {
			OnlySilver.instance.log.catching(e);
		}
		
		return false;
	}
	
	protected final Random rand = new Random();
	@SubscribeEvent public void onLivingHurt(LivingHurtEvent event) {
		if (!isWerewolfInWolfForm(event.entityLiving))
			return;
		
		Entity entity;
		if (!(event.source instanceof EntityDamageSource
				&& (entity = event.source.getEntity()) instanceof EntityLivingBase))
			return;
		
		if (entity instanceof EntitySilverGolem) {
			event.ammount = 10 + rand.nextInt(15);
			return;
		}
		
		ItemStack item = ((EntityLivingBase) entity).getHeldItem();
		if (item == null)
			return;
		
		Float amount = damages.get(item.getItem());
		if (amount != null)
			event.ammount = amount;
		
	}
	
	protected final ImmutableMap<Item, Float> damages;
	{
		ImmutableMap.Builder<Item, Float> damages = ImmutableMap.builder();
		for (Instrumentum inst : Instrumentum.values())
			if (inst.exists() && inst.isTool()) {
				BaseAttributeMap attrs = new ServersideAttributeMap();
				attrs.registerAttribute(attackDamage);
				attrs.applyAttributeModifiers(new ItemStack(inst.get()).getAttributeModifiers());
				
				float damage = (float) attrs.getAttributeInstance(attackDamage).getAttributeValue();
				if (damage < 5)
					damage = (damage + 16) / 3;
				else
					damage += 2;
				damages.put(inst.get(), damage);
			}
		this.damages = damages.build();
	}
	
}
