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

import net.minecraft.launchwrapper.Launch;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractMethodPatcher implements Patcher {

  final MethodPredicate target;

  protected AbstractMethodPatcher(MethodPredicate target) {
    this.target = target;
  }

  @Override public TypePredicate targetType() {
    return target.getOwner();
  }

  protected boolean useMcpNames() {
    return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
  }

  private void addMethod(ClassNode classNode) { }

  protected abstract void processMethod(MethodNode targetMethod);


  @Override public byte[] patch(byte[] basicClass, Logger log, boolean dev) throws Throwable {
    ClassNode classNode = new ClassNode();
    new ClassReader(basicClass).accept(classNode, 0);

    MethodNode targetNode = null;
    boolean newlyCreated = false;

    for (MethodNode methodNode : classNode.methods)
      if (target.covers(methodNode)) {
        targetNode = methodNode;
        break;
      }

    if (targetNode == null) {
      addMethod(classNode);

      for (MethodNode methodNode : classNode.methods)
        if (target.covers(methodNode)) {
          targetNode = methodNode;
          break;
        }

      if (targetNode == null) throw new MissingMethodException(target);

      log.log(dev ? Level.INFO : Level.TRACE, "Added %s", target);
      newlyCreated = true;
    }

    return processMethod(newlyCreated, classNode, targetNode, log, dev);
  }

  byte[] processMethod(boolean newlyCreated, ClassNode classNode, MethodNode targetMethod, Logger log, boolean dev) {
    processMethod(targetMethod);
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    classNode.accept(cw);

    if (!newlyCreated) log.log(dev ? Level.INFO : Level.TRACE, "Processed %s", target);
    return cw.toByteArray();
  }

}
