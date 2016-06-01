package zotmc.onlysilver.loading;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

import org.objectweb.asm.Type;

import com.google.common.collect.ImmutableList;

public final class TypePredicate {
	
	private final String name;
	
	private TypePredicate(String name) {
		this.name = checkNotNull(name);
	}
	
	public static TypePredicate of(String internalName) {
		return new TypePredicate(internalName);
	}
	
	public static TypePredicate of(Type type) {
		checkArgument(type.getSort() == Type.OBJECT);
		return new TypePredicate(type.getInternalName());
	}
	
	public MethodPredicate method(String... names) {
		return new MethodPredicate(name, ImmutableList.copyOf(names), null);
	}
	
	public MethodPredicate method(Iterable<String> names) {
		return new MethodPredicate(name, ImmutableList.copyOf(names), null);
	}
	
	
	public boolean covers(String typeDesc) {
		return name.equals(typeDesc) || FMLDeobfuscatingRemapper.INSTANCE.unmap(name).equals(typeDesc);
	}
	
	
	@Override public int hashCode() {
		return name.hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		return obj instanceof TypePredicate && name.equals(((TypePredicate) obj).name);
	}
	
	/**
	 * Mapped internal name
	 */
	@Override public String toString() {
		return name;
	}
	
}
