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
package zotmc.onlysilver.loading;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import zotmc.onlysilver.util.init.MethodInfo;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public final class MethodPredicate {

  private final String owner;
  private final ImmutableList<String> names;
  private final String desc;

  MethodPredicate(String owner, ImmutableList<String> names, String desc) {
    this.owner = checkNotNull(owner);
    this.names = checkNotNull(names);
    this.desc = desc;
  }

  public MethodPredicate desc(String desc) {
    checkState(this.desc == null);
    return new MethodPredicate(owner, names, desc);
  }

  TypePredicate getOwner() {
    return TypePredicate.of(owner);
  }

  MethodInfo toMethodInfo(int nameIndex) {
    checkState(desc != null);
    return MethodInfo.of(names.get(nameIndex), desc);
  }


  private boolean covers(String name, String desc) {
    FMLDeobfuscatingRemapper r = FMLDeobfuscatingRemapper.INSTANCE;
    if (!names.contains(r.mapMethodName(r.unmap(owner), name, desc))) return false;
    String s = this.desc;

    if (s != null) {
      if (s.length() <= 0 || s.charAt(0) != '(') return false;

      int i = 1;
      Type[] a = Type.getArgumentTypes(desc);

      for (Type type : a) {
        String t = ((Type) r.mapValue(type)).getDescriptor();
        if (!s.regionMatches(i, t, 0, t.length())) return false;
        i += t.length();
      }

      if (s.length() <= i || s.charAt(i) != ')') return false;
    }

    return true;
  }

  private boolean covers(String owner, String name, String desc) {
    return getOwner().covers(owner) && covers(name, desc);
  }

  @SuppressWarnings("WeakerAccess")
  public boolean covers(MethodNode node) {
    return covers(node.name, node.desc);
  }

  public boolean covers(MethodInsnNode insnNode) {
    return covers(insnNode.owner, insnNode.name, insnNode.desc);
  }

  public boolean covers(int opcode, AbstractInsnNode insnNode) {
    return insnNode.getOpcode() == opcode && covers((MethodInsnNode) insnNode);
  }


  @Override public int hashCode() {
    return Objects.hashCode(owner, names, desc);
  }

  @Override public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj instanceof MethodPredicate) {
      MethodPredicate o = (MethodPredicate) obj;
      return owner.equals(o.owner) && names.equals(o.names) && Objects.equal(desc, o.desc);
    }
    return false;
  }

  @Override public String toString() {
    return String.format("%s/%s", owner, names);
  }

}
