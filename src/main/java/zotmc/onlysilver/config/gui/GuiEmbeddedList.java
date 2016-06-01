package zotmc.onlysilver.config.gui;

import net.minecraft.client.gui.GuiSlot;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

class GuiEmbeddedList extends GuiSlot {

	private final GuiScreenWrapper mainScreen;
	private final PseudoIterator<Row> rows;
	private MutableBoolean isFocus = new MutableBoolean();

	public GuiEmbeddedList(GuiScreenWrapper mainScreen, int rowHeight, Iterable<Row> entries) {
		super(mainScreen.mc, mainScreen.width, mainScreen.height, 33, mainScreen.height - 32, rowHeight);
		setShowSelectionBox(false);
		this.mainScreen = mainScreen;
		this.rows = PseudoIterator.of(Iterables.filter(entries, NotFolded.INSTANCE), EmptyRow.INSTANCE, 1);
	}

	@Override protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) { }

	@Override protected boolean isSelected(int slotIndex) { return false; }

	@Override protected void drawBackground() { }

	@Override protected int getSize() {
		return rows.size();
	}

	static int getMarginWidth(int width) {
		return Math.max(0, Math.min(38, width - 388));
	}

	@Override protected int getScrollBarX() {
		return width - getMarginWidth(width) - 6;
	}

	@Override public int getListWidth() {
		return mainScreen.width;
	}

	@Override public int getSlotIndexFromScreenCoords(int x, int y) {
		return super.getSlotIndexFromScreenCoords(x, y - 4);
	}

	@Override protected void drawSlot(int index, int unused, int y, int slotHeight, int mouseX, int mouseY) {
		int s = getMarginWidth(width) + 33;
		rows.next(index).drawRow(s, y + 6, getListWidth() - 2 * s, slotHeight, mouseX, mouseY);
	}

	public void keyTyped(char typedChar, int keyCode) {
		for (Row r : rows)
			r.keyTyped(typedChar, keyCode);
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
		if (isMouseYWithinSlotBounds(mouseY)) {
			int s = getScrollBarX();

			if (mouseX < s || mouseX > s + 6 || func_148135_f() <= 0) {
				isFocus.setValue(false);

				if (mouseEvent == 0) {
					int index = getSlotIndexFromScreenCoords(mouseX, mouseY);

					if (index >= 0) {
						Row row = rows.next(index);

						if (row.clickRow(mouseX, mouseY)) {
							row.setIsFocus(isFocus = new MutableBoolean(true));
							setEnabled(false);
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean mouseReleased(int mouseX, int mouseY, int mouseEvent) {
		if (mouseEvent == 0)
			for (Row r : rows)
				r.releaseRow(mouseX, mouseY);

		setEnabled(true);
		return false;
	}


	private enum NotFolded implements Predicate<Row> {
		INSTANCE;
		@Override public boolean apply(Row input) {
			return !input.folded();
		}
	}

}
