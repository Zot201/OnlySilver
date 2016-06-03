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

import org.apache.commons.lang3.mutable.MutableBoolean;

public enum EmptyRow implements Row {
  INSTANCE;

  @Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) { }

  @Override public boolean clickRow(int mouseX, int mouseY) { return false; }

  @Override public void releaseRow(int mouseX, int mouseY) { }

  @Override public void keyTyped(char typedChar, int keyCode) { }

  @Override public void setIsFocus(MutableBoolean isFocus) { }

  @Override public boolean folded() { return false; }

}
