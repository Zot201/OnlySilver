package zotmc.onlysilver.config.gui;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class IconButtonLayout implements Iterable<Row> {

	public static final int SLOT_SIZE = 26;
	private final Iterable<Row> rows;

	public IconButtonLayout(Iterable<IconButton> iconButtons, int width) {
		rows = Iterables.transform(Iterables.partition(iconButtons, buttonsPerRow(width)), NewButtonRow.INSTANCE);
	}

	private static int buttonsPerRow(int width) {
		return Math.max(1, (width - 2 * (GuiEmbeddedList.getMarginWidth(width) + 33)) / SLOT_SIZE);
	}

	@Override public Iterator<Row> iterator() {
		return rows.iterator();
	}
	
	
	private static class ButtonRow implements Row {
		private final List<IconButton> buttons;
		
		ButtonRow(List<IconButton> buttons) {
			this.buttons = buttons;
		}

		@Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) {
			int n = buttons.size();
			int x1 = x + w / 2 - n * (SLOT_SIZE / 2);
			for (int i = 0; i < n; i++) {
				drawSlot(i, x1, y, mouseX, mouseY);
				x1 += SLOT_SIZE;
			}
		}
		private void drawSlot(int index, int x, int y, int mouseX, int mouseY) {
			buttons.get(index)
				.setLeftTop(x + (SLOT_SIZE - 22) / 2, y + (SLOT_SIZE - 22) / 2)
				.draw(mouseX, mouseY);
		}

		@Override public boolean clickRow(int mouseX, int mouseY) {
			for (IconButton b : buttons)
				if (b.click(mouseX, mouseY)) return true;
			return false;
		}
		@Override public void releaseRow(int mouseX, int mouseY) {
			for (IconButton b : buttons)
				b.release(mouseX, mouseY);
		}
		
		@Override public void keyTyped(char typedChar, int keyCode) { }
		
		@Override public void setIsFocus(MutableBoolean isFocus) { }
		
		@Override public boolean folded() { return false; }
	}
	
	private enum NewButtonRow implements Function<List<IconButton>, Row> {
		INSTANCE;
		@Override public Row apply(List<IconButton> input) {
			return new ButtonRow(input);
		}
	}
	
}
