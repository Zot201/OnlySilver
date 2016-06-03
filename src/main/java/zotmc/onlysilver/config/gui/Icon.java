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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import org.apache.commons.lang3.mutable.Mutable;

import com.google.common.base.Supplier;
import com.google.common.primitives.Floats;

@SuppressWarnings("WeakerAccess")
public abstract class Icon<T extends Icon<T>> {

  public static final float PHI_M1 = 0.618034f;
  protected int color = 0xFFFFFFFF;

  public final void drawIcon(int x, int y) {
    drawIcon(x, y, 0);
  }

  protected abstract void drawIcon(int x, int y, int z);

  @SuppressWarnings("unchecked")
  public T setAlpha(int alpha) {
    color = alpha << 24 | color & 0xFFFFFF;
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  public T setBrightness(float brightness) {
    int i = toInt(brightness);
    this.color = color & 0xFF000000 | i << 16 | i << 8 | i;
    return (T) this;
  }

  private static int toInt(float a) {
    return !Floats.isFinite(a) ? 0 : (int) Math.rint(255.0f * Math.max(0, Math.min(1, a)));
  }

  protected boolean common() { return true; }

  public Icon<?> overlay(Icon<?> icon) {
    return new Overlay(this, icon);
  }

  public Icon<?> overlay(Item i) {
    return overlay(new ItemIcon(i));
  }
  public Icon<?> overlay(Block b) {
    return overlay(new ItemIcon(b));
  }

  public CategoryRow categoryRow(final Supplier<String> text) {
    return CategoryRow.create(this, text);
  }

  public Element title(final Supplier<String> text, int x, int y) {
    return new Title(this, text, x, y);
  }

  public IconButton iconButton(final Mutable<Boolean> state,
      final Supplier<List<String>> texts, final Holder<List<String>> textHolder) {
    return new IconButton(new IconButton.Handler() {
      @Override public Icon<?> icon() {
        return Icon.this;
      }
      @Override public boolean isCommon() {
        return Icon.this.common();
      }
      @Override public boolean getState() {
        return state.getValue();
      }
      @Override public void toggleState() {
        state.setValue(!state.getValue());
      }
      @Override public Holder<List<String>> hoveringTextHolder() {
        return textHolder;
      }
      @Override public List<String> getHoveringTexts() {
        return texts.get();
      }
    });
  }

  private static class Overlay extends Icon<Overlay> {
    private final Icon<?> top, bottom;
    public Overlay(Icon<?> top, Icon<?> bottom) {
      this.top = top;
      this.bottom = bottom;
    }
    @Override protected void drawIcon(int x, int y, int z) {
      bottom.drawIcon(x, y, z);
      top.drawIcon(x, y, z + 50);
    }
    @Override public Overlay setAlpha(int alpha) {
      bottom.setAlpha(alpha);
      top.setAlpha(alpha);
      return this;
    }
    @Override public Overlay setBrightness(float brightness) {
      bottom.setBrightness(brightness);
      top.setBrightness(brightness);
      return this;
    }
  }

}