package zotmc.onlysilver.data;

import java.lang.reflect.Field;

import net.minecraft.block.state.BlockWorldState;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.item.Item;
import net.minecraft.world.storage.DerivedWorldInfo;
import zotmc.onlysilver.util.Utils;

public class ReflData {

  public static final Field
  WORLD = Utils.findField(BlockWorldState.class, "world", "field_177515_a"),
  ITEM_MODIFIER_UUID = Utils.findField(Item.class, "itemModifierUUID", "field_111210_e"),
  AI_ATTACK_ON_COLLIDE = Utils.findField(EntitySkeleton.class, "aiAttackOnCollide", "field_85038_e"),
  THE_WORLD_INFO = Utils.findField(DerivedWorldInfo.class, "theWorldInfo", "field_76115_a");

}
