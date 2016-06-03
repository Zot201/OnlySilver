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

public interface Row {

  void drawRow(int x, int y, int w, int h, int mouseX, int mouseY);

  boolean clickRow(int mouseX, int mouseY);

  void releaseRow(int mouseX, int mouseY);

  void keyTyped(char typedChar, int keyCode);

  void setIsFocus(MutableBoolean isFocus);

  boolean folded();

}
