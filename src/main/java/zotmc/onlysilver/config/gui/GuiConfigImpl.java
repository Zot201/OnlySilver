package zotmc.onlysilver.config.gui;

import java.util.Map;

import net.minecraft.client.gui.GuiScreen;

import com.google.common.collect.Maps;

public class GuiConfigImpl extends GuiScreenWrapper {

	private static final Map<Class<? extends GuiConfigImpl>, Screen> screens = Maps.newIdentityHashMap();

	@Deprecated protected GuiConfigImpl(GuiScreen parent) {
		super(parent);
	}

	@Override Screen createScreen() {
		return screens.get(getClass());
	}

	public static void setScreen(Class<? extends GuiConfigImpl> clz, Screen screen) {
		screens.put(clz, screen);
	}

}
