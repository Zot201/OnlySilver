package zotmc.onlysilver.data;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import zotmc.onlysilver.data.ModData.OnlySilvers;
import zotmc.onlysilver.loading.*;
import zotmc.onlysilver.loading.Patcher.Hook;

@SuppressWarnings("WeakerAccess")
public class CommonAsm {

  public static final String MODID = OnlySilvers.MODID;

  @Hook
  private static final TypePredicate
  HOOKS = TypePredicate.of("zotmc/onlysilver/CommonHooks");

  // callbacks
  static final MethodPredicate
  ENCHANTING_CONTEXT = HOOKS.method("enchantingContext")
      .desc("Ljava/lang/ThreadLocal;"),
  MODIFIER_CONTEXT = HOOKS.method("modifierContext")
      .desc("Ljava/lang/ThreadLocal;"),
  ARROW_LOOSE_CONTEXT = HOOKS.method("arrowLooseContext")
      .desc("Ljava/lang/ThreadLocal;"),
  GET_SILVER_AURA_DAMAGE_NEGATION = HOOKS.method("getSilverAuraDamageNegation")
      .desc("(Lnet/minecraft/item/ItemStack;Ljava/util/Random;)Z"),
  GET_SILVER_AURA_HARVEST_LEVEL = HOOKS.method("getSilverAuraHarvestLevel")
      .desc("(ILnet/minecraft/entity/player/EntityPlayer;)I"),
  ON_STOPPED_USING = HOOKS.method("onStoppedUsing")
      .desc("(Lnet/minecraft/item/ItemStack;)V"),
  ON_MOB_STOPPED_USING = HOOKS.method("onMobStoppedUsing")
      .desc("(Lnet/minecraft/entity/IRangedAttackMob;)V"),
  GET_PROTOTYPE = HOOKS.method("getPrototype")
      .desc("(Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;"),
  ENCHANT_SILVER_SWORD = HOOKS.method("enchantSilverSword")
      .desc("(Lnet/minecraft/entity/monster/EntitySkeleton;)V");


  static class ThreadLocals {
    // callbacks
    public static final MethodPredicate
    SET = TypePredicate.of("java/lang/ThreadLocal")
        .method("set")
        .desc("(Ljava/lang/Object;)V");
  }

  private static class Booleans {
    private static final TypePredicate TYPE = TypePredicate.of("java/lang/Boolean");

    // callbacks
    public static final MethodPredicate
    TRUE = TYPE.method("TRUE").desc("Ljava/lang/Boolean;"),
    FALSE = TYPE.method("FALSE").desc("Ljava/lang/Boolean;");
  }

  private static class IModifiers {
    // targets
    public static final MethodPredicate
    CALCULATE_MODIFIER = TypePredicate.of("net/minecraft/enchantment/EnchantmentHelper$IModifier")
        .method("calculateModifier", "func_77493_a")
        .desc("(Lnet/minecraft/enchantment/Enchantment;I)V");
  }

  public static class EnchantmentHelpers {
    private static final TypePredicate TYPE = TypePredicate.of("net/minecraft/enchantment/EnchantmentHelper");

    // targets
    private static final MethodPredicate
    ADD_RANDOM_ENCHANTMENT = TYPE.method("addRandomEnchantment", "func_77504_a")
        .desc("(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;"),
    BUILD_ENCHANTMENT_LIST = TYPE.method("buildEnchantmentList", "func_77513_b")
        .desc("(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Ljava/util/List;"),
    APPLY_ENCHANTMENT_MODIFIER = TYPE.method("applyEnchantmentModifier", "func_77518_a")
        .desc("(Lnet/minecraft/enchantment/EnchantmentHelper$IModifier;Lnet/minecraft/item/ItemStack;)V");

    // patches
    public static final Patcher
    ADD_RANDOM_ENCHANTMENT_PATCHER = new AbstractInsnPatcher(ADD_RANDOM_ENCHANTMENT) {
      /*
       * + enchantingContext.set(true);
       * + try {
       *     [buildEnchantmentList]
       * + } finally {
       * +   enchantingContext.set(false);
       * + }
       */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return BUILD_ENCHANTMENT_LIST.covers(Opcodes.INVOKESTATIC, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder(), post = new InsnListBuilder();
        Label l0 = new Label(), l1 = new Label(), l2 = new Label();

        pre.tryCatchBlock(l0, l1, l1);
        pre.getstatic(ENCHANTING_CONTEXT);
        pre.getstatic(Booleans.TRUE);
        pre.invokevirtual(ThreadLocals.SET, false);
        pre.mark(l0);

        post.goTo(l2);
        post.mark(l1);
        post.getstatic(ENCHANTING_CONTEXT);
        post.getstatic(Booleans.FALSE);
        post.invokevirtual(ThreadLocals.SET, false);
        post.athrow();
        post.mark(l2);
        post.getstatic(ENCHANTING_CONTEXT);
        post.getstatic(Booleans.FALSE);
        post.invokevirtual(ThreadLocals.SET, false);

        list.insertBefore(targetInsn, pre.build());
        list.insert(targetInsn, post.build());
      }
    },
    APPLY_ENCHANTMENT_MODIFIER_PATCHER = new AbstractInsnPatcher(APPLY_ENCHANTMENT_MODIFIER) {
      /*
       * + modifierContext.set(stack);
       * + try {
       *     [calculateModifier]
       * + } finally {
       * +   modifierContext.set(null);
       * + }
       */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return IModifiers.CALCULATE_MODIFIER.covers(Opcodes.INVOKEINTERFACE, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder(), post = new InsnListBuilder();
        Label l0 = new Label(), l1 = new Label(), l2 = new Label();

        pre.tryCatchBlock(l0, l1, l1);
        pre.getstatic(MODIFIER_CONTEXT);
        pre.aload(1);
        pre.invokevirtual(ThreadLocals.SET, false);
        pre.mark(l0);

        post.goTo(l2);
        post.mark(l1);
        post.getstatic(MODIFIER_CONTEXT);
        post.aconst(null);
        post.invokevirtual(ThreadLocals.SET, false);
        post.athrow();
        post.mark(l2);
        post.getstatic(MODIFIER_CONTEXT);
        post.aconst(null);
        post.invokevirtual(ThreadLocals.SET, false);

        list.insertBefore(targetInsn, pre.build());
        list.insert(targetInsn, post.build());
      }
    };
  }

  private static class EnchantmentDurabilitys {
    // targets
    public static final MethodPredicate
    NEGATE_DAMAGE = TypePredicate.of("net/minecraft/enchantment/EnchantmentDurability")
        .method("negateDamage", "func_92097_a")
        .desc("(Lnet/minecraft/item/ItemStack;ILjava/util/Random;)Z");
  }

  public static class ItemStacks {
    private static final TypePredicate TYPE = TypePredicate.of("net/minecraft/item/ItemStack");

    // targets
    private static final MethodPredicate
    ATTEMPT_DAMAGE_ITEM = TYPE.method("attemptDamageItem", "func_96631_a")
        .desc("(ILjava/util/Random;)Z"),
    GET_ITEM = TYPE.method("getItem", "func_77973_b")
        .desc("()Lnet/minecraft/item/Item;"),
    ON_PLAYER_STOPPED_USING = TYPE.method("onPlayerStoppedUsing", "func_77974_b")
        .desc("(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;I)V");

    // patches
    public static final Patcher
    ATTEMPT_DAMAGE_ITEM_PATCHER = new AbstractInsnPatcher(ATTEMPT_DAMAGE_ITEM) {
      /*
       * - if (negateDamage(...))
       * + if (negateDamage(...) || getSilverAuraDamageNegation(this, rand))
       */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return EnchantmentDurabilitys.NEGATE_DAMAGE.covers(Opcodes.INVOKESTATIC, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder post = new InsnListBuilder();
        Label l0 = new Label();

        post.dup();
        post.ifne(l0);
        post.pop();
        post.aload(0);
        post.aload(2);
        post.invokestatic(GET_SILVER_AURA_DAMAGE_NEGATION, false);
        post.mark(l0);

        list.insert(targetInsn, post.build());
      }
    },
    ON_PLAYER_STOPPED_USING_PATCHER = new AbstractInsnPatcher(ON_PLAYER_STOPPED_USING) {
      /*
       * + try {
       * +   onStoppedUsing(this);
       *     [onPlayerStoppedUsing]
       * + } finally {
       * +   arrowLooseContext.set(null);
       * + }
       */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return Itemss.ON_PLAYER_STOPPED_USING.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder(), post = new InsnListBuilder();
        Label l0 = new Label(), l1 = new Label(), l2 = new Label();

        pre.tryCatchBlock(l0, l1, l1);
        pre.mark(l0);
        pre.aload(0);
        pre.invokestatic(ON_STOPPED_USING, false);

        post.goTo(l2);
        post.mark(l1);
        post.getstatic(ARROW_LOOSE_CONTEXT);
        post.aconst(null);
        post.invokevirtual(ThreadLocals.SET, false);
        post.athrow();
        post.mark(l2);
        post.getstatic(ARROW_LOOSE_CONTEXT);
        post.aconst(null);
        post.invokevirtual(ThreadLocals.SET, false);

        list.insertBefore(targetInsn, pre.build());
        list.insert(targetInsn, post.build());
      }
    };
  }

  private static class Itemss {
    private static final TypePredicate TYPE = TypePredicate.of("net/minecraft/item/Item");

    // targets
    public static final MethodPredicate
    GET_HARVEST_LEVEL = TYPE.method("getHarvestLevel")
        .desc("(Lnet/minecraft/item/ItemStack;Ljava/lang/String;)I"),
    ON_PLAYER_STOPPED_USING = TYPE.method("onPlayerStoppedUsing", "func_77615_a")
        .desc("(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;I)V");
  }

  public static class ForgeHookss {
    // targets
    private static final MethodPredicate
    CAN_HARVEST_BLOCK = TypePredicate.of("net/minecraftforge/common/ForgeHooks")
        .method("canHarvestBlock")
        .desc("(Lnet/minecraft/block/Block;Lnet/minecraft/entity/player/EntityPlayer;" +
            "Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Z");

    // patches
    public static final Patcher
    CAN_HARVEST_BLOCK_PATCHER = new AbstractInsnPatcher(CAN_HARVEST_BLOCK) {
      /*
       * - ...getHarvestLevel(...)
       * + getSilverAuraHarvestLevel(...getHarvestLevel(...), player)
       */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return Itemss.GET_HARVEST_LEVEL.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder post = new InsnListBuilder();

        post.aload(1);
        post.invokestatic(GET_SILVER_AURA_HARVEST_LEVEL, false);

        list.insert(targetInsn, post.build());
      }
    };
  }

  private static class EntityLivings {
    // targets
    public static final MethodPredicate
    SET_ENCHANTMENT_BASED_ON_DIFFICULTY = TypePredicate.of("net/minecraft/entity/EntityLiving")
        .method("setEnchantmentBasedOnDifficulty", "func_180483_b")
        .desc("(Lnet/minecraft/world/DifficultyInstance;)V");
  }

  public static class EntitySkeletons {
    private static final TypePredicate TYPE = TypePredicate.of("net/minecraft/entity/monster/EntitySkeleton");

    // targets
    private static final MethodPredicate
    SET_COMBAT_TASK = TYPE.method("setCombatTask", "func_85036_m")
        .desc("()V"),
    ON_INITIAL_SPAWN = TYPE.method("onInitialSpawn", "func_180482_a")
        .desc("(Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/entity/IEntityLivingData;)" +
            "Lnet/minecraft/entity/IEntityLivingData;");

    // patches
    public static final Patcher
    SET_COMBAT_TASK_PATCHER = new AbstractInsnPatcher(SET_COMBAT_TASK) {
      /*
       * - ...getItem()
       * + getPrototype(...getItem())
       */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return ItemStacks.GET_ITEM.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder post = new InsnListBuilder();

        post.invokestatic(GET_PROTOTYPE, false);

        list.insert(targetInsn, post.build());
      }
    },
    ON_INITIAL_SPAWN_PATCHER = new AbstractInsnPatcher(ON_INITIAL_SPAWN) {
      /*
       *   setEnchantmentBasedOnDifficulty(...);
       * + enchantSilverSword(this);
      */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return EntityLivings.SET_ENCHANTMENT_BASED_ON_DIFFICULTY.covers(Opcodes.INVOKEVIRTUAL, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder post = new InsnListBuilder();

        post.aload(0);
        post.invokestatic(ENCHANT_SILVER_SWORD, false);

        list.insert(targetInsn, post.build());
      }
    };
  }

  private static class IRangedAttackMobs {
    // targets
    public static final MethodPredicate
    ATTACK_ENTITY_WITH_RANGED_ATTACK = TypePredicate.of("net/minecraft/entity/IRangedAttackMob")
        .method("attackEntityWithRangedAttack", "func_82196_d")
        .desc("(Lnet/minecraft/entity/EntityLivingBase;F)V");
  }

  public static class EntityAIAttackRangedBows {
    // targets
    private static final MethodPredicate
    UPDATE_TASKS = TypePredicate.of("net/minecraft/entity/ai/EntityAIAttackRangedBow")
        .method("updateTask", "func_75246_d")
        .desc("()V");

    // patches
    public static final Patcher
    UPDATE_TASKS_PATCHER = new AbstractInsnPatcher(UPDATE_TASKS) {
      /*
       * + try {
       * +   onMobStoppedUsing(rangedAttackEntityHost);
       *     [attackEntityWithRangedAttack]
       * + } finally {
       * +   arrowLooseContext.set(null);
       * + }
       */

      @Override protected boolean isTargetInsn(AbstractInsnNode insnNode) {
        return IRangedAttackMobs.ATTACK_ENTITY_WITH_RANGED_ATTACK.covers(Opcodes.INVOKEINTERFACE, insnNode);
      }

      @Override protected void processInsn(InsnList list, AbstractInsnNode targetInsn) {
        InsnListBuilder pre = new InsnListBuilder(), post = new InsnListBuilder();
        Label l0 = new Label(), l1 = new Label(), l2 = new Label();

        pre.tryCatchBlock(l0, l1, l1);
        pre.mark(l0);
        pre.dup2X1();
        pre.pop2();
        pre.dupX2();
        pre.invokestatic(ON_MOB_STOPPED_USING, false);

        post.goTo(l2);
        post.mark(l1);
        post.getstatic(ARROW_LOOSE_CONTEXT);
        post.aconst(null);
        post.invokevirtual(ThreadLocals.SET, false);
        post.athrow();
        post.mark(l2);
        post.getstatic(ARROW_LOOSE_CONTEXT);
        post.aconst(null);
        post.invokevirtual(ThreadLocals.SET, false);

        list.insertBefore(targetInsn, pre.build());
        list.insert(targetInsn, post.build());
      }
    };
  }

}
