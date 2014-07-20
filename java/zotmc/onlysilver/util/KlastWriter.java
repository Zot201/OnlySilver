package zotmc.onlysilver.util;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class KlastWriter extends ClassWriter {
	
	public final Klas<?> target, parent;

	public KlastWriter(Klas<?> target, Klas<?> parent) {
		super(COMPUTE_MAXS);
		this.target = target;
		this.parent = parent;
	}

}
