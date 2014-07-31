package zotmc.onlysilver.handler;

import static cpw.mods.fml.common.eventhandler.EventPriority.LOWEST;
import static net.minecraft.enchantment.EnchantmentHelper.addRandomEnchantment;
import static net.minecraft.entity.EntityLiving.getArmorPosition;
import static net.minecraft.init.Items.shears;
import static zotmc.onlysilver.item.Instrumentum.silverAxe;
import static zotmc.onlysilver.item.Instrumentum.silverBoots;
import static zotmc.onlysilver.item.Instrumentum.silverChest;
import static zotmc.onlysilver.item.Instrumentum.silverHelm;
import static zotmc.onlysilver.item.Instrumentum.silverHoe;
import static zotmc.onlysilver.item.Instrumentum.silverLegs;
import static zotmc.onlysilver.item.Instrumentum.silverPick;
import static zotmc.onlysilver.item.Instrumentum.silverShovel;
import static zotmc.onlysilver.item.Instrumentum.silverSword;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import zotmc.onlysilver.data.ModData.OnlySilvers;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class JoinWorldHandler {

	public static final float
	MAJOR_CHANCE = 0.03F,
	AKKA_CHANCE = 1 / 10F,
	MINOR_CHANCE = 1 / 20F;
	
	public static final String
	HANDLED_KEY = OnlySilvers.MODID + ".spawn.handled";
	

	public List<Item> defaultEquips = ImmutableList.copyOf(Akka.SOLDIER.equips());
	
	private final Random rand = new Random();
	private final boolean akkaEnabled;
	
	public JoinWorldHandler(boolean akkaEnabled) {
		this.akkaEnabled = akkaEnabled;
	}
	

	
	@SubscribeEvent(priority = LOWEST)
	public void onSpecialSpawn(SpecialSpawn event) {
		if (!event.world.isRemote && event.isCanceled()) {
			Entity entity = event.entityLiving;
			
			if (!(entity.getClass() == EntitySkeleton.class && ((EntitySkeleton) entity).getSkeletonType() == 0
					|| entity.getClass() == EntityZombie.class))
				return;
			
			entity.getEntityData().setBoolean(HANDLED_KEY, true);
		}
		
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
	
	public static ItemStack enchanting(Item item, float difficulty, Random rand) {
		ItemStack stack = new ItemStack(item);
		if (rand.nextFloat() < difficulty * (item instanceof ItemArmor ? 0.5F : 0.25F))
			addRandomEnchantment(rand, stack, 5 + (int) (difficulty * rand.nextInt(18)));
		return stack;
	}
	
	

	@SuppressWarnings("unchecked")
	public enum Akka {
		SOLDIER (silverHelm, silverChest, silverLegs, silverBoots, silverSword),
		MINER (silverHelm, silverBoots, silverPick),
		DIGGER (silverLegs, silverBoots, silverShovel),
		WOODSMAN (silverHelm, silverChest, silverAxe),
		FARMER (silverHelm, silverBoots, silverHoe),
		GARDENER (silverHelm, silverChest, Suppliers.<Item>ofInstance(shears));
		
		
		private final List<Supplier<Item>> equips;
		
		Akka(Supplier<Item>... contents) {
			equips = ImmutableList.copyOf(contents);
		}
		
		public List<Item> equips() {
			return Lists.transform(equips, Suppliers.<Item>supplierFunction());
		}
		
		public void apply(Entity entity, float factor, Random rand) {
			for (Supplier<Item> equip : equips) {
				ItemStack stack = enchanting(equip.get(), factor, rand);
				entity.setCurrentItemOrArmor(getArmorPosition(stack), stack);
			}
		}
	}
	
	
}
