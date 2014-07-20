package zotmc.onlysilver.entity;

import static zotmc.onlysilver.Contents.silverIngot;
import static zotmc.onlysilver.OnlySilver.MODID;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class EntitySilverGolem extends EntityIronGolem {

	public EntitySilverGolem(World world) {
		super(world);
	}
	
	@Override public void onLivingUpdate() {
		super.onLivingUpdate();
		
		removePotionEffect(Potion.poison.id);
	}
	
	@Override protected void applyEntityAttributes() {
		super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(75);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
	}
	
	@Override public EntityItem dropItem(Item item, int stackSize) {
		if (item == Items.iron_ingot)
			return super.dropItem(silverIngot.get(), stackSize);
		return super.dropItem(item, stackSize);
	}
	
	
	@Override protected String getHurtSound() {
		return MODID + ":silvergolem.hit";
	}
	
	@Override protected String getDeathSound() {
		return MODID + ":silvergolem.death";
	}

	//playStepSound
	@Override protected void func_145780_a(int par1, int par2, int par3, Block block) {
		super.playSound("mob.irongolem.walk", 1.3322326644653289306578613157226F, 1);
	}
	
	@Override public void playSound(String sound, float volume, float pitch) {
		if (!sound.equals("mob.irongolem.throw"))
			volume *= 1.6271853423385485683250586869705F;
		
		super.playSound(sound, volume, pitch);
	}

}
