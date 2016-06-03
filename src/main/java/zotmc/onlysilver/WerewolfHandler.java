/*
 * Copyright 2016 Zot201
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zotmc.onlysilver;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
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

class WerewolfHandler<MoCEntityWerewolf extends EntityLivingBase> {

  private final EventBus bus;
  private final Class<MoCEntityWerewolf> werewolfType;
  private final Invokable<MoCEntityWerewolf, Boolean> getIsHumanForm;
  private final Random rand = new Random();
  private boolean enabled;
  
  WerewolfHandler(EventBus bus) throws Throwable {
    this.bus = bus;
    //noinspection unchecked
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
    DamageSource s = event.getSource();
    
    if (s != null && isWerewolfInWolfForm(event.getEntityLiving())) {
      float a = event.getAmount();

      if (s.getEntity() instanceof EntitySilverGolem) {
        event.setAmount(Math.max(a, 10 + rand.nextInt(15)));
        return;
      }

      if (a < 6 && ItemOnlyBow.shotBySilverBow(s)) {
        event.setAmount(6);
        return;
      }

      AttackItem attackItem = OnlySilverUtils.getAttackItem(s);

      if (attackItem != null) {
        event.setAmount(Math.max(a, OnlySilverUtils.getWerewolfDamage(attackItem.getItem(), a)));
      }
    }
  }
  
}
