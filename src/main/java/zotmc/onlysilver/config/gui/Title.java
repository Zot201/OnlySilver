package zotmc.onlysilver.config.gui;

import static com.google.common.base.Preconditions.checkNotNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.google.common.base.Supplier;

public class Title implements Element {

  private final Icon<?> icon;
  private final Supplier<String> text;
  private final int x, y;

  public Title(Supplier<String> text, int x, int y) {
    this(null, text, x, y);
  }

  Title(Icon<?> icon, Supplier<String> text, int x, int y) {
    this.icon = icon;
    this.text = checkNotNull(text);
    this.x = x;
    this.y = y;
  }

  @Override public void draw(int mouseX, int mouseY) {
    String s = text.get();
    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    int x1 = x - fr.getStringWidth(s) / 2;

    if (icon != null) {
      icon.drawIcon(x1 - 9, y - 5);
      fr.drawStringWithShadow(s, x1 + 12, y + 9 - fr.FONT_HEIGHT, 0xFFFFFF);
    }
    else {
      fr.drawStringWithShadow(s, x1, y + 9 - fr.FONT_HEIGHT, 0xFFFFFF);
    }
  }

  @Override public boolean click(int mouseX, int mouseY) {
    return false;
  }

  @Override public void release(int mouseX, int mouseY) { }

  @Override public void keyTyped(char typedChar, int keyCode) { }

}
