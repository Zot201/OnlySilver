package zotmc.onlysilver.loading;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.logging.log4j.Logger;

public interface Patcher {

  TypePredicate targetType();

  byte[] patch(byte[] basicClass, Logger log, boolean devEnv) throws Throwable;


  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE, ElementType.FIELD})
  @interface ClientOnly { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  @interface Delegation { }

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.FIELD, ElementType.METHOD})
  @interface Hook {
    Strategy value() default Strategy.ARRIVAL;

    enum Strategy {
      ARRIVAL (false),
      RETURN (false),
      AGENT (true);

      final boolean consuming;
      Strategy(boolean consuming) {
        this.consuming = consuming;
      }
    }
  }

  @Target(ElementType.METHOD)
  @interface Name {
    String value();
  }

  @Target(ElementType.METHOD)
  @interface Srg {
    String value();
  }

  @Target(ElementType.METHOD)
  @interface Return {
    boolean condition();
  }

  @Target(ElementType.METHOD)
  @interface ReturnBoolean {
    boolean condition();
    boolean value();
  }

  @Target(ElementType.METHOD)
  @interface Static {
    Class<?> value();
  }

}
