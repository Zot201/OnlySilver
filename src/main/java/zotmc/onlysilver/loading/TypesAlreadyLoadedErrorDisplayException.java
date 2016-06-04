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
package zotmc.onlysilver.loading;

import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class TypesAlreadyLoadedErrorDisplayException extends CustomModLoadingErrorDisplayException {

  private static final long serialVersionUID = -5430273970008550555L;
  private final Set<String> erred;
  private final List<String> msg;

  public TypesAlreadyLoadedErrorDisplayException(IllegalStateException cause, Set<String> erred, List<String> msg) {
    super(null, cause);
    this.erred = erred;
    this.msg = msg;
  }

  @Override public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) { }

  @Override public void drawScreen(GuiErrorScreen errorScreen,
      FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {

    int center = errorScreen.width / 2;

    int offset = Math.max(10, 105 - (msg.size() + erred.size()) * 10);
    for (String m : msg) {
      offset += 10;
      errorScreen.drawCenteredString(fontRenderer, m, center, offset, 0xFFFFFF);
    }

    offset += 5;
    for (String m : erred) {
      offset += 10;
      errorScreen.drawCenteredString(fontRenderer, m, center, offset, 0xEEEEEE);
    }
  }

}
