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
