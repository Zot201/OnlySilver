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

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.loading.Patcher.Srg;

@SuppressWarnings("WeakerAccess")
@SideOnly(Side.CLIENT)
public class ClientDelegates {

  @Srg("func_175035_a")
  public static void renderModel(RenderItem renderItem, IBakedModel model, int color) { }

  @Srg("func_175045_a")
  public static void renderModel(RenderItem renderItem, IBakedModel model,
      int color, ItemStack item) { }

  @Srg("func_180451_a")
  public static void renderEffect(RenderItem renderItem, IBakedModel model) { }

  @Srg("func_180452_a")
  public static void setupGuiTransform(RenderItem renderItem, int xPosition, int yPosition, boolean isGui3d) { }

}
