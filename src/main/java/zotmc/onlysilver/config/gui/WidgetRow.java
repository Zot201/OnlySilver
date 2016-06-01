package zotmc.onlysilver.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.base.Supplier;

public abstract class WidgetRow implements Row {
	
	// widget
	protected abstract Widget<?> widget();

	protected int widgetPos(int k) {
		return k * 7 / 12;
	}

	// title
	protected Icon<?> icon() {
		return null;
	}

	protected abstract Supplier<String> title();
	
	
	// row
	@Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) {
		int l = widgetPos(w + 144) - 72;
		widget().setLeftTop(x + l, y - 1)
			.setWidthHeight(w - l, h + 3)
			.draw(mouseX, mouseY);

		drawTitle(Minecraft.getMinecraft().fontRendererObj, title(), x + 2, y, h);
	}

	private void drawTitle(FontRenderer fr, Supplier<String> title, int x1, int y, int h) {
		Icon<?> icon = icon();
		if (icon != null) {
			icon.drawIcon(++x1, y);
			x1 += 20;
		}
		fr.drawString(title.get(), x1, y + h / 2 - fr.FONT_HEIGHT / 2, 0xFFFFFF);
	}

	@Override public boolean clickRow(int mouseX, int mouseY) {
		return widget().click(mouseX, mouseY);
	}

	@Override public void releaseRow(int mouseX, int mouseY) {
		widget().release(mouseX, mouseY);
	}

	@Override public void keyTyped(char typedChar, int keyCode) {
		widget().keyTyped(typedChar, keyCode);
	}

	@Override public void setIsFocus(MutableBoolean isFocus) { }

	@Override public boolean folded() { return false; }

}
