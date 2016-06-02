package zotmc.onlysilver.data;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import zotmc.onlysilver.loading.AbstractInsnPatcher;
import zotmc.onlysilver.loading.AbstractMethodPatcher;
import zotmc.onlysilver.loading.InsnListBuilder;
import zotmc.onlysilver.loading.MethodPredicate;
import zotmc.onlysilver.loading.Patcher;
import zotmc.onlysilver.loading.Patcher.ClientOnly;
import zotmc.onlysilver.loading.Patcher.Hook;
import zotmc.onlysilver.loading.TypePredicate;

// TODO: Forge PR to eliminate needs for low level hooks
public class OreGenAsm {

  @Hook
  private static final TypePredicate
  HANDLER = TypePredicate.of("zotmc/onlysilver/oregen/OreGenHandler");

  // callbacks
  static final MethodPredicate
  LOAD_WORLD_PROPERTY = HANDLER.method("loadWorldProperty")
    .desc("(Ljava/util/Map;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/nbt/NBTTagCompound;)V");

  @ClientOnly
  private static class Holder {
    public static final MethodPredicate
    ON_WORLD_SETTINGS_CREATED = HANDLER.method("onWorldSettingsCreated")
      .desc("(Lnet/minecraft/world/WorldSettings;Lnet/minecraft/client/gui/GuiCreateWorld;)V"),
    ON_GUI_INIT = HANDLER.method("onGuiInit")
      .desc("([[Lnet/minecraft/client/gui/GuiPageButtonList$GuiListEntry;Lnet/minecraft/client/gui/GuiCustomizeWorldScreen;"
          + "Lnet/minecraft/world/gen/ChunkProviderSettings$Factory;)"
          + "[[Lnet/minecraft/client/gui/GuiPageButtonList$GuiListEntry;"),
    ON_GUI_SET_FLOAT_VALUE = HANDLER.method("onGuiSetFloatValue")
      .desc("(Lnet/minecraft/client/gui/GuiCustomizeWorldScreen;Lnet/minecraft/world/gen/ChunkProviderSettings$Factory;IF)V");
  }


  private static class WorldInfos {
    // targets
    public static final MethodPredicate
    SET_ADDITIONAL_PROPERTIES = TypePredicate.of("net/minecraft/world/storage/WorldInfo")
      .method("setAdditionalProperties")
      .desc("(Ljava/util/Map;)V");
  }

  public static class FMLCommonHandlers {
    // targets
    private static final MethodPredicate
    HANDLE_WORLD_DATA_LOAD = TypePredicate.of("net/minecraftforge/fml/common/FMLCommonHandler")
      .method("handleWorldDataLoad")
      .desc("(Lnet/minecraft/world/storage/SaveHandler;Lnet/minecraft/world/storage/WorldInfo;"
          + "Lnet/minecraft/nbt/NBTTagCompound;)V");

    // patches
    public static final Patcher
    HANDLE_WORLD_DATA_LOAD_PATCHER = new AbstractInsnPatcher(HANDLE_WORLD_DATA_LOAD) {
      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return WorldInfos.SET_ADDITIONAL_PROPERTIES.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder();

        pre.dup();
        pre.aload(2);
        pre.aload(3);
        pre.invokestatic(LOAD_WORLD_PROPERTY, false);

        list.insertBefore(targetInsn, pre.build());
      }
    };
  }

  @ClientOnly
  private static class WorldSettingss {
    // targets
    public static final MethodPredicate
    SET_WORLD_NAME = TypePredicate.of("net/minecraft/world/WorldSettings")
      .method("setWorldName", "func_82750_a")
      .desc("(Ljava/lang/String;)Lnet/minecraft/world/WorldSettings;");
  }

  @ClientOnly
  public static class GuiCreateWorlds {
    // targets
    private static final MethodPredicate
    ACTION_PERFORMED = TypePredicate.of("net/minecraft/client/gui/GuiScreen")
      .method("actionPerformed", "func_146284_a")
      .desc("(Lnet/minecraft/client/gui/GuiButton;)V");

    // patches
    public static final Patcher
    ACTION_PERFORMED_PATCHER = new AbstractInsnPatcher(ACTION_PERFORMED) {
      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return WorldSettingss.SET_WORLD_NAME.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder post = new InsnListBuilder();

        post.dup();
        post.aload(0);
        post.invokestatic(Holder.ON_WORLD_SETTINGS_CREATED, false);

        list.insert(targetInsn, post.build());
      }
    };
  }

  @ClientOnly
  private static class GuiPageButtonLists {
    // targets
    public static final MethodPredicate
    CTOR = TypePredicate.of("net/minecraft/client/gui/GuiPageButtonList")
      .method("<init>")
      .desc("(Lnet/minecraft/client/Minecraft;IIIIILnet/minecraft/client/gui/GuiPageButtonList$GuiResponder;"
          + "[[Lnet/minecraft/client/gui/GuiPageButtonList$GuiListEntry;)V");
  }

  @ClientOnly
  public static class GuiCustomizeWorldScreens {
    // type
    private static final TypePredicate TYPE = TypePredicate.of("net/minecraft/client/gui/GuiCustomizeWorldScreen");

    // callbacks
    private static final MethodPredicate
    FACTORY = TYPE.method("field_175336_F").desc("Lnet/minecraft/world/gen/ChunkProviderSettings$Factory;");

    // targets
    private static final MethodPredicate
    INIT_GUI_ENTRIES = TYPE.method("func_175325_f").desc("()V"),
    SET_FLOAT_VALUE = TYPE.method("func_175320_a").desc("(IF)V");

    // patches
    public static final Patcher
    INIT_GUI_ENTRIES_PATCHER = new AbstractInsnPatcher(INIT_GUI_ENTRIES) {
      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return GuiPageButtonLists.CTOR.covers(Opcodes.INVOKESPECIAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder();

        pre.aload(0);
        pre.aload(0);
        pre.getfield(FACTORY);
        pre.invokestatic(Holder.ON_GUI_INIT, false);

        list.insertBefore(targetInsn, pre.build());
      }
    },
    SET_FLOAT_VALUE_PATCHER = new AbstractMethodPatcher(SET_FLOAT_VALUE) {
      @Override protected void processMethod(MethodNode targetMethod) {
        InsnListBuilder pre = new InsnListBuilder();

        pre.aload(0);
        pre.aload(0);
        pre.getfield(FACTORY);
        pre.iload(1);
        pre.fload(2);
        pre.invokestatic(Holder.ON_GUI_SET_FLOAT_VALUE, false);

        targetMethod.instructions.insert(pre.build());
      }
    };
  }

}
