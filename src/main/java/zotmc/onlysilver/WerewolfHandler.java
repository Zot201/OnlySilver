package zotmc.onlysilver;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zotmc.onlysilver.api.AttackItem;
import zotmc.onlysilver.api.OnlySilverUtils;
import zotmc.onlysilver.config.AbstractConfig;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.ModData.MoCreatures;
import zotmc.onlysilver.entity.EntitySilverGolem;
import zotmc.onlysilver.item.ItemOnlyBow;

import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

public class WerewolfHandler<MoCEntityWerewolf extends EntityLivingBase> {

	private final EventBus bus;
	private final Class<MoCEntityWerewolf> werewolfType;
	private final Invokable<MoCEntityWerewolf, Boolean> getIsHumanForm;
	private final Random rand = new Random();
	private boolean enabled;
	
	@SuppressWarnings("unchecked")
	public WerewolfHandler(EventBus bus) throws Throwable {
		this.bus = bus;
		werewolfType = (Class<MoCEntityWerewolf>) Class.forName(MoCreatures.MOC_ENTITY_WEREWOLF);
		getIsHumanForm = TypeToken.of(werewolfType)
				.method(werewolfType.getDeclaredMethod(MoCreatures.GET_IS_HUMAN_FORM))
				.returning(boolean.class);
	}
	
	@SubscribeEvent public void onConfigAccept(AbstractConfig.Accept event) {
		if (enabled = Config.current().werewolfEffectiveness.get()) bus.register(this);
	}
	
	@SubscribeEvent public void onConfigDiscard(AbstractConfig.Discard event) {
		if (enabled) bus.unregister(this);
	}
	
	private boolean isWerewolfInWolfForm(EntityLivingBase entityLiving) {
		if (werewolfType.isInstance(entityLiving))
			try {
				return getIsHumanForm.invoke(werewolfType.cast(entityLiving));
			} catch (Throwable t) {
				OnlySilver.INSTANCE.log.catching(t);
			}
		return false;
	}
	
	@SubscribeEvent public void onLivingHurt(LivingHurtEvent event) {
		if (event.source == null || !isWerewolfInWolfForm(event.entityLiving))
			return;
		
		if (event.source.getEntity() instanceof EntitySilverGolem) {
			event.ammount = Math.max(event.ammount, 10 + rand.nextInt(15));
			return;
		}
		
		if (event.ammount < 6 && ItemOnlyBow.shotBySilverBow(event.source)) {
			event.ammount = 6;
			return;
		}
		
		AttackItem attackItem = OnlySilverUtils.getAttackItem(event.source);
		if (attackItem != null) {
			float newAmount = OnlySilverUtils.getWerewolfDamage(attackItem.getItem(), event.ammount);
			event.ammount = Math.max(event.ammount, newAmount);
		}
	}
	
}
