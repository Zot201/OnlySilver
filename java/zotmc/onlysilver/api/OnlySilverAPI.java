package zotmc.onlysilver.api;

import static java.lang.reflect.Modifier.FINAL;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import zotmc.onlysilver.Content;
import zotmc.onlysilver.Contents;

import com.google.common.base.Optional;

/**
 * This class contains duplicates of every fields exist in zotmc.onlysilver.Contents,
 * but wrapped with {@code Optional<T>}s instead of {@code Content<T>}s.
 * 
 * @author zot
 */
public class OnlySilverAPI {
	
	public static final Optional<Block>
	silverOre = Optional.absent(),
	silverBlock = Optional.absent();
	
	public static final Optional<Item>
	silverHelm = Optional.absent(),
	silverChest = Optional.absent(),
	silverLegs = Optional.absent(),
	silverBoots = Optional.absent(),
	
	silverIngot = Optional.absent(),
	silverRod = Optional.absent(),
	
	silverPick = Optional.absent(),
	silverAxe = Optional.absent(),
	silverShovel = Optional.absent(),
	silverSword = Optional.absent(),
	silverHoe = Optional.absent(),
	silverBow = Optional.absent(),
	
	silverSpear = Optional.absent(),
	silverHalberd = Optional.absent(),
	silverBattleaxe = Optional.absent(),
	silverKnife = Optional.absent(),
	silverWarhammer = Optional.absent(),
	silverFlail = Optional.absent(),
	silverKatana = Optional.absent(),
	silverBoomerang = Optional.absent(),
	silverBayonetMusket = Optional.absent();
	
	
	public static final Optional<ToolMaterial>
	toolSilver = Optional.absent();
	
	public static final Optional<ArmorMaterial>
	armorSilver = Optional.absent();
	
	
	public static final Optional<Enchantment>
	everlasting = Optional.absent(),
	incantation = Optional.absent();
	
	
	
	private static void init() {
		for (Field f : OnlySilverAPI.class.getDeclaredFields())
			try {
				Content<?> content = (Content<?>)
						Contents.class.getDeclaredField(f.getName()).get(null);
				
				if (content.exists()) {
					Field mf = Field.class.getDeclaredField("modifiers");
					mf.setAccessible(true);
					mf.setInt(f, f.getModifiers() & ~FINAL);
					
					f.set(null, Optional.of(content.get()));
				}
				
			} catch (Exception e) {
				throw new RuntimeException("Fatal Error", e);
			}
		
	}
	
	
}
