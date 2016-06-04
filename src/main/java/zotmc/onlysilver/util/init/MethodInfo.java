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
package zotmc.onlysilver.util.init;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class MethodInfo extends Method {

  private MethodInfo(String name, String desc) {
    super(name, desc);
  }

  public static MethodInfo of(String name, String desc) {
    return new MethodInfo(name, desc);
  }
  public static MethodInfo of(String name, Type returnType, Type... argumentTypes) {
    return new MethodInfo(name, Type.getMethodDescriptor(returnType, argumentTypes));
  }
  public static MethodInfo of(Method method) {
    return new MethodInfo(method.getName(), method.getDescriptor());
  }
  public static MethodInfo of(java.lang.reflect.Method method) {
    return new MethodInfo(method.getName(), Type.getMethodDescriptor(method));
  }

}
