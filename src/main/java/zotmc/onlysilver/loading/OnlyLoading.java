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

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.apache.logging.log4j.LogManager;
import zotmc.onlysilver.data.ClientAsm;
import zotmc.onlysilver.data.CommonAsm;
import zotmc.onlysilver.data.OreGenAsm;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

@SortingIndex(0x47488C7A)
public class OnlyLoading implements IFMLLoadingPlugin, IFMLCallHook {

  static final String MODID = CommonAsm.MODID;
  private static LaunchClassLoader classLoader;

  @Override public String getSetupClass() {
    return getClass().getName();
  }

  @Override public void injectData(Map<String, Object> data) {
    Object cl = data.get("classLoader");
    if (cl != null && classLoader == null) {
      classLoader = (LaunchClassLoader) cl;
      String s = MappedTransformer.class.getName();
      LogManager.getFormatterLogger(MODID).trace("Registering transformer %s", s);
      classLoader.registerTransformer(s);
    }
  }

  Iterable<Patcher> getPatchers() {
    List<Iterable<Patcher>> l = Lists.newArrayList();
    for (Class<?> clz : new Class<?>[] {CommonAsm.class, ClientAsm.class, OreGenAsm.class}) {
      l.add(new ExplicitPatchLoader(clz));
      l.add(new MethodHookGenerator(clz, classLoader));
      l.add(new DelegationGenerator(clz, classLoader));
    }
    return Iterables.concat(l);
  }

  public void validate() {
    ClassLoader cl = getClass().getClassLoader();

    if (cl instanceof LaunchClassLoader) {
      checkState(classLoader != null, "Failed loading core mod");

      if (cl == classLoader) {
        Set<String> erred = new MappedTransformer().transformAll();

        if (!erred.isEmpty()) {
          List<String> msg = ImmutableList.of(
              "Found type(s) being loaded before they can be transformed.",
              "Loading cannot be proceeded without causing in-game errors.",
              "Please report to core mod author(s).",
              "",
              "Type(s) affected:"
          );
          IllegalStateException exception = new IllegalStateException(Joiner.on(" ").join(msg) + " " + erred);

          if (FMLLaunchHandler.side().isServer()) throw exception;
          try {
            // avoid directly calling new of a side only class.
            Constructor<? extends RuntimeException> ctor = TypesAlreadyLoadedErrorDisplayException.class
                .getConstructor(IllegalStateException.class, Set.class, List.class);
            throw ctor.newInstance(exception, erred, msg);

          } catch (Throwable t) {
            throw Throwables.propagate(t);
          }
        }

        LogManager.getFormatterLogger(MODID).trace("Transformations validated");
      }
    }
  }

  @Override public String getAccessTransformerClass() {  return null; }

  @Override public String[] getASMTransformerClass() { return null; }

  @Override public Void call() { return null; }

  @Override public String getModContainerClass() { return null; }

}
