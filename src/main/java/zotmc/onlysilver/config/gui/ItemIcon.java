package zotmc.onlysilver.config.gui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.logging.log4j.Logger;

import zotmc.onlysilver.OnlySilver;
import zotmc.onlysilver.util.ClientUtils;

import com.google.common.base.Supplier;

public class ItemIcon extends Icon<ItemIcon> implements Supplier<String> {

	private static final Logger log = OnlySilver.INSTANCE.log;
	private final ItemStack item;
	private final float f;
	private boolean renderEffect = true;
	private boolean isItemValid = true;

	public ItemIcon(Block b) {
		this(b, 1);
	}
	public ItemIcon(Item i) {
		this(i, 1);
	}
	public ItemIcon(ItemStack item) {
		this(item, 1);
	}

	public ItemIcon(Block b, float f) {
		this(new ItemStack(b), f);
	}
	public ItemIcon(Item i, float f) {
		this(new ItemStack(i), f);
	}
	public ItemIcon(ItemStack item, float f) {
		checkNotNull(item.getItem());
		this.item = item;
		this.f = f;
	}
	
	public ItemIcon setRenderEffect(boolean renderEffect) {
		this.renderEffect = renderEffect;
		return this;
	}
	
	@Override protected boolean common() {
		return item.getRarity() == EnumRarity.COMMON;
	}

	@Override public void drawIcon(int x, int y, int z) {
		if (isItemValid) {
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			GlStateManager.pushMatrix();

			{
				GlStateManager.translate(x + 18, y + 18, 0);
				GlStateManager.scale(f, f, 1);

				RenderItem ri = Minecraft.getMinecraft().getRenderItem();
				float original = ri.zLevel;
				ri.zLevel = z;

				try {
					ClientUtils.renderItemIntoGUI(item, -18, -18, color, renderEffect);

				} catch (Throwable t) {
					log.catching(t);
					isItemValid = false;

				} finally {
					ri.zLevel = original;
				}
			}

			GlStateManager.popMatrix();
			GlStateManager.disableLighting();
			GlStateManager.depthMask(true);
			GlStateManager.enableDepth();
		}
	}

	@Override public String get() {
		return item.getDisplayName();
	}
	
	public IconButton iconButton(Mutable<Boolean> state, Holder<List<String>> textHolder) {
		return iconButton(state, new ItemHoveringTexts(item), textHolder);
	}
	
	public static List<String> colorTooltip(List<String> tooltips, EnumChatFormatting rarityColor) {
		int n = tooltips.size();
		if (n > 0) {
			tooltips.set(0, rarityColor + tooltips.get(0));
			for (int i = 1; i < n; i++)
				tooltips.set(i, EnumChatFormatting.GRAY + tooltips.get(i));
		}
		return tooltips;
	}


	private static class ItemHoveringTexts implements Supplier<List<String>> {
		private final ItemStack item;

		public ItemHoveringTexts(ItemStack item) {
			checkNotNull(item.getItem());
			this.item = item;
		}

		@Override public List<String> get() {
			Minecraft mc = Minecraft.getMinecraft();
			@SuppressWarnings("unchecked")
			List<String> ret = item.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);
			return colorTooltip(ret, item.getRarity().rarityColor);
		}
	}

}