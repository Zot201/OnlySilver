package zotmc.onlysilver.config.gui;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableBoolean;

import zotmc.onlysilver.data.LangData;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

abstract class AbstractConfigScreen implements Screen {
	
	protected abstract void reset();

	protected abstract void save();
	
	protected abstract Element getTitleElement(int w);
	
	protected Iterable<? extends Element> getExtraElements(int w, int h, Holder<List<String>> hoveringText) {
		return ImmutableList.of();
	}

	@Override public Iterable<Element> getElements(int w, int h, Holder<List<String>> hoveringText, final Runnable quit) {
		Runnable saveQuit = new Runnable() { public void run() {
			save();
			quit.run();
		}};

		List<Element> ret = Lists.newArrayList();

		ret.add(getTitleElement(w));

		new Button(saveQuit, LangData.DONE)
			.setLeftTop(w / 2 - 155, h - 29)
			.setWidthHeight(150, 20)
			.addTo(ret);

		new Button(quit, LangData.CANCEL)
			.setLeftTop(w - w / 2 + 5, h - 29)
			.setWidthHeight(150, 20)
			.addTo(ret);

		return Iterables.concat(ret, getExtraElements(w, h, hoveringText));
	}

	@Override public int getRowHeight() {
		return 20;
	}

	protected abstract Iterable<? extends Row> getUpperRows(int w, Holder<List<String>> hoveringText);

	protected Iterable<Row> getLowerRows() {
		return Collections.<Row>nCopies(2, EmptyRow.INSTANCE);
	}

	@Override public Iterable<Row> getRows(int w, Holder<List<String>> hoveringText) {
		return Iterables.concat(getUpperRows(w, hoveringText),
				ImmutableList.of(EmptyRow.INSTANCE, new ResetButtonRow()), getLowerRows());
	}


	private class ResetButtonRow implements Row, Runnable {
		private final Button button = new Button(this, LangData.RESET_TO_DEFAULT);

		@Override public void drawRow(int x, int y, int w, int h, int mouseX, int mouseY) {
			int l = (w + 144) * 5 / 9 - 72;
			button.setLeftTop(x + l, y)
				.setWidthHeight(w - l, 16)
				.draw(mouseX, mouseY);
		}

		@Override public boolean clickRow(int mouseX, int mouseY) {
			return button.click(mouseX, mouseY);
		}

		@Override public void releaseRow(int mouseX, int mouseY) { }

		@Override public void keyTyped(char typedChar, int keyCode) { }

		@Override public void setIsFocus(MutableBoolean isFocus) { }

		@Override public boolean folded() { return false; }

		@Override public void run() {
			reset();
		}
	}

}
