package zotmc.onlysilver.config.gui;

/**
 * Elements on a screen. Static element usually locate on a header or a footer.
 */
public interface Element {

	public void draw(int mouseX, int mouseY);

	public boolean click(int mouseX, int mouseY);

	public void release(int mouseX, int mouseY);

	public void keyTyped(char typedChar, int keyCode);

}
