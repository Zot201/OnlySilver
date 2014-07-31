package zotmc.onlysilver.data;

import static zotmc.onlysilver.Contents.toolSilver;
import net.minecraft.client.renderer.texture.IIconRegister;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

import zotmc.onlysilver.item.ItemOnlyIngot;
import zotmc.onlysilver.util.Consumer;
import zotmc.onlysilver.util.KlastWriter;
import zotmc.onlysilver.util.MethodInfo;
import zotmc.onlysilver.util.Utils;

import com.google.common.base.Supplier;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class AsmData implements Opcodes {
	
	public static class Clients {
		
		private static final Supplier<Boolean>
		IS_CLIENT_SIDE = new Supplier<Boolean>() {
			@Override public Boolean get() {
				return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
			}
		};
		
		public static final Consumer<KlastWriter>
		REGISTER_ICONS = Utils.conditional(IS_CLIENT_SIDE, new Consumer<KlastWriter>() {
			@Override public void accept(KlastWriter cw) {
				GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC,
						ClientData.REGISTER_ICONS, null, null, cw
				);
				mg.loadThis();
				mg.loadArgs();
				mg.loadThis();
				mg.invokeVirtual(
						cw.target.toType(),
						ClientData.GET_ICON_STRING
				);
				mg.invokeInterface(
						Type.getType(IIconRegister.class),
						ClientData.REGISTER_ICON
				);
				mg.putField(
						cw.target.toType(),
						ClientData.ITEM_ICON.getName(),
						Type.getType(ClientData.ITEM_ICON.getType())
				);
				mg.returnValue();
				mg.endMethod();
			}
		});
		
	}
	
	public static final Consumer<KlastWriter>
	GET_IS_REPAIRABLE = new Consumer<KlastWriter>() {
		@Override public void accept(KlastWriter cw) {
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC,
					ObfData.GET_IS_REPAIRABLE, null, null, cw
			);
			mg.loadArg(1);
			mg.invokeStatic(
					Type.getType(ItemOnlyIngot.class),
					MethodInfo.of("isSilverIngot", "(Lnet/minecraft/item/ItemStack;)Z")
			);
			mg.returnValue();
			mg.endMethod();
		}
	},
	GET_ITEM_ENCHANTABILITY = new Consumer<KlastWriter>() {
		@Override public void accept(KlastWriter cw) {
			GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC,
					ObfData.GET_ITEM_ENCHANTABILITY, null, null, cw
			);
			mg.push(toolSilver.get().getEnchantability());
			mg.returnValue();
			mg.endMethod();
		}
	};

}
