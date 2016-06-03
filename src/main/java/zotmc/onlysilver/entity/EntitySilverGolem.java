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
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zotmc.onlysilver.ClientProxy;
import zotmc.onlysilver.ItemFeature;
import zotmc.onlysilver.data.ModData.OnlySilvers;

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
    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(35);
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
  }

  @Override public EntityItem dropItem(Item item, int stackSize) {
    if (item == Items.iron_ingot)
      return super.dropItem(ItemFeature.silverIngot.get(), stackSize);
    return super.dropItem(item, stackSize);
  }


  @Override protected String getHurtSound() {
    return OnlySilvers.MODID + ":silvergolem.hit";
  }

  @Override protected String getDeathSound() {
    return OnlySilvers.MODID + ":silvergolem.death";
  }

  @Override protected void playStepSound(BlockPos pos, Block block) {
    super.playSound("mob.irongolem.walk", 1.3322327F, 1);
  }

  @Override public void playSound(String sound, float volume, float pitch) {
    if (sound == "none") return;
    if (!sound.equals("mob.irongolem.throw")) volume *= 1.6271853F;
    super.playSound(sound, volume, pitch);
  }

}
