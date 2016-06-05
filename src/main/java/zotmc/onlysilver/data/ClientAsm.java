package zotmc.onlysilver.data;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import zotmc.onlysilver.data.CommonAsm.ThreadLocals;
import zotmc.onlysilver.loading.*;
import zotmc.onlysilver.loading.Patcher.ClientOnly;
import zotmc.onlysilver.loading.Patcher.Delegation;
import zotmc.onlysilver.loading.Patcher.Hook;

@SuppressWarnings("WeakerAccess")
@ClientOnly
public class ClientAsm {

  @Delegation
  public static final TypePredicate
  DELEGATES = TypePredicate.of("zotmc/onlysilver/ClientDelegates");

  @Hook
  private static final TypePredicate
  HOOKS = TypePredicate.of("zotmc/onlysilver/ClientHooks");

  // callbacks
  static final MethodPredicate
  RENDER_SILVER_AURA = HOOKS.method("renderSilverAura")
      .desc("(Lnet/minecraft/client/renderer/entity/RenderItem;Lnet/minecraft/client/resources/model/IBakedModel;" +
          "Lnet/minecraft/item/ItemStack;)Z"),
  RENDER_ARMOR_CONTEXT = HOOKS.method("renderArmorContext")
      .desc("Ljava/lang/ThreadLocal;");


  public static class RenderItems {
    // type
    private static final TypePredicate TYPE = TypePredicate.of("net/minecraft/client/renderer/RenderItem");

    // targets
    private static final MethodPredicate
    RENDER_ITEM = TYPE.method("renderItem", "func_180454_a")
        .desc("(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V"),
    RENDER_EFFECT = TYPE.method("renderEffect", "func_180451_a")
        .desc("(Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");

    // patches
    public static final Patcher
    RENDER_ITEM_PATCHER = new AbstractInsnPatcher(RENDER_ITEM) {
      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return RENDER_EFFECT.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder(), post = new InsnListBuilder();
        Label l0 = new Label();

        pre.aload(1);
        pre.invokestatic(RENDER_SILVER_AURA, false);
        pre.ifne(l0);
        pre.aload(0);
        pre.aload(2);

        post.mark(l0);

        list.insertBefore(targetInsn, pre.build());
        list.insert(targetInsn, post.build());
      }
    };
  }

  public static class LayerArmorBases {
    // type
    private static final TypePredicate TYPE = TypePredicate.of(
        "net/minecraft/client/renderer/entity/layers/LayerArmorBase");

    // targets
    private static final MethodPredicate
    RENDER_ARMOR_LAYER = TYPE.method("renderArmorLayer", "func_188361_a")
        .desc("(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V"),
    RENDER_ENCHANTED_GLINT = TYPE.method("renderEnchantedGlint", "func_188364_a")
        .desc("(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;" +
            "Lnet/minecraft/client/model/ModelBase;FFFFFFF)V");

    // patches
    public static final Patcher
    RENDER_LAYER_PATCHER = new AbstractInsnPatcher(RENDER_ARMOR_LAYER) {
      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return RENDER_ENCHANTED_GLINT.covers(Opcodes.INVOKESTATIC, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder();

        pre.getstatic(RENDER_ARMOR_CONTEXT);
        pre.aload(9);
        pre.invokevirtual(ThreadLocals.SET, false);

        list.insertBefore(targetInsn, pre.build());
      }
    };
  }

}
