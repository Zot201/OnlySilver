package zotmc.onlysilver.loading;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;
import java.util.Iterator;

import net.minecraft.launchwrapper.LaunchClassLoader;

import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import zotmc.onlysilver.loading.Patcher.Delegation;
import zotmc.onlysilver.util.init.MethodInfo;

import com.google.common.collect.ObjectArrays;

class DelegationGenerator extends AbstractGenerator {

  private static final String SUFFIX = "_" + OnlyLoading.MODID;

  public DelegationGenerator(Class<?> clz, LaunchClassLoader classLoader) {
    super(clz, classLoader);
  }


  @Override protected boolean isSource(Field f) {
    return f.getAnnotation(Delegation.class) != null;
  }

  @Override protected Patcher processSource(TypePredicate source, boolean isClient) {
    return new DelegatingPatcher(source, isClient);
  }

  private static class DelegatingPatcher implements Patcher {
    private final TypePredicate target;
    private final boolean isClient;

    DelegatingPatcher(TypePredicate target, boolean isClient) {
      this.target = target;
      this.isClient = isClient;
    }

    @Override public TypePredicate targetType() {
      return target;
    }

    @Override public byte[] patch(byte[] basicClass, Logger log, boolean dev) {
      ClassNode classNode = new ClassNode();
      new ClassReader(basicClass).accept(classNode, 0);
      Iterator<MethodNode> it = classNode.methods.iterator();

      while (it.hasNext()) {
        MethodNode mn = it.next();
        if (!isStatic(mn.access) || isSynthetic(mn.access)) continue;
        if (!isClient) {
          AnnotationNode sideOnly = Tag.SIDE_ONLY.of(mn);
          if (sideOnly != null) {
            String[] a = valueOf(sideOnly, "value", String[].class);
            if (a != null && a[1].equals("CLIENT")) continue;
          }
        }

        mn.instructions.clear();
        GeneratorAdapter mg = new GeneratorAdapter(mn, mn.access, mn.name, mn.desc);
        mg.loadArgs();
        Type t = staticOwner(mn);
        mg.invokeStatic(t != null ? t : Type.getArgumentTypes(mn.desc)[0], MethodInfo.of(targetName(mn) + SUFFIX, mn.desc));
        mg.returnValue();
        mg.endMethod();
      }

      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
      classNode.accept(cw);
      return cw.toByteArray();
    }
  }


  @Override protected boolean isTarget(MethodNode mn) {
    return true;
  }

  @Override protected Patcher processTarget(MethodPredicate target, MethodPredicate source, MethodNode sourceNode) {
    return new ExposingPatcher(target);
  }

  private static class ExposingPatcher extends AbstractMethodPatcher {
    ExposingPatcher(MethodPredicate target) {
      super(target);
    }

    @Override protected void processMethod(MethodNode targetMethod) { }

    @Override byte[] processMethod(boolean created, ClassNode classNode, MethodNode targetMethod, Logger log, boolean dev) {
      boolean isStatic = isStatic(targetMethod.access);
      Type owner = Type.getObjectType(classNode.name);
      String desc = targetMethod.desc;
      Type[] args = Type.getArgumentTypes(desc);
      MethodInfo m = MethodInfo.of(target.toMethodInfo(0).getName() + SUFFIX,
          Type.getReturnType(desc), isStatic ? args : ObjectArrays.concat(owner, args));

      GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, m, null, null, classNode);
      mg.loadArgs();
      m = MethodInfo.of(targetMethod.name, desc);
      if (isStatic) mg.invokeStatic(owner, m);
      else if (isPrivate(targetMethod.access)) mg.invokeConstructor(owner, m);
      else mg.invokeVirtual(owner, m);
      mg.returnValue();
      mg.endMethod();

      log.trace("Generated delegate for %s", target);

      ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
      classNode.accept(cw);
      return cw.toByteArray();
    }
  }

}
