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

import com.google.common.collect.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.util.Dynamic;
import zotmc.onlysilver.util.Utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import static com.google.common.base.Preconditions.checkArgument;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  private final ListMultimap<Item, ModelResourceLocation> itemModels =
      Utils.newArrayListMultimap(Maps.newIdentityHashMap());

  private final Semaphore canSpawn = new Semaphore(0, true);
  private final Queue<Entity> spawnQueue = Queues.newConcurrentLinkedQueue();

  private ClientProxy() { }

  @Override public <T extends Entity> void registerEntityRenderer(Class<T> entity) {
    EntityRenderer annotation = entity.getAnnotation(EntityRenderer.class);
    checkArgument(annotation != null);
    //noinspection unchecked
    Class<Render<? super T>> render = (Class<Render<? super T>>) annotation.value();

    RenderingRegistry.registerEntityRenderingHandler(entity, m -> Dynamic
        .construct(render)
        .via(RenderManager.class, m)
        .get());
  }

  @Override public void registerItemModels(Item i, String... models) {
    List<ModelResourceLocation> list = itemModels.get(i);
    for (String s : models) {
      list.add(new ModelResourceLocation(s.indexOf(':') != -1 ? s : OnlySilvers.MODID + ":" + s, "inventory"));
    }
  }

  @Override public void spawnEntityInWorld(Entity entity) {
    spawnQueue.add(entity);
  }


  @SubscribeEvent public void init(OnlySilver.Init event) {
    ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    for (Entry<Item, List<ModelResourceLocation>> entry : Multimaps.asMap(itemModels).entrySet()) {
      for (int i = 0; i < entry.getValue().size(); i++) {
        itemModelMesher.register(entry.getKey(), i, entry.getValue().get(i));
      }
      ModelBakery.registerItemVariants(entry.getKey(), Iterables.toArray(entry.getValue(), ResourceLocation.class));
    }
  }

  @SubscribeEvent public void onFOVUpdate(FOVUpdateEvent event) {
    if (ItemFeature.silverBow.exists()) {
      EntityPlayer player = event.getEntity();
      if (player.isHandActive()) {
        ItemStack active = player.getActiveItemStack();
        if (active != null && active.getItem() == ItemFeature.silverBow.get()) {
          event.setNewfov(getFOVMultiplier(player));
        }
      }
    }
  }

  private float getFOVMultiplier(EntityPlayer player) {
    float f = player.getItemInUseCount() / 20.0F;
    if (f > 1) f = 1;
    else f *= f;
    return 1 - f * 0.15F;
  }

  @SubscribeEvent public void onServerTick(ServerTickEvent event) {
    if (event.phase == Phase.END && !spawnQueue.isEmpty()) {
      canSpawn.acquireUninterruptibly();

      do {
        Entity entity = spawnQueue.remove();
        entity.worldObj.spawnEntityInWorld(entity);
      } while (!spawnQueue.isEmpty());

      canSpawn.release();
    }
  }

  @SubscribeEvent public void onRenderTick(RenderTickEvent event) {
    Semaphore s = canSpawn;
    s.release();
    s.acquireUninterruptibly();
  }


  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface EntityRenderer {
    Class<? extends Render> value();
  }

}
