package zotmc.onlysilver.config.gui;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.base.Supplier;

public class CategoryRow implements Row {

	private final Icon<?> icon;
	private final Supplier<String> text;

	private CategoryRow(Icon<?> icon, Supplier<String> text) {
		this.icon = icon;
		this.text = text;
	}

	public static CategoryRow create(Supplier<String> text) {
		return new CategoryRow(null, text);
	}

	static CategoryRow create(Icon<?> icon, Supplier<String> text) {
		return new CategoryRow(checkNotNull(icon), text);
	}

	@Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) {
		String s = text.get();
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		int x1 = x + w / 2 - fr.getStringWidth(s) / 2;

		if (icon != null) {
			icon.drawIcon(x1 - 9, y);
			fr.drawString(s, x1 + 12, y - 2 + h - fr.FONT_HEIGHT, 0xFFFFFF);
		}
		else {
			fr.drawString(s, x1, y - 2 + h - fr.FONT_HEIGHT, 0xFFFFFF);
		}
	}

	@Override public boolean clickRow(int mouseX, int mouseY) { return false; }

	@Override public void releaseRow(int mouseX, int mouseY) { }

	@Override public void keyTyped(char typedChar, int keyCode) { }

	@Override public void setIsFocus(MutableBoolean isFocus) { }

	@Override public boolean folded() { return false; }

}
