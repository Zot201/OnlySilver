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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.minecraft.client.gui.GuiSlot;
import org.apache.commons.lang3.mutable.MutableBoolean;

class GuiEmbeddedList extends GuiSlot {

  private final GuiScreenWrapper mainScreen;
  private final PseudoIterator<Row> rows;
  private MutableBoolean isFocus = new MutableBoolean();

  GuiEmbeddedList(GuiScreenWrapper mainScreen, int rowHeight, Iterable<Row> entries) {
    super(mainScreen.mc, mainScreen.width, mainScreen.height, 33, mainScreen.height - 32, rowHeight);
    setShowSelectionBox(false);
    this.mainScreen = mainScreen;
    this.rows = PseudoIterator.of(Iterables.filter(entries, NotFolded.INSTANCE), EmptyRow.INSTANCE, 1);
  }

  @Override protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) { }

  @Override protected boolean isSelected(int slotIndex) { return false; }

  @Override protected void drawBackground() { }

  @Override protected int getSize() {
    return rows.size();
  }

  static int getMarginWidth(int width) {
    return Math.max(0, Math.min(38, width - 388));
  }

  @Override protected int getScrollBarX() {
    return width - getMarginWidth(width) - 6;
  }

  @Override public int getListWidth() {
    return mainScreen.width;
  }

  @Override public int getSlotIndexFromScreenCoords(int x, int y) {
    return super.getSlotIndexFromScreenCoords(x, y - 4);
  }

  @Override protected void drawSlot(int index, int unused, int y, int slotHeight, int mouseX, int mouseY) {
    int s = getMarginWidth(width) + 33;
    rows.next(index).drawRow(s, y + 6, getListWidth() - 2 * s, slotHeight, mouseX, mouseY);
  }

  public void keyTyped(char typedChar, int keyCode) {
    for (Row r : rows)
      r.keyTyped(typedChar, keyCode);
  }

  boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
    if (isMouseYWithinSlotBounds(mouseY)) {
      int s = getScrollBarX();

      if (mouseX < s || mouseX > s + 6 || getMaxScroll() <= 0) {
        isFocus.setValue(false);

        if (mouseEvent == 0) {
          int index = getSlotIndexFromScreenCoords(mouseX, mouseY);

          if (index >= 0) {
            Row row = rows.next(index);

            if (row.clickRow(mouseX, mouseY)) {
              row.setIsFocus(isFocus = new MutableBoolean(true));
              setEnabled(false);
              return true;
            }
          }
        }
      }
    }

    return false;
  }

  boolean mouseReleased(int mouseX, int mouseY, int mouseEvent) {
    if (mouseEvent == 0)
      for (Row r : rows)
        r.releaseRow(mouseX, mouseY);

    setEnabled(true);
    return false;
  }


  private enum NotFolded implements Predicate<Row> {
    INSTANCE;
    @Override public boolean apply(@SuppressWarnings("NullableProblems") Row input) {
      return !input.folded();
    }
  }

}
