package zotmc.onlysilver.loading;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import zotmc.onlysilver.loading.Patcher.ClientOnly;
import zotmc.onlysilver.loading.Patcher.Hook;
import zotmc.onlysilver.loading.Patcher.Hook.Strategy;
import zotmc.onlysilver.loading.Patcher.Name;
import zotmc.onlysilver.loading.Patcher.Return;
import zotmc.onlysilver.loading.Patcher.ReturnBoolean;
import zotmc.onlysilver.loading.Patcher.Srg;
import zotmc.onlysilver.loading.Patcher.Static;

import com.google.common.base.Enums;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

abstract class AbstractGenerator implements Iterable<Patcher> {

  private final Class<?> clz;
  private final LaunchClassLoader classLoader;
  private final boolean isClient = FMLLaunchHandler.side().isClient();

  public AbstractGenerator(Class<?> clz, LaunchClassLoader classLoader) {
    this.clz = checkNotNull(clz);
    this.classLoader = checkNotNull(classLoader);
  }

  protected static boolean isSynthetic(int access) {
    return (access & Opcodes.ACC_SYNTHETIC) != 0;
  }

  protected static <T> T valueOf(AnnotationNode an, String key, Class<T> clz) {
    List<Object> pairs = an.values;
    if (pairs != null)
      for (List<Object> pair : Lists.partition(pairs, 2))
        if (key.equals(pair.get(0))) {
          Object ret = pair.get(1);
          return clz.isInstance(ret) ? clz.cast(ret) : null;
        }
    return null;
  }

  protected boolean booleanValueOf(AnnotationNode an, String key, boolean defaultValue) {
    return Objects.firstNonNull(valueOf(an, key, Boolean.class), defaultValue);
  }

  protected static <T extends Enum<T>> T enumValueOf(AnnotationNode an, String key, T defaultValue) {
    String[] a = valueOf(an, key, String[].class);
    return a != null ? Enums.getIfPresent(defaultValue.getDeclaringClass(), a[1]).or(defaultValue) : checkNotNull(defaultValue);
  }

  protected static Type staticOwner(MethodNode mn) {
    AnnotationNode an = Tag.STATIC.of(mn);
    return an == null ? null : valueOf(an, "value", Type.class);
  }

  protected static String targetName(MethodNode mn) {
    AnnotationNode name = Tag.NAME.of(mn);
    return name == null ? mn.name : valueOf(name, "value", String.class);
  }

  @Override public Iterator<Patcher> iterator() {
    if (!isClient && clz.getAnnotation(ClientOnly.class) != null) return Iterators.emptyIterator();

    return new AbstractIterator<Patcher>() {
      final Iterator<Field> fields = Iterators.forArray(clz.getDeclaredFields());
      TypePredicate source;
      Iterator<MethodNode> methods = Iterators.emptyIterator();

      @Override protected Patcher computeNext() {
        while (true) {
          if (methods.hasNext()) {
            MethodNode mn = methods.next();
            if (isSynthetic(mn.access) || !isStatic(mn.access) || !isTarget(mn)) continue;
            if (!isClient) {
              AnnotationNode sideOnly = Tag.SIDE_ONLY.of(mn);
              if (sideOnly != null) {
                String[] a = valueOf(sideOnly, "value", String[].class);
                if (a != null && a[1].equals("CLIENT")) continue;
              }
            }

            Type owner = staticOwner(mn);
            Type[] args = Type.getArgumentTypes(mn.desc);
            AnnotationNode hook = Tag.HOOK.of(mn);
            if (hook != null) {
              checkArgument(canHook());
              if (enumValueOf(hook, "value", Strategy.ARRIVAL).consuming)
                args = Arrays.copyOfRange(args, 1, args.length);
            }
            if (owner == null) {
              owner = args[0];
              args = Arrays.copyOfRange(args, 1, args.length);
            }
            Set<String> names = Sets.newLinkedHashSet();
            names.add(targetName(mn));
            AnnotationNode srg = Tag.SRG.of(mn);
            if (srg != null) {
              String s = valueOf(srg, "value", String.class);
              if (s != null) names.add(s);
            }
            String desc = Type.getMethodDescriptor(Type.getReturnType(mn.desc), args);

            MethodPredicate target = TypePredicate.of(owner).method(names).desc(desc);
            MethodPredicate source = this.source.method(mn.name).desc(mn.desc);
            return checkNotNull(processTarget(target, source, mn));
          }

          if (fields.hasNext()) {
            Field f = fields.next();
            if (!TypePredicate.class.isAssignableFrom(f.getType()) || !isSource(f)) continue;
            if (!isClient && f.getAnnotation(ClientOnly.class) != null) continue;

            try {
              f.setAccessible(true);
              ClassNode cn = new ClassNode();
              new ClassReader(classLoader.getClassBytes(f.get(null).toString().replace('/', '.'))).accept(cn, 0);

              source = TypePredicate.of(cn.name);
              methods = cn.methods.iterator();
              Patcher p = processSource(source, isClient);
              if (p != null) return p;
              continue;

            } catch (Throwable t) {
              throw Throwables.propagate(t);
            }
          }

          return endOfData();
        }
      }
    };
  }

  protected boolean canHook() { return false; }

  protected abstract boolean isSource(Field f);

  protected abstract Patcher processSource(TypePredicate source, boolean isClient);

  protected abstract boolean isTarget(MethodNode mn);

  protected abstract Patcher processTarget(MethodPredicate target, MethodPredicate source, MethodNode sourceNode);


  protected enum Tag implements Predicate<AnnotationNode> {
    SIDE_ONLY (SideOnly.class),
    HOOK (Hook.class),
    NAME (Name.class),
    SRG (Srg.class),
    RETURN (Return.class),
    RETURN_BOOLEAN (ReturnBoolean.class),
    STATIC (Static.class);

    private final String desc;
    private final boolean visible;
    private Tag(Class<? extends Annotation> clz) {
      desc = Type.getDescriptor(clz);
      Retention retention = clz.getAnnotation(Retention.class);
      visible = retention != null && retention.value() == RetentionPolicy.RUNTIME;
    }
    @Override public boolean apply(AnnotationNode input) {
      return input.desc.equals(desc);
    }
    public AnnotationNode of(MethodNode mn) {
      List<AnnotationNode> l = visible ? mn.visibleAnnotations : mn.invisibleAnnotations;
      return l == null ? null : Iterables.find(l, this, null);
    }
  }

}
