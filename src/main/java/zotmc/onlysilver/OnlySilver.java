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
package zotmc.onlysilver;

import com.google.common.base.CaseFormat;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zotmc.onlysilver.config.AbstractConfig;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.data.ModData;
import zotmc.onlysilver.loading.OnlyLoading;
import zotmc.onlysilver.oregen.SilverGenerator;
import zotmc.onlysilver.util.Utils;

import javax.annotation.Nullable;

import static zotmc.onlysilver.data.ModData.OnlySilvers.*;
import static zotmc.onlysilver.util.Utils.isClientSide;

@Mod(modid = MODID, dependencies = DEPENDENCIES, guiFactory = GUI_FACTORY)
public enum OnlySilver {
  INSTANCE;
  
  public final EventBus eventBus = new EventBus();
  public final Logger log = LogManager.getFormatterLogger(MODID);
  final CommonProxy proxy = Utils.construct(!isClientSide() ? CommonProxy.class : ClientProxy.class);

  @Deprecated @InstanceFactory public static Object instance() { return INSTANCE; }
  
  
  @EventHandler public void construct(FMLConstructionEvent event) {
    Utils.checkRequirements(ModData.class);
    new OnlyLoading().validate();
  }
  
  @EventHandler public void preInit(FMLPreInitializationEvent event) {
    eventBus.register(proxy);
    MinecraftForge.EVENT_BUS.register(proxy);
    
    Config.init(MODID + "-config", eventBus, event.getSuggestedConfigurationFile());
    Contents.init();
    
    GameRegistry.registerWorldGenerator(new SilverGenerator(), 0xC22F0082);
  }
  
  @EventHandler public void init(FMLInitializationEvent event) {
    eventBus.post(new Init());
  }
  
  
  @EventHandler public void onServerStart(FMLServerAboutToStartEvent event) {
    eventBus.post(new AbstractConfig.NotifyServerStart());
  }
  
  @EventHandler public void onServerStop(FMLServerStoppingEvent event) {
    eventBus.post(new AbstractConfig.NotifyServerStop());
  }
  
  @EventHandler public void onMappingMiss(FMLMissingMappingsEvent event) {
    for (MissingMapping missing : event.get()) {
      switch (missing.type) {
      case ITEM:
        Item i = retrieve(Item.REGISTRY, missing.name);
        if (i != null) missing.remap(i);
        else log.error("Unable to remap item instance for %s", missing.name);
        break;
        
      case BLOCK:
        Block b = retrieve(Block.REGISTRY, missing.name);
        if (b != null) missing.remap(b);
        else log.error("Unable to remap block instance for %s", missing.name);
        break;
        
      default:
        log.error("Unknown missing mapping type %s", missing.type);
      }
    }
  }
  
  private @Nullable <I extends IForgeRegistryEntry<I>> I retrieve(
      RegistryNamespaced<ResourceLocation, I> registry, String oldId) {
    String camel = oldId.substring(oldId.indexOf(':') + 1), underscore = Contents.renameMap.get(camel);
    if (underscore == null) underscore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel);
    return registry.getObject(new ResourceLocation(MODID, underscore));
  }
  
  
  public static class Init extends Event { }
  
}
