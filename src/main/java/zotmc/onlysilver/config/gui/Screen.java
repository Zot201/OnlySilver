package zotmc.onlysilver.config.gui;

import java.util.List;

/**
 * A reusable object defining a screen.
 */
public interface Screen {

  public void create();

  public void destroy();

  public Iterable<Element> getElements(int w, int h, Holder<List<String>> hoveringText, Runnable quit);

  public int getRowHeight();

  public Iterable<Row> getRows(int w, Holder<List<String>> hoveringText);

}
