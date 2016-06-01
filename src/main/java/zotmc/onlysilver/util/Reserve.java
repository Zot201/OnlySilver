package zotmc.onlysilver.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Optional;

public class Reserve<T> implements Feature<T> {

	private T reference;
	private Reserve() { }
	
	public static <T> Reserve<T> absent() {
		return new Reserve<T>();
	}
	
	
	@Override public boolean exists() {
		return reference != null;
	}
	
	@Override public T get() {
		checkState(exists());
		return reference;
	}
	
	public T orNull() {
		return reference;
	}
	
	public Reserve<T> set(T reference) {
		checkState(!exists());
		this.reference = checkNotNull(reference);
		return this;
	}
	
	public Optional<T> toOptional() {
		return Optional.fromNullable(orNull());
	}
	
}
