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
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zotmc.onlysilver.ClientProxy;
import zotmc.onlysilver.Contents;

import javax.annotation.Nullable;

@ClientProxy.EntityRenderer(RenderSilverGolem.class)
public class EntitySilverGolem extends EntityIronGolem {

  public EntitySilverGolem(World world) {
    super(world);
    setSize(0.8F, 1.9F);
  }

  @Override public void addPotionEffect(PotionEffect potionEffect) {
    if (potionEffect.getPotion() != MobEffects.POISON) super.addPotionEffect(potionEffect);
  }

  @Override protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35);
    getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
  }

  @Nullable @Override protected ResourceLocation getLootTable() {
    return Contents.silverGolemLoots.get();
  }


  @Override protected SoundEvent getHurtSound() {
    return Contents.silverGolemHit.get();
  }

  @Override protected SoundEvent getDeathSound() {
    return Contents.silverGolemDeath.get();
  }

  @Override protected void playStepSound(BlockPos pos, Block block) {
    super.playSound(SoundEvents.ENTITY_IRONGOLEM_STEP, 1.3322327F, 1);
  }

  @Override public void playSound(SoundEvent sound, float volume, float pitch) {
    if (sound != SoundEvents.ENTITY_IRONGOLEM_ATTACK) volume *= 1.6271853F;
    super.playSound(sound, volume, pitch);
  }

}
