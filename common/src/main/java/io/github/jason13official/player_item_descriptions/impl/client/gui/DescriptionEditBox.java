package io.github.jason13official.player_item_descriptions.impl.client.gui;

import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

/// 1.21.1's MultiLineEditBox has no line limit and always draws a character counter below itself,
/// so we need to fill in the builder options (setLineLimit / setShowDecorations) as used on newer versions.
public class DescriptionEditBox extends MultiLineEditBox {

  private static final int LINE_HEIGHT = 9;

  private final int lineLimit;

  public DescriptionEditBox(Font font, int x, int y, int width, int height, int characterLimit, int lineLimit) {
    super(font, x, y, width, height, Component.empty(), Component.empty());
    this.lineLimit = lineLimit;
    this.setCharacterLimit(characterLimit);
  }

  /// 1.21.1's MultiLineEditBox accepts mouse input while hidden, which would swallow clicks on the slots beneath it,
  /// even if it's not being rendered (backport from 26.3 fix)
  /// @see MultiLineEditBox
  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    return this.isUsable() && super.mouseClicked(mouseX, mouseY, button);
  }

  @Override
  public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
    return this.isUsable() && super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
    return this.isUsable() && super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return this.limitLines(() -> super.keyPressed(keyCode, scanCode, modifiers));
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {
    return this.limitLines(() -> super.charTyped(codePoint, modifiers));
  }

  /// line limit keeps text inside the box, so the scrollbar and character counter are never needed
  @Override
  protected void renderDecorations(GuiGraphics graphics) {
  }

  private boolean isUsable() {
    return this.visible && this.active;
  }

  private boolean limitLines(BooleanSupplier edit) {

    String previous = this.getValue();
    boolean handled = edit.getAsBoolean();

    if (this.getInnerHeight() / LINE_HEIGHT > this.lineLimit) {
      this.setValue(previous);
    }

    return handled;
  }
}
