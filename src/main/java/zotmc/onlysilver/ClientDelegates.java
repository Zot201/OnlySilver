package zotmc.onlysilver;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zotmc.onlysilver.loading.Patcher.Srg;

@SideOnly(Side.CLIENT)
public class ClientDelegates {

	@Srg("func_175035_a")
	public static void renderModel(RenderItem renderItem,
			@SuppressWarnings("deprecation") net.minecraft.client.resources.model.IBakedModel model, int color) { }

	@Srg("func_175045_a")
	public static void renderModel(RenderItem renderItem,
			@SuppressWarnings("deprecation") net.minecraft.client.resources.model.IBakedModel model,
			int color, ItemStack item) { }

	@Srg("func_180451_a")
	public static void renderEffect(RenderItem renderItem,
			@SuppressWarnings("deprecation") net.minecraft.client.resources.model.IBakedModel model) { }

	@Srg("func_180452_a")
	public static void setupGuiTransform(RenderItem renderItem, int xPosition, int yPosition, boolean isGui3d) { }

}
