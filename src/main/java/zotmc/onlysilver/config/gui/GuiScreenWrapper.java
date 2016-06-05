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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class GuiScreenWrapper extends GuiScreen {

  private final GuiScreen parent; // nullable
  private final Holder<List<String>> hoveringText = Holder.absent();
  private Screen screen;
  private Iterable<Element> elements;
  private GuiEmbeddedList embeddedList;

  private GuiScreenWrapper(@Nullable GuiScreen parent, Screen screen) {
    this.parent = parent;
    this.screen = checkNotNull(screen);
    screen.create();
  }

  GuiScreenWrapper(GuiScreen parent) {
    this.parent = parent;
    this.screen = checkNotNull(createScreen());
    screen.create();
  }

  @Nullable Screen createScreen() {
    return null;
  }

  public static void display(Screen screen) {
    Minecraft mc = Minecraft.getMinecraft();
    mc.displayGuiScreen(new GuiScreenWrapper(mc.currentScreen, screen));
  }

  @Override public void initGui() {
    Keyboard.enableRepeatEvents(true);
    elements = screen.getElements(width, height, hoveringText, () -> {
      screen.destroy();
      mc.displayGuiScreen(parent);
    });
    embeddedList = new GuiEmbeddedList(this, screen.getRowHeight(), screen.getRows(width, hoveringText));
  }

  @Override public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }

  @Override public void drawScreen(int mouseX, int mouseY, float tickFrac) {
    drawDefaultBackground();
    hoveringText.clear();
    embeddedList.drawScreen(mouseX, mouseY, tickFrac);
    for (Element e : elements)
      e.draw(mouseX, mouseY);
    super.drawScreen(mouseX, mouseY, tickFrac);
    if (hoveringText.isPresent()) drawHoveringText(hoveringText.get(), mouseX, mouseY);
  }

  @Override public void handleMouseInput() throws IOException {
    embeddedList.handleMouseInput();
    super.handleMouseInput();
  }

  @Override protected void keyTyped(char typedChar, int keyCode) throws IOException {
    embeddedList.keyTyped(typedChar, keyCode);
  }

  @Override protected void mouseClicked(int x, int y, int mouseEvent) throws IOException {
    if (!embeddedList.mouseClicked(x, y, mouseEvent) && mouseEvent == 0)
      for (Element e : elements)
        if (e.click(x, y)) return;
  }

  @Override protected void mouseReleased(int x, int y, int mouseEvent) {
    if (!embeddedList.mouseReleased(x, y, mouseEvent) && mouseEvent == 0)
      for (Element e : elements)
        e.release(x, y);
  }

}
