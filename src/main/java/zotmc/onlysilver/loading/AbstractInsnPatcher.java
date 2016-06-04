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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractInsnPatcher extends AbstractMethodPatcher {

  protected AbstractInsnPatcher(MethodPredicate target) {
    super(target);
  }

  protected abstract boolean isTargetInsn(AbstractInsnNode insnNode);

  protected abstract void processInsn(InsnList list, AbstractInsnNode targetInsn);

  @Override protected void processMethod(MethodNode targetMethod) { }


  @Override byte[] processMethod(boolean newlyCreated, ClassNode classNode, MethodNode targetMethod, Logger log, boolean dev) {
    processMethod(targetMethod);
    InsnList list = targetMethod.instructions;

    int count = 0;
    for (AbstractInsnNode insnNode : list.toArray())
      if (isTargetInsn(insnNode)) {
        processInsn(list, insnNode);
        count++;
      }

    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    classNode.accept(cw);

    if (!newlyCreated)
      log.log(dev ? Level.INFO : Level.TRACE, "Processed %d insn%s in %s", count, count == 1 ? "" : "s", target);
    return cw.toByteArray();
  }

}
