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
package zotmc.onlysilver.data;

import com.google.common.base.Supplier;
import com.google.common.collect.Multiset;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Dynamic.Chain;
import zotmc.onlysilver.util.Utils;
import zotmc.onlysilver.util.Utils.Dependency;
import zotmc.onlysilver.util.Utils.Modid;
import zotmc.onlysilver.util.Utils.Requirements;

@MCVersion(Loader.MC_VERSION)
public class ModData {
  
  public static class OnlySilvers {
    public static final String
    MODID = "onlysilver",
    DEPENDENCIES = "after:weaponmod",
    GUI_FACTORY = "zotmc.onlysilver.config.ConfigGui";
  }
  
  
  @Dependency
  @Requirements("1.9.4 = 12.17.0.1937")
  public static class Forge {
    @Modid public static final String MODID = "Forge";
  }
  
  public static class MoCreatures {
    @Modid public static final String MODID = "mocreatures";
    
    public static final String
    MOC_ENTITY_WEREWOLF = "drzhark.mocreatures.entity.monster.MoCEntityWerewolf",
    GET_IS_HUMAN_FORM = "getIsHumanForm";
  }
  
  public static class Thaumcraft {
    @Modid public static final String MODID = "Thaumcraft";
    
    private static final String
    THAUMCRAFT_API = "thaumcraft.api.ThaumcraftApi",
    ASPECT_LIST = "thaumcraft.api.aspects.AspectList",
    ASPECT = "thaumcraft.api.aspects.Aspect";
    
    public enum Aspect {
      METAL, GREED, EARTH;
      
      @SuppressWarnings("Guava")
      private static Supplier<?> adapt(Multiset<Aspect> aspects) {
        Chain<?> ret = Dynamic.construct(ASPECT_LIST);
        for (Multiset.Entry<Aspect> entry : aspects.entrySet())
          ret = ret.invoke("add")
            .via(ASPECT, Dynamic.refer(ASPECT, entry.getElement().name()))
            .viaInt(entry.getCount());
        return ret;
      }
    }
    
    public static void registerEntityTag(Class<? extends Entity> entity, Multiset<Aspect> aspects) {
      Dynamic.<Void>invoke(THAUMCRAFT_API, "registerEntityTag")
          .via(Utils.getEntityString(entity))
          .via(ASPECT_LIST, Aspect.adapt(aspects))
          .via(Utils.newArray(THAUMCRAFT_API + "$EntityTagsNBT", 0))
          .get();
    }
    
    public static void registerObjectTag(ItemStack item, Multiset<Aspect> aspects) {
      Dynamic.<Void>invoke(THAUMCRAFT_API, "registerObjectTag")
          .via(ItemStack.class, item)
          .via(ASPECT_LIST, Aspect.adapt(aspects))
          .get();
    }
  }

}
