package zotmc.onlysilver.config.gui;

import org.apache.commons.lang3.mutable.MutableBoolean;

public interface Row {

	public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY);

	public boolean clickRow(int mouseX, int mouseY);

	public void releaseRow(int mouseX, int mouseY);

	public void keyTyped(char typedChar, int keyCode);

	public void setIsFocus(MutableBoolean isFocus);

	public boolean folded();

}
