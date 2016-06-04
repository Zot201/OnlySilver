package zotmc.onlysilver;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Semaphore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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

import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Queues;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

  private final Set<Class<? extends Entity>> entityRenderers = Utils.newIdentityHashSet();
  private final ListMultimap<Item, String> itemModels = Utils.newArrayListMultimap(Maps.newIdentityHashMap());

  private final Semaphore canSpawn = new Semaphore(0, true);
  private final Queue<Entity> spawnQueue = Queues.newConcurrentLinkedQueue();

  private ClientProxy() { }

  @Override public void registerEntityRenderer(Class<? extends Entity> entity) {
    checkArgument(entity.getAnnotation(EntityRenderer.class) != null);
    entityRenderers.add(entity);
  }

  @Override public void registerItemModels(Item i, String... models) {
    List<String> list = itemModels.get(i);
    for (String s : models)
      list.add(s.indexOf(':') != -1 ? s : OnlySilvers.MODID + ":" + s);
  }

  @Override public void spawnEntityInWorld(Entity entity) {
    spawnQueue.add(entity);
  }


  @SubscribeEvent public void init(OnlySilver.Init event) {
    RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
    for (Class<? extends Entity> entity : entityRenderers) {
      Render render = Dynamic.construct(entity.getAnnotation(EntityRenderer.class).value())
          .via(RenderManager.class, renderManager)
          .get();
      RenderingRegistry.registerEntityRenderingHandler(entity, render);
    }

    ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
    for (Map.Entry<Item, List<String>> entry : Multimaps.asMap(itemModels).entrySet()) {
      for (int i = 0; i < entry.getValue().size(); i++)
        itemModelMesher.register(entry.getKey(), i, new ModelResourceLocation(entry.getValue().get(i), "inventory"));
      ModelBakery.addVariantName(entry.getKey(), Iterables.toArray(entry.getValue(), String.class));
    }
  }

  @SubscribeEvent public void onFOVUpdate(FOVUpdateEvent event) {
    EntityPlayer player = event.entity;
    if (player.isUsingItem() && player.getItemInUse().getItem() == ItemFeature.silverBow.get())
      event.newfov = getFOVMultiplier(player);
  }

  private float getFOVMultiplier(EntityPlayer player) {
    float f = player.getItemInUseDuration() / 20.0F;
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
