package zotmc.onlysilver.handler;

import static zotmc.onlysilver.Contents.silverAxe;
import static zotmc.onlysilver.Contents.silverHoe;
import static zotmc.onlysilver.Contents.silverPick;
import static zotmc.onlysilver.Contents.silverShovel;
import static zotmc.onlysilver.Contents.silverSword;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import zotmc.onlysilver.entity.EntitySilverGolem;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WerewolfHandler {
	
	private final Class<?> werewolf;
	private final Invokable<EntityLivingBase, Boolean> isHumanForm;
	
	public WerewolfHandler() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		werewolf = Class.forName("drzhark.mocreatures.entity.monster.MoCEntityWerewolf");
		isHumanForm = TypeToken.of(EntityLivingBase.class)
					.method(werewolf.getMethod("getIsHumanForm"))
					.returning(Boolean.class);
	}
	
	private boolean isWerewolfInWolfForm(EntityLivingBase entityLiving) {
		try {
			return werewolf.isInstance(entityLiving) && !isHumanForm.invoke(entityLiving);
		} catch (InvocationTargetException ignored) {
		} catch (IllegalAccessException ignored) { }
		
		return false;
	}
	
	private Random rand = new Random();
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
		
		ItemStack stack = ((EntityLivingBase) entity).getHeldItem();
		if (stack == null)
			return;
		
		Item item = stack.getItem();
		if (item == silverHoe.get())
			event.ammount = 6;
		else if (item == silverShovel.get())
			event.ammount = 7;
		else if (item == silverPick.get())
			event.ammount = 8;
		else if (item == silverAxe.get())
			event.ammount = 9;
		else if (item == silverSword.get())
			event.ammount = 10;
		
	}
	
}
