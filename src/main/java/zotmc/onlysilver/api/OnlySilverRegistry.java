package zotmc.onlysilver.api;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Queue;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

public class OnlySilverRegistry {

  static final ListMultimap<String, DamageSourceHandler> damageSourceHandlers = ArrayListMultimap.create();

  /**
   * Register your own {@link DamageSourceHandler} to help OnlySilver determining the actual item in use from a DamageSource.
   * Currently, it affects how enchantments and werewolf effectiveness works.
   *
   * <p>The {@link DamageSourceHandler#getTargetDamageTypes} method is called only once on registration.
   *
   * @see OnlySilverUtils#getAttackItem
   */
  public static void registerDamageSourceHandler(DamageSourceHandler handler) {
    for (String damageType : handler.getTargetDamageTypes())
      damageSourceHandlers.put(checkNotNull(damageType), handler);
  }


  static final Queue<Predicate<? super ItemStack>> silverPredicates = Queues.newArrayDeque();

  /**
   * Register your own {@linkplain Predicate predicate} to help OnlySilver determining whether or not an item is made from silver.
   * The current purpose for this method is to determine whether an instrument should have silver enchantments.
   *
   * <p>At runtime use of {@link OnlySilverUtils#isSilveryEquip}, registered {@linkplain Predicate predicates}
   * are applied one by one until they have been all applied or once {@code true} is returned. (Short circuit evaluation)
   *
   * <p>{@linkplain Predicate Predicates} are guaranteed to have non-{@code null} input.
   *
   * @see OnlySilverUtils#isSilveryEquip
   */
  public static void registerSilverPredicate(Predicate<? super ItemStack> silverPredicate) {
    silverPredicates.add(silverPredicate);
  }


  static final Map<Item, Function<? super ItemStack, Float>> werewolfDamages = Maps.newIdentityHashMap();

  /**
   * Register your own damage {@linkplain Function function} to calculate the amount of damage to be inflicted on a werewolf,
   * with respect to the {@linkplain ItemStack item} used.
   * The first argument act as a lookup key for the registered {@linkplain Function functions} at runtime,
   * and the damage {@linkplain Function function} being looked up do the actual calculation with the actual {@link ItemStack}.
   *
   * <p>Each {@link Item} can only be mapped to a single {@linkplain Function function}.
   * A {@code null} output of function is allowed to indicate no changes to the raw damage amount.
   *
   * @see OnlySilverUtils#getWerewolfDamage
   */
  public static void registerWerewolfDamage(Item item, Function<? super ItemStack, Float> damageFunction) {
    werewolfDamages.put(checkNotNull(item), checkNotNull(damageFunction));
  }

}
