package zotmc.onlysilver.config.gui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiScreenWrapper extends GuiScreen {

  private final GuiScreen parent; // nullable
  private final Holder<List<String>> hoveringText = Holder.absent();
  private Screen screen;
  private Iterable<Element> elements;
  private GuiEmbeddedList embededList;

  private GuiScreenWrapper(GuiScreen parent, Screen screen) {
    this.parent = parent;
    this.screen = checkNotNull(screen);
    screen.create();
  }

  GuiScreenWrapper(GuiScreen parent) {
    this.parent = parent;
    this.screen = checkNotNull(createScreen());
    screen.create();
  }

  Screen createScreen() {
    return null;
  }

  public static void display(Screen screen) {
    Minecraft mc = Minecraft.getMinecraft();
    mc.displayGuiScreen(new GuiScreenWrapper(mc.currentScreen, screen));
  }

  @Override public void initGui() {
    Keyboard.enableRepeatEvents(true);
    elements = screen.getElements(width, height, hoveringText, new Runnable() { public void run() {
      screen.destroy();
      mc.displayGuiScreen(parent);
    }});
    embededList = new GuiEmbeddedList(this, screen.getRowHeight(), screen.getRows(width, hoveringText));
  }

  @Override public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }

  @Override public void drawScreen(int mouseX, int mouseY, float tickFrac) {
    drawDefaultBackground();
    hoveringText.clear();
    embededList.drawScreen(mouseX, mouseY, tickFrac);
    for (Element e : elements)
      e.draw(mouseX, mouseY);
    super.drawScreen(mouseX, mouseY, tickFrac);
    if (hoveringText.isPresent()) drawHoveringText(hoveringText.get(), mouseX, mouseY);
  }

  @Override public void handleMouseInput() throws IOException {
    embededList.handleMouseInput();
    super.handleMouseInput();
  }

  @Override protected void keyTyped(char typedChar, int keyCode) throws IOException {
    embededList.keyTyped(typedChar, keyCode);
  }

  @Override protected void mouseClicked(int x, int y, int mouseEvent) throws IOException {
    if (!embededList.mouseClicked(x, y, mouseEvent) && mouseEvent == 0)
      for (Element e : elements)
        if (e.click(x, y)) return;
  }

  @Override protected void mouseReleased(int x, int y, int mouseEvent) {
    if (!embededList.mouseReleased(x, y, mouseEvent) && mouseEvent == 0)
      for (Element e : elements)
        e.release(x, y);
  }

}
