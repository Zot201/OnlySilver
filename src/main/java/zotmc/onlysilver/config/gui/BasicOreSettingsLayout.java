package zotmc.onlysilver.config.gui;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

import zotmc.onlysilver.config.gui.Slider.Slidable;
import zotmc.onlysilver.data.LangData;
import zotmc.onlysilver.util.Utils.Localization;

import com.google.common.collect.ImmutableList;

public abstract class BasicOreSettingsLayout implements Iterable<Row> {

  protected abstract MutableInt size();

  protected abstract MutableInt count();

  protected abstract MutableInt minHeight();

  protected abstract MutableInt maxHeight();

  @Override public Iterator<Row> iterator() {
    return rows.iterator();
  }

  private final List<Row> rows = ImmutableList.<Row>of(
      new PairSliderRow(
          new LabeledIntSlidable() {
            @Override Localization localization() {
              return LangData.SIZE;
            }
            @Override MutableInt value() {
              return size();
            }
            @Override int min() {
              return 1;
            }
            @Override int max() {
              return 50;
            }
          },
          new LabeledIntSlidable() {
            @Override Localization localization() {
              return LangData.COUNT;
            }
            @Override MutableInt value() {
              return count();
            }
            @Override int min() {
              return 0;
            }
            @Override int max() {
              return 40;
            }
          }
      ),
      new PairSliderRow(
          new LabeledIntSlidable() {
            @Override Localization localization() {
              return LangData.MIN_HEIGHT;
            }
            @Override MutableInt value() {
              return minHeight();
            }
            @Override int min() {
              return 0;
            }
            @Override int max() {
              return 255;
            }
          },
          new LabeledIntSlidable() {
            @Override Localization localization() {
              return LangData.MAX_HEIGHT;
            }
            @Override MutableInt value() {
              return maxHeight();
            }
            @Override int min() {
              return 0;
            }
            @Override int max() {
              return 255;
            }
          }
      )
  );


  private static abstract class LabeledIntSlidable implements Slidable {
    abstract Localization localization();

    abstract MutableInt value();

    abstract int min();

    abstract int max();

    @Override public String getText() {
      return localization() + ": " + value();
    }
    @Override public double getPosition() {
      int min = min();
      return Math.max(0, Math.min(1, (value().intValue() - min) / (double) (max() - min)));
    }
    @Override public void setPosition(double position) {
      int min = min();
      value().setValue(min + position * (max() - min));
    }
    @Override public void next() {
      MutableInt v = value();
      v.setValue(Math.min(max(), v.intValue() + 1));
    }
    @Override public void previous() {
      MutableInt v = value();
      v.setValue(Math.max(min(), v.intValue() - 1));
    }
  }

  private static class PairSliderRow implements Row {
    private final Slider left, right;

    public PairSliderRow(Slidable left, Slidable right) {
      this.left = new Slider(left);
      this.right = new Slider(right);
    }

    @Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) {
      int w1 = w / 2 - 4, y1 = y - 1, h1 = h + 3;

      left.setLeftTop(x, y1)
        .setWidthHeight(w1, h1)
        .draw(mouseX, mouseY);

      right.setLeftTop(x + w - w1, y1)
        .setWidthHeight(w1, h1)
        .draw(mouseX, mouseY);
    }

    @Override public boolean clickRow(int mouseX, int mouseY) {
      return left.click(mouseX, mouseY) || right.click(mouseX, mouseY);
    }

    @Override public void releaseRow(int mouseX, int mouseY) {
      left.release(mouseX, mouseY);
      right.release(mouseX, mouseY);
    }

    @Override public void keyTyped(char typedChar, int keyCode) { }

    @Override public void setIsFocus(MutableBoolean isFocus) { }

    @Override public boolean folded() { return false; }
  }

}
