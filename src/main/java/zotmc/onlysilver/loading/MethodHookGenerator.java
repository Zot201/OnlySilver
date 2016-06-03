package zotmc.onlysilver.loading;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;

import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import zotmc.onlysilver.loading.Patcher.Hook;
import zotmc.onlysilver.loading.Patcher.Hook.Strategy;

class MethodHookGenerator extends AbstractGenerator {

  MethodHookGenerator(Class<?> clz, LaunchClassLoader classLoader) {
    super(clz, classLoader);
  }

  @Override protected boolean canHook() {
    return true;
  }


  @Override protected boolean isSource(Field f) {
    return f.getAnnotation(Hook.class) != null;
  }

  @Override protected Patcher processSource(TypePredicate source, boolean isClient) {
    return null;
  }


  @Override protected boolean isTarget(MethodNode mn) {
    return Tag.HOOK.of(mn) != null;
  }

  @Override protected Patcher processTarget(MethodPredicate target, MethodPredicate source, MethodNode sourceNode) {
    AnnotationNode hook = Tag.HOOK.of(sourceNode);
    Strategy strategy = enumValueOf(hook, "value", Strategy.ARRIVAL);
    boolean dup = strategy.consuming;
    Conditional cond = null;

    AnnotationNode v = Tag.RETURN.of(sourceNode);
    if (v != null) {
      //checkArgument(cond == null);
      cond = new Conditional(booleanValueOf(v, "condition", false));
    }

    AnnotationNode z = Tag.RETURN_BOOLEAN.of(sourceNode);
    if (z != null) {
      checkArgument(cond == null);
      cond = new ObjectConditional(booleanValueOf(z, "condition", false), booleanValueOf(z, "value", false));
    }

    Type returnType = Type.getReturnType(sourceNode.desc);
    if (cond != null) {
      checkArgument(returnType.equals(Type.BOOLEAN_TYPE));
    }
    else if (dup && !returnType.equals(Type.VOID_TYPE)) {
      dup = false;
      Type[] a = Type.getArgumentTypes(sourceNode.desc);
      checkArgument(a.length >= 1 && a[0].equals(returnType));
    }

    return new HookInvocationPatcher(target, source, strategy, dup, cond);
  }

  private static class HookInvocationPatcher extends AbstractMethodPatcher {
    final MethodPredicate source;
    final Strategy strategy;
    final boolean dup;
    final Conditional cond;

    HookInvocationPatcher(MethodPredicate target, MethodPredicate source, Strategy strategy, boolean dup, Conditional cond) {
      super(target);
      this.source = checkNotNull(source);
      this.strategy = checkNotNull(strategy);
      this.dup = dup;
      this.cond = cond;
    }

    @Override protected void processMethod(MethodNode targetMethod) { }

    @Override byte[] processMethod(boolean created, ClassNode classNode, MethodNode targetMethod, Logger log, boolean dev) {
      if (strategy == Strategy.ARRIVAL) {
        targetMethod.instructions.insert(getInsns(targetMethod));

        log.log(dev ? Level.INFO : Level.TRACE, "Processed %s", target);
      }
      else {
        int count = 0;
        int returnOpcode = Type.getReturnType(targetMethod.desc).getOpcode(Opcodes.IRETURN);
        for (AbstractInsnNode insn : targetMethod.instructions.toArray())
          if (insn.getOpcode() == returnOpcode) {
            targetMethod.instructions.insertBefore(insn, getInsns(targetMethod));
            count++;
          }

        log.log(dev ? Level.INFO : Level.TRACE, "Processed %d insn%s in %s", count, count == 1 ? "" : "s", target);
      }

      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
      classNode.accept(cw);
      return cw.toByteArray();
    }

    private InsnList getInsns(MethodNode mn) {
      InsnListBuilder mg0 = new InsnListBuilder();
      GeneratorAdapter mg1 = new GeneratorAdapter(mg0, mn.access, mn.name, mn.desc);

      if (dup) mg0.dup();
      if (!isStatic(mn.access)) mg1.loadThis();
      mg1.loadArgs();
      mg0.invokestatic(source, false);
      if (cond != null) {
        Label l0 = new Label();
        mg1.ifZCmp(cond.condition ? Opcodes.IFEQ : Opcodes.IFNE, l0);
        if (dup) mg0.pop();
        cond.push(mg0);
        mg1.returnValue();
        mg0.mark(l0);
      }

      return mg0.build();
    }
  }

  private static class Conditional {
    final boolean condition;
    Conditional(boolean condition) {
      this.condition = condition;
    }
    void push(InsnListBuilder mg) { }
  }

  private static class ObjectConditional extends Conditional {
    final Object value;
    ObjectConditional(boolean condition, Object value) {
      super(condition);
      this.value = value;
    }
    @Override void push(InsnListBuilder mg) {
      mg.push(value);
    }
  }

}
