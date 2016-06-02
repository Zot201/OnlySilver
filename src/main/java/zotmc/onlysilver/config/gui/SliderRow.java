package zotmc.onlysilver.config.gui;

import zotmc.onlysilver.config.gui.Slider.Slidable;

public abstract class SliderRow extends WidgetRow implements Slidable {

  private final Slider slider = new Slider(this);

  @Override protected Widget<?> widget() {
    return slider;
  }

  @Override protected int widgetPos(int k) {
    return k * 11 / 21;
  }

}
