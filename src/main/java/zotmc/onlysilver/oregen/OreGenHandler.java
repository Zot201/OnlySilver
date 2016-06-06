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
package zotmc.onlysilver.oregen;

import com.google.common.base.Objects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.gson.*;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiCustomizeWorldScreen;
import net.minecraft.client.gui.GuiPageButtonList.GuiLabelEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiListEntry;
import net.minecraft.client.gui.GuiPageButtonList.GuiSlideEntry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.ChunkProviderSettings.Factory;
import net.minecraft.world.gen.ChunkProviderSettings.Serializer;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.config.Config;
import zotmc.onlysilver.config.GenDefaults;
import zotmc.onlysilver.data.LangData;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.data.ReflData;
import zotmc.onlysilver.loading.Patcher.*;
import zotmc.onlysilver.loading.Patcher.Hook.Strategy;
import zotmc.onlysilver.util.Fields;
import zotmc.onlysilver.util.JsonHelper;
import zotmc.onlysilver.util.Utils;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

//TODO: Forge PR to eliminate needs for low level hooks
@SuppressWarnings("WeakerAccess")
public class OreGenHandler extends CacheLoader<WorldInfo, ExtSettings> {

  public static final OreGenHandler INSTANCE = new OreGenHandler();
  private final Map<Factory, JsonHelper> factories = new MapMaker().weakKeys().makeMap();
  private final LoadingCache<WorldInfo, ExtSettings> settings = CacheBuilder.newBuilder().weakKeys().build(this);

  public ExtSettings loadExtSettings(World world) {
    return settings.getUnchecked(world.getWorldInfo());
  }

  @Deprecated @Override public ExtSettings load(@Nullable WorldInfo worldInfo) {
    checkNotNull(worldInfo);

    if (worldInfo instanceof DerivedWorldInfo) {
      //noinspection ConstantConditions
      return settings.getUnchecked(Fields.get(worldInfo, ReflData.THE_WORLD_INFO));
    }

    try {
      String silverGen = ((NBTTagString) getSilverGen(worldInfo)).getString();
      JsonObject j = new Gson().fromJson(silverGen, JsonObject.class);
      return new ExtSettings(new JsonHelper(j));

    } catch (Throwable t) {
      OnlySilver.INSTANCE.log.catching(t);
    }

    OnlySilver.INSTANCE.log.warn("Silver generation property is missing or corrupted");
    JsonHelper ext = null;
    String generatorOptions = worldInfo.getGeneratorOptions();

    //noinspection ConstantConditions
    if (generatorOptions != null && !generatorOptions.isEmpty()) {
      Factory factory = Factory.jsonToFactory(generatorOptions);
      ext = INSTANCE.factories.get(factory);
    }
    if (ext == null) ext = Config.current().silverGenDefaults.base().get().get();

    return new ExtSettings(ext);
  }

  private static NBTBase getSilverGen(WorldInfo worldInfo) {
    if (Utils.isClientSide()) {
      String silverGen = Holder.silverGenStrings.get(worldInfo);
      if (silverGen != null) return new NBTTagString(silverGen);
    }
    return worldInfo.getAdditionalProperty(OnlySilvers.MODID + "-silverGen");
  }


  // ======================================== Common hooks ========================================

  public static void loadWorldProperty(Map<String, NBTBase> properties, WorldInfo worldInfo, NBTTagCompound tagCompound) {
    String silverGen;
    NBTTagCompound onlysilver = tagCompound.getCompoundTag(OnlySilvers.MODID);

    if (onlysilver.hasKey("silverGen", 8)) silverGen = onlysilver.getString("silverGen");
    else {
      JsonHelper ext = null;
      String generatorOptions = worldInfo.getGeneratorOptions();

      //noinspection ConstantConditions
      if (generatorOptions != null && !generatorOptions.isEmpty()) {
        Factory factory = Factory.jsonToFactory(generatorOptions);
        ext = INSTANCE.factories.get(factory);
      }
      if (ext == null) ext = Config.current().silverGenDefaults.get().get();

      silverGen = new Gson().toJson(ext.asJsonObject());
    }

    properties.put(OnlySilvers.MODID + "-silverGen", new NBTTagString(silverGen));
  }

  @Hook @Static(FMLCommonHandler.class)
  public static void handleWorldDataSave(SaveHandler handler, WorldInfo worldInfo, NBTTagCompound tagCompound) {
    NBTBase silverGen = getSilverGen(worldInfo);
    if (silverGen instanceof NBTTagString) {
      NBTTagCompound onlysilver = new NBTTagCompound();
      onlysilver.setTag("silverGen", silverGen);
      tagCompound.setTag(OnlySilvers.MODID, onlysilver);
    }
  }

  @Hook @Srg("func_177863_a")
  public static void setDefaults(Factory factory) {
    INSTANCE.factories.remove(factory);
  }

  @Hook(Strategy.AGENT)
  public static int hashCode(int hash, Factory factory) {
    JsonHelper ext = INSTANCE.factories.get(factory);
    return 31 * hash + (ext == null ? 0 : ext.hashCode());
  }

  @Hook @ReturnBoolean(condition = false, value = false)
  public static boolean equals(Factory factory, Object object) {
    if (object != factory && object instanceof Factory) {
      Map<Factory, JsonHelper> exts = INSTANCE.factories;
      return Objects.equal(exts.get(factory), exts.get(object));
    }
    return true;
  }

  @Hook(Strategy.AGENT)
  public static <T> void deserialize(T result, Serializer caller, JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
    try {
      JsonHelper j = new JsonHelper((JsonObject)  json).getAsHelper(OnlySilvers.MODID + "-silver");
      if (!j.isEmpty()) INSTANCE.factories.put((Factory) result, j);
    } catch (JsonSyntaxException ignored) { }
  }

  @Hook(Strategy.AGENT)
  public static <T> void serialize(JsonElement result, Serializer caller, T src, Type typeOfSrc, JsonSerializationContext ctx) {
    JsonHelper ext = INSTANCE.factories.get(src);
    if (ext != null) new JsonHelper((JsonObject) result).set(OnlySilvers.MODID + "-silver", ext);
  }


  // ======================================== Client explicit hooks ========================================

  @SideOnly(Side.CLIENT)
  private static class Holder {
    static final Map<GuiCustomizeWorldScreen, JsonHelper> genDefaults = new MapMaker().weakKeys().makeMap();
    static final Map<Object, String> silverGenStrings = new MapMaker().weakKeys().makeMap();
    static List<WeakReference<GuiListEntry>> extraEntries;
  }

  @SideOnly(Side.CLIENT) @Hook @Static(FMLCommonHandler.class)
  public static void handleWorldDataLoad(SaveHandler handler, WorldInfo worldInfo, NBTTagCompound tagCompound) {
    if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
      NBTTagCompound onlysilver = tagCompound.getCompoundTag(OnlySilvers.MODID);
      if (onlysilver.hasKey("silverGen", 8)) Holder.silverGenStrings.put(worldInfo, onlysilver.getString("silverGen"));
    }
  }

  @SideOnly(Side.CLIENT) @Hook @Srg("func_146318_a")
  public static void recreateFromExistingWorld(GuiCreateWorld gui, WorldInfo worldInfo) {
    Map<Object, String> map = Holder.silverGenStrings;
    String silverGen = map.get(worldInfo);
    if (silverGen != null) map.put(gui, silverGen);
  }

  @SideOnly(Side.CLIENT)
  public static void onWorldSettingsCreated(WorldSettings worldSettings, GuiCreateWorld gui) {
    Map<Object, String> map = Holder.silverGenStrings;
    String silverGen = map.get(gui);
    if (silverGen != null) map.put(worldSettings, silverGen);
  }

  @SideOnly(Side.CLIENT) @Hook(Strategy.RETURN) @Name("<init>")
  public static void onWorldInfoBuilt(WorldInfo worldInfo, WorldSettings worldSettings, String name) {
    Map<Object, String> map = Holder.silverGenStrings;
    String silverGen = map.get(worldSettings);
    if (silverGen != null) map.put(worldInfo, silverGen);
  }

  @SideOnly(Side.CLIENT)
  public static GuiListEntry[][] onGuiInit(GuiListEntry[][] entries, GuiCustomizeWorldScreen gui, Factory factory) {
    UniqueIntegers freeIds = UniqueIntegers.startFrom(0x16A65CDC);
    for (GuiListEntry[] a : entries)
      for (GuiListEntry entry : a)
        if (entry != null)
          freeIds.remove(entry.getId());

    GenDefaults genDefaults = Config.current().silverGenDefaults.get();
    Holder.genDefaults.put(gui, genDefaults.get());
    JsonHelper ext = INSTANCE.factories.get(factory);
    if (ext == null) ext = genDefaults.get();

    List<GuiListEntry> toAdd = Lists.newArrayList();
    toAdd.add(new GuiLabelEntry(freeIds.next(), LangData.SILVER_ORE_ONLY_SILVER.get(), false));
    toAdd.add(null);
    toAdd.add(new GuiSlideEntry(freeIds.next(), LangData.SIZE.get(), false, gui, 1, 50, ext.getAsInt("size")));
    toAdd.add(new GuiSlideEntry(freeIds.next(), LangData.COUNT.get(), false, gui, 0, 40, ext.getAsInt("count")));
    toAdd.add(new GuiSlideEntry(freeIds.next(), LangData.MIN_HEIGHT.get(), false, gui, 0, 255, ext.getAsInt("minHeight")));
    toAdd.add(new GuiSlideEntry(freeIds.next(), LangData.MAX_HEIGHT.get(), false, gui, 0, 255, ext.getAsInt("maxHeight")));

    Holder.extraEntries = Lists.newArrayList();
    for (int i = 2; i < 6; i++)
      Holder.extraEntries.add(new WeakReference<>(toAdd.get(i)));

    Class<GuiListEntry> clz = GuiListEntry.class;
    entries[1] = ObjectArrays.concat(entries[1], Iterables.toArray(toAdd, clz), clz);
    return entries;
  }

  @SideOnly(Side.CLIENT)
  public static void onGuiSetFloatValue(GuiCustomizeWorldScreen gui, Factory factory, int elementId, float value) {
    List<WeakReference<GuiListEntry>> extraEntries = Holder.extraEntries;

    if (extraEntries != null)
      for (int i = 0; i < 4; i++) {
        GuiListEntry entry = extraEntries.get(i).get();

        if (entry != null && entry.getId() == elementId) {
          String s;
          switch (i) {
            case 0: s = "size"; break;
            case 1: s = "count"; break;
            case 2: s = "minHeight"; break;
            case 3: s = "maxHeight"; break;
            default: throw new IllegalArgumentException();
          }

          Map<Factory, JsonHelper> exts = INSTANCE.factories;
          JsonHelper ext = exts.get(factory);
          if (ext != null) {
            ext.set(s, (int) value);
            if (ext.equals(Holder.genDefaults.get(gui))) exts.remove(factory);
          }
          else {
            ext = Config.current().silverGenDefaults.get().get().set(s, (int) value);
            if (!ext.equals(Holder.genDefaults.get(gui))) exts.put(factory, ext);
          }
        }
      }
  }

  @SideOnly(Side.CLIENT)
  private static class UniqueIntegers extends AbstractIterator<Integer> {
    private static final RangeSet<Integer> ALL = ImmutableRangeSet.of(Range.<Integer>all().canonical(DiscreteDomain.integers()));
    private final RangeSet<Integer> free = TreeRangeSet.create(ALL);
    private Integer next;
    private UniqueIntegers() { }

    public static UniqueIntegers startFrom(int i) {
      UniqueIntegers ret = new UniqueIntegers();
      ret.next = i;
      return ret;
    }
    public void remove(Integer i) {
      free.remove(Range.singleton(i).canonical(DiscreteDomain.integers()));
    }

    @Override protected Integer computeNext() {
      if (!free.isEmpty()) {
        Range<Integer> r = free.complement().rangeContaining(next);
        if (r != null) {
          if (r.hasUpperBound()) next = r.upperEndpoint();
          else {
            r = free.complement().rangeContaining(Integer.MIN_VALUE);
            next = r != null ? r.upperEndpoint() : Integer.MIN_VALUE;
          }
        }
        Integer ret = next++;
        remove(ret);
        return ret;
      }
      return endOfData();
    }
  }

}
