package zotmc.onlysilver.config.gui;

import zotmc.onlysilver.config.AbstractConfig;
import zotmc.onlysilver.config.AbstractConfig.Property;
import zotmc.onlysilver.data.LangData;
import zotmc.onlysilver.util.Utils.Localization;

import com.google.common.base.Supplier;

public abstract class AbstractBooleanRow<T extends AbstractConfig<T>> extends WidgetRow {

  private final ButtonComponent comp = new ButtonComponent();

  @Override protected Widget<?> widget() {
    return comp.button;
  }

  protected abstract T getTemp();

  protected abstract Property<Boolean> toProperty(T t);

  protected abstract Boolean getRawValue(Property<Boolean> p);

  protected abstract void setRawValue(Property<Boolean> p, Boolean v);


  private class ButtonComponent implements Runnable, Supplier<String> {
    final Button button = new Button(this, this);

    @Override public void run() {
      Property<Boolean> p = toProperty(getTemp());
      setRawValue(p, State.of(getRawValue(p)).next().value);
    }
    @Override public String get() {
      Property<Boolean> p = toProperty(getTemp());
      return State.getLocalized(getRawValue(p), p);
    }
  }

  private enum State {
    DEFAULT (null, LangData.DEFAULT),
    ON (true, LangData.ON),
    OFF (false, LangData.OFF);

    final Boolean value;
    private final Localization localization;
    private State(Boolean value, Localization localization) {
      this.value = value;
      this.localization = localization;
    }
    static State of(Boolean value) {
      return value == null ? DEFAULT : value ? ON : OFF;
    }
    State next() {
      return values()[(ordinal() + 1) % values().length];
    }

    public static String getLocalized(Boolean raw, Property<Boolean> p) {
      return raw != null ? (raw ? ON : OFF).localization.toString()
          : DEFAULT.localization.toString((p.get() ? ON : OFF).localization);
    }
  }

}
