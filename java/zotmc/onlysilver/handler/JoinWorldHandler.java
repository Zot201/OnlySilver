package zotmc.onlysilver.handler;

import static net.minecraft.enchantment.EnchantmentHelper.addRandomEnchantment;
import static net.minecraft.entity.EntityLiving.getArmorPosition;
import static net.minecraft.init.Items.shears;
import static zotmc.onlysilver.Contents.silverAxe;
import static zotmc.onlysilver.Contents.silverBoots;
import static zotmc.onlysilver.Contents.silverChest;
import static zotmc.onlysilver.Contents.silverHelm;
import static zotmc.onlysilver.Contents.silverHoe;
import static zotmc.onlysilver.Contents.silverLegs;
import static zotmc.onlysilver.Contents.silverPick;
import static zotmc.onlysilver.Contents.silverShovel;
import static zotmc.onlysilver.Contents.silverSword;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import zotmc.onlysilver.Content;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class JoinWorldHandler {

	public static final float
	MAJOR_CHANCE = 0.03F,
	AKKA_CHANCE = 1 / 10F,
	MINOR_CHANCE = 1 / 20F;
	
	public static final String
	HANDLED_KEY = "onlysilver.spawn.handled";
	

	public List<Item> defaultEquips = ImmutableList.copyOf(Akka.SOLDIER.equips());
	
	private final Random rand = new Random();
	private final boolean akkaEnabled;
	
	public JoinWorldHandler(boolean akkaEnabled) {
		this.akkaEnabled = akkaEnabled;
	}
	
	
	
	@SubscribeEvent public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.world.isRemote)
			return;
		
		Entity entity = event.entity;
		if (!(entity.getClass() == EntitySkeleton.class && ((EntitySkeleton) entity).getSkeletonType() == 0
				|| entity.getClass() == EntityZombie.class)
				|| entity.getEntityData().getBoolean(HANDLED_KEY))
			return;
		entity.getEntityData().setBoolean(HANDLED_KEY, true);
		
		if (rand.nextFloat() >= MAJOR_CHANCE)
			return;
		
		float factor = entity.worldObj.func_147462_b(entity.posX, entity.posY, entity.posZ);
		
		if (akkaEnabled && rand.nextFloat() < AKKA_CHANCE)
			if (entity.getClass() == EntitySkeleton.class)
				Akka.SOLDIER.apply(entity, factor, rand);
			else
				Akka.values()[1 + rand.nextInt(5)].apply(entity, factor, rand);
		
		for (Item item : defaultEquips)
			if (rand.nextFloat() < MINOR_CHANCE) {
				ItemStack stack = enchanting(item, factor, rand);
				entity.setCurrentItemOrArmor(getArmorPosition(stack), stack);
			}
		
		
	}
	
	public static ItemStack enchanting(Item item, float factor, Random rand) {
		ItemStack stack = new ItemStack(item);
		if (rand.nextFloat() < factor * (item instanceof ItemArmor ? 0.5F : 0.25F))
			addRandomEnchantment(rand, stack, 5 + (int) (factor * rand.nextInt(18)));
		return stack;
	}
	
	

	@SuppressWarnings("unchecked")
	public enum Akka {
		SOLDIER (silverHelm, silverChest, silverLegs, silverBoots, silverSword),
		MINER (silverHelm, silverBoots, silverPick),
		DIGGER (silverLegs, silverBoots, silverShovel),
		WOODSMAN (silverHelm, silverChest, silverAxe),
		FARMER (silverHelm, silverBoots, silverHoe),
		GARDENER (silverHelm, silverChest, Content.<Item>of(shears));
		
		
		private final List<Content<Item>> equips;
		
		Akka(Content<Item>... contents) {
			equips = ImmutableList.copyOf(contents);
		}
		
		public List<Item> equips() {
			return Lists.transform(equips, Content.<Item>unwrap());
		}
		
		public void apply(Entity entity, float factor, Random rand) {
			for (Content<Item> equip : equips) {
				ItemStack stack = enchanting(equip.get(), factor, rand);
				entity.setCurrentItemOrArmor(getArmorPosition(stack), stack);
			}
		}
	}
	
	
}
