package zotmc.onlysilver.util;

import com.google.common.base.Supplier;

public interface Feature<T> extends Supplier<T> {

  public boolean exists();

}
