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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import zotmc.onlysilver.loading.*;
import zotmc.onlysilver.loading.Patcher.ClientOnly;
import zotmc.onlysilver.loading.Patcher.Hook;

// TODO: Forge PR to eliminate needs for low level hooks
@SuppressWarnings("WeakerAccess")
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
        .desc("([[Lnet/minecraft/client/gui/GuiPageButtonList$GuiListEntry;" +
            "Lnet/minecraft/client/gui/GuiCustomizeWorldScreen;" +
            "Lnet/minecraft/world/gen/ChunkProviderSettings$Factory;)" +
            "[[Lnet/minecraft/client/gui/GuiPageButtonList$GuiListEntry;"),
    ON_GUI_SET_FLOAT_VALUE = HANDLER.method("onGuiSetFloatValue")
        .desc("(Lnet/minecraft/client/gui/GuiCustomizeWorldScreen;" +
            "Lnet/minecraft/world/gen/ChunkProviderSettings$Factory;IF)V");
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
        .desc("(Lnet/minecraft/world/storage/SaveHandler;Lnet/minecraft/world/storage/WorldInfo;" +
            "Lnet/minecraft/nbt/NBTTagCompound;)V");

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
  private static class Minecrafts {
    // targets
    public static final MethodPredicate
    LAUNCH_INTEGRATED_SERVER = TypePredicate.of("net/minecraft/client/Minecraft")
        .method("launchIntegratedServer", "func_71371_a")
        .desc("(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V");
  }

  @ClientOnly
  public static class GuiCreateWorlds {
    // targets
    private static final MethodPredicate
    ACTION_PERFORMED = TypePredicate.of("net/minecraft/client/gui/GuiCreateWorld")
        .method("actionPerformed", "func_146284_a")
        .desc("(Lnet/minecraft/client/gui/GuiButton;)V");

    // patches
    public static final Patcher
    ACTION_PERFORMED_PATCHER = new AbstractInsnPatcher(ACTION_PERFORMED) {
      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return Minecrafts.LAUNCH_INTEGRATED_SERVER.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder();

        pre.dup();
        pre.aload(0);
        pre.invokestatic(Holder.ON_WORLD_SETTINGS_CREATED, false);

        list.insertBefore(targetInsn, pre.build());
      }
    };
  }

  @ClientOnly
  private static class GuiPageButtonLists {
    // targets
    public static final MethodPredicate
    CTOR = TypePredicate.of("net/minecraft/client/gui/GuiPageButtonList")
        .method("<init>")
        .desc("(Lnet/minecraft/client/Minecraft;IIIIILnet/minecraft/client/gui/GuiPageButtonList$GuiResponder;" +
            "[[Lnet/minecraft/client/gui/GuiPageButtonList$GuiListEntry;)V");
  }

  @ClientOnly
  public static class GuiCustomizeWorldScreens {
    // type
    private static final TypePredicate TYPE = TypePredicate.of("net/minecraft/client/gui/GuiCustomizeWorldScreen");

    // callbacks
    private static final MethodPredicate
    SETTINGS = TYPE.method("settings")
        .desc("Lnet/minecraft/world/gen/ChunkProviderSettings$Factory;"),
    SETTINGS_SRG = TYPE.method("field_175336_F")
        .desc("Lnet/minecraft/world/gen/ChunkProviderSettings$Factory;");

    // targets
    private static final MethodPredicate
    CREATE_PAGED_LIST = TYPE.method("createPagedList", "func_175325_f").desc("()V"),
    SET_ENTRY_VALUE = TYPE.method("setEntryValue", "func_175320_a").desc("(IF)V");

    // patches
    public static final Patcher
    CREATE_PAGED_LIST_PATCHER = new AbstractInsnPatcher(CREATE_PAGED_LIST) {
      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return GuiPageButtonLists.CTOR.covers(Opcodes.INVOKESPECIAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder();

        pre.aload(0);
        pre.aload(0);
        pre.getfield(useMcpNames() ? SETTINGS : SETTINGS_SRG);
        pre.invokestatic(Holder.ON_GUI_INIT, false);

        list.insertBefore(targetInsn, pre.build());
      }
    },
    SET_ENTRY_VALUE_PATCHER = new AbstractMethodPatcher(SET_ENTRY_VALUE) {
      @Override protected void processMethod(MethodNode targetMethod) {
        InsnListBuilder pre = new InsnListBuilder();

        pre.aload(0);
        pre.aload(0);
        pre.getfield(useMcpNames() ? SETTINGS : SETTINGS_SRG);
        pre.iload(1);
        pre.fload(2);
        pre.invokestatic(Holder.ON_GUI_SET_FLOAT_VALUE, false);

        targetMethod.instructions.insert(pre.build());
      }
    };
  }

}
