package zotmc.onlysilver.handler;

import static net.minecraft.entity.SharedMonsterAttributes.attackDamage;
import static zotmc.onlysilver.Contents.toolSilver;
import static zotmc.onlysilver.item.ItemOnlyBow.isShotBySilverBow;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.api.OnlySilverRegistry;
import zotmc.onlysilver.entity.EntitySilverGolem;
import zotmc.onlysilver.item.Instrumentum;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WerewolfHandler {
	
	private final Class<? extends EntityLivingBase> werewolf;
	private final Invokable<EntityLivingBase, Boolean> getIsHumanForm;
	
	public WerewolfHandler() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		werewolf = Utils.getClassChecked("drzhark.mocreatures.entity.monster.MoCEntityWerewolf");
		getIsHumanForm = Utils.upcast(werewolf)
				.method(werewolf.getDeclaredMethod("getIsHumanForm"))
				.returning(boolean.class);
		
		
		for (Instrumentum inst : Instrumentum.values())
			if (inst.exists() && inst.isTool()) {
				BaseAttributeMap attrs = new ServersideAttributeMap();
				attrs.registerAttribute(attackDamage);
				attrs.applyAttributeModifiers(new ItemStack(inst.get()).getAttributeModifiers());
				
				float damage = (float) attrs.getAttributeInstance(attackDamage).getAttributeValue();
				damage = 6 + Math.max(0, damage - 2 - toolSilver.get().getDamageVsEntity());
				OnlySilverRegistry.registerWerewolfDamage(inst.get(), Utils.<ItemStack, Float>constant(damage));
			}
		
	}
	
	
	protected boolean isWerewolfInWolfForm(EntityLivingBase entityLiving) {
		try {
			return werewolf.isInstance(entityLiving) && !getIsHumanForm.invoke(entityLiving);
		} catch (Throwable e) {
			OnlySilver.instance.log.catching(e);
		}
		
		return false;
	}
	
	protected final Random rand = new Random();
	@SubscribeEvent public void onLivingHurt(LivingHurtEvent event) {
		if (event.source == null || !isWerewolfInWolfForm(event.entityLiving))
			return;
		
		if (event.source.getEntity() instanceof EntitySilverGolem) {
			event.ammount = 10 + rand.nextInt(15);
			return;
		}
		
		if (event.ammount < 6 && isShotBySilverBow(event.source)) {
			event.ammount = 6;
			return;
		}
		
		Optional<ItemStack> weapon = OnlySilverRegistry.getWeapon(event.source).getItem();
		if (weapon.isPresent() && weapon.get().stackSize > 0) {
			Float amount = OnlySilverRegistry.getWerewolfDamage(weapon.get());
			if (amount != null)
				event.ammount = Math.max(amount, event.ammount);
		}
		
	}
	
}
