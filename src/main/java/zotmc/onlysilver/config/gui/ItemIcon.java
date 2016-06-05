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
package zotmc.onlysilver.config.gui;

import com.google.common.base.Supplier;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.logging.log4j.Logger;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.util.ClientUtils;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ItemIcon extends Icon<ItemIcon> implements Supplier<String> {

  private static final Logger log = OnlySilver.INSTANCE.log;
  private final ItemStack item;
  private final float f;
  private boolean renderEffect = true;
  private boolean isItemValid = true;

  public ItemIcon(Block b) {
    this(b, 1);
  }
  public ItemIcon(Item i) {
    this(i, 1);
  }
  public ItemIcon(ItemStack item) {
    this(item, 1);
  }

  public ItemIcon(Block b, float f) {
    this(new ItemStack(b), f);
  }
  public ItemIcon(Item i, float f) {
    this(new ItemStack(i), f);
  }
  public ItemIcon(ItemStack item, float f) {
    checkNotNull(item.getItem());
    this.item = item;
    this.f = f;
  }

  public ItemIcon setRenderEffect(boolean renderEffect) {
    this.renderEffect = renderEffect;
    return this;
  }

  @Override protected boolean common() {
    return item.getRarity() == EnumRarity.COMMON;
  }

  @Override public void drawIcon(int x, int y, int z) {
    if (isItemValid) {
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      GlStateManager.enableLighting();
      GlStateManager.pushMatrix();

      {
        GlStateManager.translate(x + 18, y + 18, 0);
        GlStateManager.scale(f, f, 1);

        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        float original = ri.zLevel;
        ri.zLevel = z;

        try {
          ClientUtils.renderItemIntoGUI(item, -18, -18, color, renderEffect);

        } catch (Throwable t) {
          log.catching(t);
          isItemValid = false;

        } finally {
          ri.zLevel = original;
        }
      }

      GlStateManager.popMatrix();
      GlStateManager.disableLighting();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
    }
  }

  @Override public String get() {
    return item.getDisplayName();
  }

  public IconButton iconButton(Mutable<Boolean> state, Holder<List<String>> textHolder) {
    return iconButton(state, new ItemHoveringTexts(item), textHolder);
  }

  public static List<String> colorTooltip(List<String> tooltips, TextFormatting rarityColor) {
    int n = tooltips.size();
    if (n > 0) {
      tooltips.set(0, rarityColor + tooltips.get(0));
      for (int i = 1; i < n; i++)
        tooltips.set(i, ChatFormatting.GRAY + tooltips.get(i));
    }
    return tooltips;
  }


  private static class ItemHoveringTexts implements Supplier<List<String>> {
    private final ItemStack item;

    public ItemHoveringTexts(ItemStack item) {
      checkNotNull(item.getItem());
      this.item = item;
    }

    @Override public List<String> get() {
      Minecraft mc = Minecraft.getMinecraft();
      // TODO: Player cannot be null
      List<String> ret = item.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
      return colorTooltip(ret, item.getRarity().rarityColor);
    }
  }

}