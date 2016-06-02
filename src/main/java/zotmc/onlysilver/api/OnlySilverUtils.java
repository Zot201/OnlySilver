package zotmc.onlysilver.api;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * A utility class that provide access to OnlySilver features / functions. Fields are substituted upon the dispatch of
 * {@link FMLPreInitializationEvent}, through reflection.
 * 
 * <p>There are several common categories that are NOT included in this class (with respective replacements after '-'):<ul>
 * <li>{@link Item} or {@link Block} - {@link ObjectHolder} or methods in {@link GameRegistry} / {@link GameData}
 * <li>{@link Enchantment} - {@link Enchantment#getEnchantmentByLocation}
 * </ul>
 * 
 * <p>You are recommended to handle the case if parts of the features do not present, i.e. disabled in config or lacking dependency.
 * {@link Optional}s can be tested using {@link Optional#isPresent}, and most of else can be tested with null inequality.
 * 
 * @author Zot
 */
public class OnlySilverUtils {

  public static final Optional<ToolMaterial> silverToolMaterial = Optional.absent();
  public static final Optional<ArmorMaterial> silverArmorMaterial = Optional.absent();
  public static final Optional<Achievement> buildSilverBow = Optional.absent();
  private static final BiConsumer<ItemStack, Runnable> applySilverAura = null;

  /**
   * Give a precise representation for an item involved in an attack.
   * Its functionality depends on {@link OnlySilverRegistry#registerDamageSourceHandler}.
   *
   * <p>Note that the returned object is single use. You should not cached it over ticks generally.
   *
   * @param damage a {@link DamageSource} representation of an attack
   * @return a precise representation of the item involved in the attack, null if the attack does not involve an item
   *
   * @see OnlySilverRegistry#registerDamageSourceHandler
   */
  public static AttackItem getAttackItem(DamageSource damage) {
    for (DamageSourceHandler handler : OnlySilverRegistry.damageSourceHandlers.get(damage.getDamageType())) {
      ItemStack item = handler.getItem(damage);
      if (item != null) return new AttackItem(handler, damage, item);
    }
    return null;
  }

  /**
   * Check whether or not an equipment (tool or armor) is made from silver like substances.
   * The current purpose for this method is to determine whether an instrument should have silver enchantments.
   *
   * @see OnlySilverRegistry#registerSilverPredicate
   */
  public static boolean isSilverEquip(ItemStack item) {
    if (item != null)
      for (Predicate<? super ItemStack> p : OnlySilverRegistry.silverPredicates)
        if (p.apply(item))
          return true;
    return false;
  }

  /**
   * Give the damage to be inflicted on a werewolf according to the item used.
   *
   * @param item an ItemStack represents the item in use
   * @param original the original amount of damage before werewolf damage applied, usually 1
   * @return the werewolf damage, or the original value if no applicable damage functions present.
   *
   * @see OnlySilverRegistry#registerWerewolfDamage
   */
  public static float getWerewolfDamage(ItemStack item, float original) {
    if (item != null) {
      Function<? super ItemStack, Float> function = OnlySilverRegistry.werewolfDamages.get(item.getItem());
      if (function != null) {
        Float ret = function.apply(item);
        if (ret != null) return ret;
      }
    }
    return original;
  }

  /**
   * Apply the attack damage bonus of the Silver Aura enchantment for an attack action. The attack action passed in
   * this method could be any process that involve spawning of {@link EntityArrow}(s) to any {@link World}s.
   * Strength of arrows is enhanced through {@link EntityArrow#getDamage} and {@link EntityArrow#setDamage}.
   *
   * <p><i>Note: Any attack action that is implemented with {@link Item#onPlayerStoppedUsing}, or is a call of
   * {@link IRangedAttackMob#attackEntityWithRangedAttack} from the <b>unoverriden</b> version of
   * {@link EntityAIArrowAttack#updateTask}, is handled by default.</i>
   *
   * @param bowItem a item that may hold the Silver Aura enchantment
   * @param attackAction an attack action that involves spawning of arrows
   */
  public static void applySilverAuraToArrows(ItemStack bowItem, Runnable attackAction) {
    applySilverAura.accept(bowItem, attackAction);
  }

}
