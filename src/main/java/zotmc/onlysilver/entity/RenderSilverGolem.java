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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderIronGolem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.data.ModData.OnlySilvers;

@SideOnly(Side.CLIENT)
class RenderSilverGolem extends RenderIronGolem {

  private static final ResourceLocation silverGolemTextures =
      new ResourceLocation(OnlySilvers.MODID + ":textures/entity/silver_golem.png");

  public RenderSilverGolem(RenderManager renderManager) {
    super(renderManager);
  }

  @Override protected ResourceLocation getEntityTexture(EntityIronGolem ironGolem) {
    return silverGolemTextures;
  }

  @Override protected void preRenderCallback(EntityIronGolem living, float f) {
    GlStateManager.scale(8/14f, 19/29f, 8/14f);
  }

}
