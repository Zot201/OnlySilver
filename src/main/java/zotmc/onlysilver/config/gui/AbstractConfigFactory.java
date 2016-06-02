package zotmc.onlysilver.config.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;

import zotmc.onlysilver.util.Klas.KlastWriter;
import zotmc.onlysilver.util.init.MethodInfo;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

public abstract class AbstractConfigFactory extends AbstractConfigScreen implements IModGuiFactory {

  protected abstract Supplier<String> getTitle();

  @Override protected Element getTitleElement(int w) {
    return new Title(getTitle(), w / 2, 16);
  }

  @Override protected Iterable<Row> getLowerRows() {
    return ImmutableList.<Row>of(EmptyRow.INSTANCE);
  }


  @Override public void initialize(Minecraft minecraftInstance) { }
  @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }
  @Override public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) { return null; }

  @Override public Class<? extends GuiScreen> mainConfigGuiClass() {
    return mainConfigGuiClass.get();
  }

  private static long counts;

  private final Supplier<Class<? extends GuiScreen>>
  mainConfigGuiClass = Suppliers.memoize(new Supplier<Class<? extends GuiScreen>>() { public Class<? extends GuiScreen> get() {
    Class<?> f = AbstractConfigFactory.this.getClass();
    KlastWriter<GuiConfigImpl> cw = new KlastWriter<>(f.getName() + "_" + counts++, GuiConfigImpl.class);

    cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER);
    cw.visitSource(".dynamic", null);

    MethodInfo m = MethodInfo.of("<init>", "(Lnet/minecraft/client/gui/GuiScreen;)V");
    GeneratorAdapter mg = new GeneratorAdapter(Opcodes.ACC_PUBLIC, m, null, null, cw);
    mg.loadThis();
    mg.loadArgs();
    mg.invokeConstructor(cw.parent.toType(), m);
    mg.returnValue();
    mg.endMethod();

    Class<? extends GuiConfigImpl> ret = cw.define().toClass();
    GuiConfigImpl.setScreen(ret, AbstractConfigFactory.this);
    return ret;
  }});

}
