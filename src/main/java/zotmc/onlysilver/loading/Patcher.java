package zotmc.onlysilver.loading;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.logging.log4j.Logger;

public interface Patcher {

  public TypePredicate targetType();

  public byte[] patch(byte[] basicClass, Logger log, boolean devEnv) throws Throwable;


  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.FIELD})
  public @interface ClientOnly { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  public @interface Delegation { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.METHOD})
  public @interface Hook {
    public Strategy value() default Strategy.ARRIVAL;

    public enum Strategy {
      ARRIVAL (false),
      RETURN (false),
      AGENT (true);

      final boolean consuming;
      private Strategy(boolean consuming) {
        this.consuming = consuming;
      }
    }
  }

  @Target(ElementType.METHOD)
  public @interface Name {
    public String value();
  }

  @Target(ElementType.METHOD)
  public @interface Srg {
    public String value();
  }

  @Target(ElementType.METHOD)
  public @interface Return {
    public boolean condition();
  }

  @Target(ElementType.METHOD)
  public @interface ReturnBoolean {
    public boolean condition();
    public boolean value();
  }

  @Target(ElementType.METHOD)
  public @interface Static {
    public Class<?> value();
  }

}
