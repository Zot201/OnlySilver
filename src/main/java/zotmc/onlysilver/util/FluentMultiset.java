package zotmc.onlysilver.util;

import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

public class FluentMultiset<E> extends ForwardingMultiset<E> {

  private final Multiset<E> backing;

  private FluentMultiset(Multiset<E> backing) {
    this.backing = backing;
  }

  @Override protected Multiset<E> delegate() {
    return backing;
  }

  @SafeVarargs public static <E> FluentMultiset<E> of(E... a) {
    return new FluentMultiset<>(ImmutableMultiset.copyOf(a));
  }

  public static <E> FluentMultiset<E> of(E e, int count) {
    return new FluentMultiset<>(ImmutableMultiset.<E>builder().addCopies(e, count).build());
  }

  @SafeVarargs public final FluentMultiset<E> tag(E... a) {
    return new FluentMultiset<>(ImmutableMultiset.<E>builder().addAll(backing).add(a).build());
  }

  public final FluentMultiset<E> tag(E e, int count) {
    return new FluentMultiset<>(ImmutableMultiset.<E>builder().addAll(backing).addCopies(e, count).build());
  }

}
