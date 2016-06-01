package zotmc.onlysilver.config.gui;

import java.util.Collection;

public interface Widget<T extends Widget<T>> extends Element {

	public T setLeftTop(int x, int y);

	public T setWidthHeight(int w, int h);

	public void addTo(Collection<? super T> collection);

}
