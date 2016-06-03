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
package zotmc.onlysilver.config;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableInt;
import zotmc.onlysilver.Contents;
import zotmc.onlysilver.ItemFeature;
import zotmc.onlysilver.config.AbstractConfig.Property;
import zotmc.onlysilver.config.gui.*;
import zotmc.onlysilver.data.LangData;
import zotmc.onlysilver.data.ModData.MoCreatures;
import zotmc.onlysilver.oregen.ExtSettings;
import zotmc.onlysilver.util.Utils;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"StaticPseudoFunctionalStyleMethod", "Guava", "unused"})
@SideOnly(Side.CLIENT)
public class ConfigGui extends AbstractConfigFactory {

  private static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
  private Config temp;
  private boolean hide, tool, armor;

  @Override public void create() {
    Config.inFile().loadFromFile();
    temp = Config.inFile().copy();
    hide = true;
  }

  @Override protected void reset() {
    temp.clear();
    tool = anyTool();
    armor = anyArmor();
  }

  @Override public void save() {
    Config.inFile().apply(temp);
    temp.saveToFile();
  }

  @Override public void destroy() {
    temp = null;
  }

  @Override protected Supplier<String> getTitle() {
    return LangData.ONLYSILVER_OPTIONS;
  }

  @Override protected Iterable<? extends Element> getExtraElements(int w, int h, Holder<List<String>> hoveringText) {
    Mutable<Boolean> state = new Mutable<Boolean>() {
      @Override public Boolean getValue() {
        return !hide;
      }
      @Override public void setValue(Boolean value) {
        hide = !value;
      }
    };
    return ImmutableList.of(new TinyCheckbox(state, Utils.doNothing()).setLeftTop(w / 2 + 48, 14));
  }

  @Override public Iterable<Row> getUpperRows(int w, Holder<List<String>> hoveringText) {
    List<Row> rows = Lists.newArrayList();

    rows.add(new ItemIcon(Blocks.GRAVEL).categoryRow(LangData.ORE_GENERATION));

    rows.add(new GenDefaultsRow() {
      @Override protected Property<GenDefaults> toProperty(Config t) {
        return t.silverGenDefaults;
      }
      @Override protected Supplier<String> title() {
        return LangData.SILVER_GEN_DEFAULTS;
      }
    });

    rows.add(EmptyRow.INSTANCE);

    rows.add(new ItemIcon(Items.ENCHANTED_BOOK).setRenderEffect(false).categoryRow(LangData.ENCHANTMENTS));

    rows.add(new BooleanRow() {
      @Override protected Property<Boolean> toProperty(Config t) {
        return t.silverAuraEnabled;
      }
      @Override protected Supplier<String> title() {
        return LangData.SILVER_AURA;
      }
    });

    rows.add(new BooleanRow() {
      @Override protected Property<Boolean> toProperty(Config t) {
        return t.incantationEnabled;
      }
      @Override protected Supplier<String> title() {
        return LangData.INCANTATION;
      }
    });

    rows.add(EmptyRow.INSTANCE);

    rows.add(new SpriteIcon(GuiScreen.STAT_ICONS, 36, 18, 128).categoryRow(LangData.STATS));

    rows.add(new BlockStatsRow() {
      @Override protected Property<BlockStats> toProperty(Config t) {
        return t.silverOreStats;
      }
      @Override protected Supplier<Block> getBlock() {
        return Contents.silverOre;
      }
    });

    rows.add(new BlockStatsRow() {
      @Override protected Property<BlockStats> toProperty(Config t) {
        return t.silverBlockStats;
      }
      @Override protected Supplier<Block> getBlock() {
        return Contents.silverBlock;
      }
    });

    tool = anyTool();
    rows.add(new ToolStatsRow() {
      @Override protected Property<ToolStats> toProperty(Config t) {
        return t.silverToolStats;
      }
      @Override protected Supplier<String> title() {
        return LangData.SILVER_TOOLS;
      }
      @Override public boolean folded() {
        return hide && !tool;
      }
    });

    armor = anyArmor();
    rows.add(new ArmorStatsRow() {
      @Override protected Property<ArmorStats> toProperty(Config t) {
        return t.silverArmorStats;
      }
      @Override protected Supplier<String> title() {
        return LangData.SILVER_ARMOR;
      }
      @Override public boolean folded() {
        return hide && !armor;
      }
    });

    rows.add(EmptyRow.INSTANCE);

    rows.add(new ItemIcon(Items.LAVA_BUCKET).categoryRow(LangData.MISCELLANEOUS));

    rows.add(new FeatureSetRow() {
      @Override protected Property<Set<String>> toProperty(Config t) {
        return t.disabledFeatures;
      }
      @Override protected Supplier<String> title() {
        return LangData.OPTIONAL_FEATURES;
      }
    });

    rows.add(new BooleanRow() {
      final boolean bow = ItemFeature.silverBow.enabled(temp);

      @Override protected Property<Boolean> toProperty(Config t) {
        return t.meleeBowKnockback;
      }
      @Override protected Supplier<String> title() {
        return LangData.MELEE_BOW_KNOCKBACK;
      }
      @Override public boolean folded() {
        return hide && !bow;
      }
    });

    rows.add(new BooleanRow() {
      final boolean moc = Loader.isModLoaded(MoCreatures.MODID);

      @Override protected Property<Boolean> toProperty(Config t) {
        return t.werewolfEffectiveness;
      }
      @Override protected Supplier<String> title() {
        return LangData.WEREWOLF_EFFECTIVENESS;
      }
      @Override public boolean folded() {
        return hide && !moc;
      }
    });

    rows.add(new BooleanRow() {
      @Override protected Property<Boolean> toProperty(Config t) {
        return t.silverGolemAssembly;
      }
      @Override protected Supplier<String> title() {
        return LangData.SILVER_GOLEM_ASSEMBLY;
      }
    });

    rows.add(EmptyRow.INSTANCE);

    return rows;
  }

  private boolean anyTool() {
    for (ItemFeature i : ItemFeature.values())
      if (i.isTool() && i.enabled(temp)) return true;
    return false;
  }

  private boolean anyArmor() {
    for (ItemFeature i : ItemFeature.values())
      if (i.isArmor() && i.enabled(temp)) return true;
    return false;
  }



  private abstract class BooleanRow extends AbstractBooleanRow<Config> {
    // config
    @Override protected Config getTemp() {
      return temp;
    }
    @Override protected Boolean getRawValue(Property<Boolean> p) {
      return p.getRaw();
    }
    @Override protected void setRawValue(Property<Boolean> p, Boolean v) {
      p.setRaw(v);
    }
  }


  private static abstract class LogSliderRow extends SliderRow {
    static final double A = Math.log(0.3819660112501051);
  }

  private static abstract class IntSliderRow extends LogSliderRow {
    private static final double B = Math.log(Integer.MAX_VALUE);
    private final int defaultValue;
    private final double k, l;

    IntSliderRow(int defaultValue) {
      this.defaultValue = defaultValue;
      k = A / (-B + Math.log(Math.max(1, defaultValue)));
      l = Math.pow(Integer.MAX_VALUE, k);
    }
    protected abstract int getValue();
    protected abstract void setValue(int value);

    // slider
    @Override public String getText() {
      int value = getValue();
      String s = Integer.toString(value);
      return value != defaultValue ? s : LangData.DEFAULT.toString(s);
    }
    @Override public double getPosition() {
      return Math.pow(getValue(), k) / l;
    }
    @Override public void setPosition(double position) {
      setValue((int) Math.rint(Math.exp(B + 1/k * Math.log(position))));
    }
    @Override public void previous() {
      setValue(getValue() - 1);
    }
    @Override public void next() {
      setValue(getValue() + 1);
    }
  }

  private static abstract class FloatSliderRow extends LogSliderRow {
    private static final double B = Math.log(Float.MAX_VALUE);
    private final float defaultValue;
    private final double step, k, l;

    FloatSliderRow(float defaultValue, double step) {
      this.defaultValue = defaultValue;
      this.step = step;
      k = A / (-B + Math.log(Math.max(1, defaultValue)));
      l = Math.pow(Float.MAX_VALUE, k);
    }
    protected abstract float getValue();
    protected abstract void setValue(float value);

    // slider
    @Override public String getText() {
      float value = getValue();
      String s = Float.toString(value);
      return value != defaultValue ? s : LangData.DEFAULT.toString(s);
    }
    @Override public double getPosition() {
      return Math.pow(getValue(), k) / l;
    }
    @Override public void setPosition(double position) {
      setValue((float) Math.exp(B + 1/k * Math.log(position)));
    }
    @Override public void previous() {
      setValue(Utils.previous(getValue(), step));
    }
    @Override public void next() {
      setValue(Utils.next(getValue(), step));
    }
  }


  private abstract class EditorRow<V> extends ValueInjectionRow<Config, V> {
    // config
    @Override protected Config getTemp() {
      return temp;
    }
    @Override protected void setValue(Property<V> p, V v) {
      p.set(v);
    }
  }

  private abstract class GenDefaultsRow extends EditorRow<GenDefaults> {
    String dimensions;
    MutableInt size, count, minHeight, maxHeight;

    @Override protected void injectValue(GenDefaults v) {
      String s = v.dimensions;
      dimensions = s != null ? s : ExtSettings.DEFAULT_DIMS.regex;
      size = new MutableInt(v.size);
      count = new MutableInt(v.count);
      minHeight = new MutableInt(v.minHeight);
      maxHeight = new MutableInt(v.maxHeight);
    }
    @Override protected GenDefaults toImmutable() {
      String s = !Objects.equal(dimensions, ExtSettings.DEFAULT_DIMS.regex) ? dimensions : null;
      return new GenDefaults(s, size.intValue(), count.intValue(), minHeight.intValue(), maxHeight.intValue());
    }

    @Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText, GenDefaults defaultValue) {
      List<Row> upper = ImmutableList.of(new TextFieldRow() {
        @Override public String getText() {
          return dimensions;
        }
        @Override public void setText(String text) {
          dimensions = text;
        }
        @Override protected Supplier<String> title() {
          return LangData.DIMENSIONS.append(":");
        }
        @Override protected int widgetPos(int k) {
          return k / 3;
        }
      });

      Iterable<Row> lower = new BasicOreSettingsLayout() {
        @Override protected MutableInt size() {
          return size;
        }
        @Override protected MutableInt count() {
          return count;
        }
        @Override protected MutableInt minHeight() {
          return minHeight;
        }
        @Override protected MutableInt maxHeight() {
          return maxHeight;
        }
      };

      return Iterables.concat(upper, lower);
    }
  }

  private abstract class BlockStatsRow extends EditorRow<BlockStats> {
    private final ItemIcon icon = new ItemIcon(getBlock().get());
    private int harvestLevel;
    private float hardness, resistance;

    // title
    protected abstract Supplier<Block> getBlock();

    @Override protected Icon<?> icon() {
      return icon;
    }
    @Override protected Supplier<String> title() {
      return icon;
    }

    // value
    @Override protected void injectValue(BlockStats v) {
      harvestLevel = v.harvestLevel;
      hardness = v.hardness;
      resistance = v.resistance;
    }
    @Override protected BlockStats toImmutable() {
      return new BlockStats(harvestLevel, hardness, resistance);
    }

    // screen
    @Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText, final BlockStats defaultValue) {
      List<Row> ret = Lists.newArrayList();

      if (defaultValue.harvestLevel >= 0)
        ret.add(new IntSliderRow(defaultValue.harvestLevel) {
          final Icon<?> icon = new ItemIcon(Items.DIAMOND, Icon.PHI_M1).overlay(Blocks.STONE);

          @Override protected Icon<?> icon() {
            return icon;
          }
          @Override protected Supplier<String> title() {
            return LangData.HARVEST_LEVEL;
          }

          @Override protected int getValue() {
            return harvestLevel;
          }
          @Override protected void setValue(int value) {
            harvestLevel = value;
          }
        });

      ret.add(new FloatSliderRow(defaultValue.hardness, 0.05) {
        final Icon<?> icon = new ItemIcon(Items.IRON_PICKAXE, Icon.PHI_M1).overlay(Blocks.STONE);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.HARDNESS;
        }

        @Override protected float getValue() {
          return hardness;
        }
        @Override protected void setValue(float value) {
          hardness = value;
        }
      });

      ret.add(new FloatSliderRow(defaultValue.resistance, 0.5) {
        final Icon<?> icon = new ItemIcon(Items.GUNPOWDER, Icon.PHI_M1).overlay(Blocks.STONE);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.RESISTANCE;
        }

        @Override protected float getValue() {
          return resistance;
        }
        @Override protected void setValue(float value) {
          resistance = value;
        }
      });

      return ret;
    }
  }

  private abstract class ToolStatsRow extends EditorRow<ToolStats> {
    private int harvestLevel, maxUses;
    private float efficiency, damage;
    private int enchantability;

    // value
    @Override protected void injectValue(ToolStats v) {
      harvestLevel = v.harvestLevel;
      maxUses = v.maxUses;
      efficiency = v.efficiency;
      damage = v.damage;
      enchantability = v.enchantability;
    }
    @Override protected ToolStats toImmutable() {
      return new ToolStats(harvestLevel, maxUses, efficiency, damage, enchantability);
    }

    // screen
    @Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText, ToolStats defaultValue) {
      List<Row> ret = Lists.newArrayList();

      ret.add(new IntSliderRow(defaultValue.harvestLevel) {
        final Icon<?> icon = new ItemIcon(Items.DIAMOND, Icon.PHI_M1).overlay(Items.IRON_PICKAXE);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.HARVEST_LEVEL;
        }

        @Override protected int getValue() {
          return harvestLevel;
        }
        @Override protected void setValue(int value) {
          harvestLevel = value;
        }
      });

      ret.add(new IntSliderRow(defaultValue.maxUses) {
        final Icon<?> icon = new SpriteIcon(Gui.STAT_ICONS, 72, 18, 128);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.MAX_USES;
        }

        @Override protected int getValue() {
          return maxUses;
        }
        @Override protected void setValue(int value) {
          maxUses = value;
        }
      });

      ret.add(new FloatSliderRow(defaultValue.efficiency, 1) {
        final Icon<?> icon = new SpriteIcon(inventoryBackground, 36, 198, 256);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.EFFICIENCY;
        }

        @Override protected float getValue() {
          return efficiency;
        }
        @Override protected void setValue(float value) {
          efficiency = value;
        }
      });

      ret.add(new FloatSliderRow(defaultValue.damage, 0.5) {
        final Icon<?> icon = new SpriteIcon(inventoryBackground, 72, 198, 256);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.DAMAGE;
        }

        @Override protected float getValue() {
          return damage;
        }
        @Override protected void setValue(float value) {
          damage = value;
        }
      });

      ret.add(new IntSliderRow(defaultValue.enchantability) {
        final Icon<?> icon = new ItemIcon(Blocks.ENCHANTING_TABLE);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.ENCHANTABILITY;
        }

        @Override protected int getValue() {
          return enchantability;
        }
        @Override protected void setValue(int value) {
          enchantability = value;
        }
      });

      return ret;
    }
  }

  private abstract class ArmorStatsRow extends EditorRow<ArmorStats> {
    private int durability;
    private int[] reductionAmounts;
    private int enchantability;
    private float toughness;

    // value
    @Override protected void injectValue(ArmorStats v) {
      durability = v.durability;
      reductionAmounts = Ints.toArray(v.reductionAmounts);
      enchantability = v.enchantability;
    }
    @Override protected ArmorStats toImmutable() {
      int[] r = reductionAmounts;
      return new ArmorStats(durability, r[0], r[1], r[2], r[3], enchantability, toughness);
    }

    // screen
    @Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText, ArmorStats defaultValue) {
      List<Row> ret = Lists.newArrayList();

      ret.add(new IntSliderRow(defaultValue.durability) {
        final Icon<?> icon = new SpriteIcon(Gui.STAT_ICONS, 72, 18, 128);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.DURABILITY;
        }

        @Override protected int getValue() {
          return durability;
        }
        @Override protected void setValue(int value) {
          durability = value;
        }
      });

      ret.add(new IntSliderRow(defaultValue.reductionAmounts.get(0)) {
        final Icon<?> icon = new ItemIcon(Items.CHAINMAIL_HELMET);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.HELMET_DEFENSE_POINT;
        }

        @Override protected int getValue() {
          return reductionAmounts[0];
        }
        @Override protected void setValue(int value) {
          reductionAmounts[0] = value;
        }
      });

      ret.add(new IntSliderRow(defaultValue.reductionAmounts.get(1)) {
        final Icon<?> icon = new ItemIcon(Items.CHAINMAIL_CHESTPLATE);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.CHESTPLATE_DEFENSE_POINT;
        }

        @Override protected int getValue() {
          return reductionAmounts[1];
        }
        @Override protected void setValue(int value) {
          reductionAmounts[1] = value;
        }
      });

      ret.add(new IntSliderRow(defaultValue.reductionAmounts.get(2)) {
        final Icon<?> icon = new ItemIcon(Items.CHAINMAIL_LEGGINGS);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.LEGGINGS_DEFENSE_POINT;
        }

        @Override protected int getValue() {
          return reductionAmounts[2];
        }
        @Override protected void setValue(int value) {
          reductionAmounts[2] = value;
        }
      });

      ret.add(new IntSliderRow(defaultValue.reductionAmounts.get(3)) {
        final Icon<?> icon = new ItemIcon(Items.CHAINMAIL_BOOTS);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.BOOTS_DEFENSE_POINT;
        }

        @Override protected int getValue() {
          return reductionAmounts[3];
        }
        @Override protected void setValue(int value) {
          reductionAmounts[3] = value;
        }
      });

      ret.add(new IntSliderRow(defaultValue.enchantability) {
        final Icon<?> icon = new ItemIcon(Blocks.ENCHANTING_TABLE);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.ENCHANTABILITY;
        }

        @Override protected int getValue() {
          return enchantability;
        }
        @Override protected void setValue(int value) {
          enchantability = value;
        }
      });

      ret.add(new FloatSliderRow(defaultValue.toughness, 0.1) {
        final Icon<?> icon = new ItemIcon(Blocks.DIAMOND_BLOCK);

        @Override protected Icon<?> icon() {
          return icon;
        }
        @Override protected Supplier<String> title() {
          return LangData.TOUGHNESS;
        }
        @Override protected float getValue() {
          return toughness;
        }
        @Override protected void setValue(float value) {
          toughness = value;
        }
      });

      return ret;
    }
  }

  private abstract class FeatureSetRow extends EditorRow<Set<String>> {
    Set<String> disabled;

    @Override protected void injectValue(Set<String> v) {
      disabled = Sets.newLinkedHashSet(v);
    }
    @Override protected Set<String> toImmutable() {
      return ImmutableSet.copyOf(disabled);
    }

    @Override public int getRowHeight() {
      return IconButtonLayout.SLOT_SIZE;
    }
    @Override protected Iterable<Row> getLowerRows() {
      return ImmutableList.of(EmptyRow.INSTANCE);
    }

    @Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText, Set<String> defaultValue) {
      List<IconButton> buttons = Lists.newArrayList();
      final Set<IconButton> accessible = Sets.newIdentityHashSet();

      for (ItemFeature i : ItemFeature.values())
        if (!i.important()) {
          IconButton button;
          if (i.exists()) button = new ItemIcon(i.get()).iconButton(enabled(i.getItemId()), hoveringText);
          else {
            button = new SpriteIcon(i.getGuiWatermark(), 0, 0, 16, 16, 16)
              .setAlpha(0x99)
              .iconButton(enabled(i.getItemId()), tooltip(i.getLocalization()), hoveringText);
          }
          if (i.enabled(null)) accessible.add(button);
          buttons.add(button);
        }

      Predicate<IconButton> p = input -> !hide || accessible.contains(input);
      return new IconButtonLayout(Iterables.filter(buttons, p), w);
    }

    private Mutable<Boolean> enabled(final String key) {
      return new Mutable<Boolean>() {
        @Override public Boolean getValue() {
          return !disabled.contains(key);
        }
        @Override public void setValue(Boolean value) {
          if (value) disabled.remove(key);
          else disabled.add(key);
        }
      };
    }
    private Supplier<List<String>> tooltip(Supplier<String> name) {
      return Suppliers.ofInstance(ItemIcon.colorTooltip(Lists.newArrayList(name.get()), TextFormatting.DARK_GRAY));
    }
  }

}
