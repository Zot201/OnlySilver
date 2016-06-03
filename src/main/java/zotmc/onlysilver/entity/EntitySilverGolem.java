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
package zotmc.onlysilver.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zotmc.onlysilver.ClientProxy;
import zotmc.onlysilver.ItemFeature;

import static zotmc.onlysilver.data.ModData.OnlySilvers.MODID;

@ClientProxy.EntityRenderer(RenderSilverGolem.class)
public class EntitySilverGolem extends EntityIronGolem {

  private SoundEvent HIT_SOUND = SoundEvent.REGISTRY.getObject(new ResourceLocation(MODID, "silvergolem.hit"));
  private SoundEvent DEATH_SOUND = SoundEvent.REGISTRY.getObject(new ResourceLocation(MODID, "silvergolem.death"));

  public EntitySilverGolem(World world) {
    super(world);
    setSize(0.8F, 1.9F);
  }

  @Override public void addPotionEffect(PotionEffect potionEffect) {
    if (potionEffect.getPotion() != MobEffects.POISON) super.addPotionEffect(potionEffect);
  }

  @Override protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35);
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
  }

  @Override public EntityItem dropItem(Item item, int stackSize) {
    if (item == Items.IRON_INGOT)
      return super.dropItem(ItemFeature.silverIngot.get(), stackSize);
    return super.dropItem(item, stackSize);
  }


  @Override protected SoundEvent getHurtSound() {
    return HIT_SOUND;
  }

  @Override protected SoundEvent getDeathSound() {
    return DEATH_SOUND;
  }

  @Override protected void playStepSound(BlockPos pos, Block block) {
    super.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.3322327F, 1);
  }

  @Override public void playSound(SoundEvent sound, float volume, float pitch) {
    if (sound != SoundEvents.ENTITY_IRONGOLEM_ATTACK) volume *= 1.6271853F;
    super.playSound(sound, volume, pitch);
  }

}
