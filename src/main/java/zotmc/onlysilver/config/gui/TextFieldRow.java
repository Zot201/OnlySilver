package zotmc.onlysilver.config.gui;

import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class TextFieldRow extends WidgetRow implements TextField.Handler {

  private MutableBoolean isFocus = new MutableBoolean(); // go first or have NPE
  private final TextField textField = new TextField(this);

  // widget
  @Override protected Widget<?> widget() {
    return textField;
  }

  @Override protected int widgetPos(int k) {
    return k * 7 / 15;
  }

  // focus
  @Override public boolean getIsFocus() {
    return isFocus.booleanValue();
  }

  @Override public void setIsFocus(MutableBoolean isFocus) {
    this.isFocus = isFocus;
  }

}
