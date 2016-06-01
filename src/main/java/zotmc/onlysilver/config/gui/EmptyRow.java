package zotmc.onlysilver.config.gui;

import org.apache.commons.lang3.mutable.MutableBoolean;

public enum EmptyRow implements Row {
	INSTANCE;

	@Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) { }

	@Override public boolean clickRow(int mouseX, int mouseY) { return false; }

	@Override public void releaseRow(int mouseX, int mouseY) { }

	@Override public void keyTyped(char typedChar, int keyCode) { }

	@Override public void setIsFocus(MutableBoolean isFocus) { }

	@Override public boolean folded() { return false; }

}
