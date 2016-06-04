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

import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

@SuppressWarnings("WeakerAccess")
public class MappedTransformer implements IClassTransformer {

  private static final Set<String> transformedTypes = Sets.newConcurrentHashSet();
  private final Logger log = LogManager.getFormatterLogger(OnlyLoading.MODID);
  private final boolean devEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
  private final ListMultimap<String, Patcher> patchers;

  public MappedTransformer() {
    this.patchers = LinkedListMultimap.create();
    for (Patcher patcher : new OnlyLoading().getPatchers())
      this.patchers.put(patcher.targetType().toString().replace('/', '.'), patcher);
  }

  @Override public byte[] transform(String name, String transformedName, byte[] basicClass) {
    for (Patcher p : patchers.get(transformedName))
      try {
        basicClass = p.patch(basicClass, log, devEnv);
        transformedTypes.add(transformedName);

      } catch (Throwable t) {
        throw Throwables.propagate(t);
      }

    return basicClass;
  }

  Set<String> transformAll() {
    for (String s : patchers.keySet())
      try {
        Class.forName(s);
      } catch (ClassNotFoundException e) {
        throw Throwables.propagate(e);
      }

    return Sets.difference(patchers.keySet(), transformedTypes);
  }

}
