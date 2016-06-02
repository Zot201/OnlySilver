package zotmc.onlysilver.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.Field;

import net.minecraftforge.common.util.EnumHelper;

public class Fields {

  @SuppressWarnings("unchecked")
  public static <T> T get(Object obj, Field field) {
    try {
      return (T) field.get(obj);
    } catch (Throwable t) {
      throw Utils.propagate(t);
    }
  }

  public static <T> void set(Object obj, Field field, T value) {
    try {
      field.set(obj, value);
    } catch (Throwable t) {
      throw Utils.propagate(t);
    }
  }

  public static <T> void setFinal(Object obj, Field field, T value) {
    try {
      checkArgument(field.isAccessible());
      EnumHelper.setFailsafeFieldValue(field, obj, value);
    } catch (Throwable t) {
      throw Utils.propagate(t);
    }
  }

}
