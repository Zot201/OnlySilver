package zotmc.onlysilver.config.gui;

import java.util.Collections;
import java.util.List;

import zotmc.onlysilver.data.LangData;

public abstract class ScreenFactoryRow extends WidgetRow {

	private final Button button = new Button(new EditSceen(), LangData.EDIT);

	// widget
	@Override protected Widget<?> widget() {
		return button;
	}

	// screen
	protected abstract void loadValue();
	protected abstract void resetValue();
	protected abstract void saveValue();
	
	public int getRowHeight() {
		return 20;
	}

	public abstract Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText);

	protected Iterable<Row> getLowerRows() {
		return Collections.<Row>nCopies(2, EmptyRow.INSTANCE);
	}


	private class EditSceen extends AbstractConfigScreen implements Runnable {
		
		@Override public void create() {
			loadValue();
		}
		@Override public void destroy() {
			loadValue();
		}
		@Override protected void reset() {
			resetValue();
		}
		@Override protected void save() {
			saveValue();
		}
		
		@Override protected Element getTitleElement(int w) {
			Icon<?> icon = icon();
			return icon != null ? icon.title(title(), w / 2, 16) : new Title(title(), w / 2, 16);
		}
		
		@Override public int getRowHeight() {
			return ScreenFactoryRow.this.getRowHeight();
		}
		@Override public Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText) {
			return ScreenFactoryRow.this.getUpperRows(w, hoveringText);
		}
		@Override protected Iterable<Row> getLowerRows() {
			return ScreenFactoryRow.this.getLowerRows();
		}

		@Override public void run() {
			GuiScreenWrapper.display(this);
		}
	}

}
