package io.github.jason13official.player_item_descriptions.impl.client.gui;

import io.github.jason13official.player_item_descriptions.api.common.access.IFormattingAccessor;
import io.github.jason13official.player_item_descriptions.mixin.MultiLineEditBoxAccessor;
import io.github.jason13official.player_item_descriptions.mixin.MultilineTextFieldAccessor;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/// 1.21.1's MultiLineEditBox has no line limit and always draws a character counter below itself,
/// so we need to fill in the builder options (setLineLimit / setShowDecorations) as used on newer versions.
public class DescriptionEditBox extends MultiLineEditBox {

  private static final int LINE_HEIGHT = 9;
  private static final int SELECTION_COLOR = 0xFF0000FF;
  private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("widget/scroller");

  private final Font font;
  private final int lineLimit;
  private long focusedTime = Util.getMillis();
  private Consumer<String> valueListener = value -> {};
  private boolean editing;

  public DescriptionEditBox(Font font, int x, int y, int width, int height, int characterLimit, int lineLimit) {
    super(font, x, y, width, height, Component.empty(), Component.empty());
    this.font = font;
    this.lineLimit = lineLimit;
    this.setCharacterLimit(characterLimit);
    ((IFormattingAccessor) ((MultiLineEditBoxAccessor) this).player_item_descriptions$getTextField()).player_item_descriptions$setAllowFormatting(true);
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
  public void setValueListener(Consumer<String> valueListener) {

    this.valueListener = valueListener;
    super.setValueListener(value -> {

      if (!this.editing) {
        this.valueListener.accept(value);
      }
    });
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    return this.limitLines(() -> super.keyPressed(keyCode, scanCode, modifiers));
  }

  @Override
  public boolean charTyped(char codePoint, int modifiers) {

    if (codePoint == '§' && this.visible && this.isFocused()) {
      return this.limitLines(() -> {
        ((MultiLineEditBoxAccessor) this).player_item_descriptions$getTextField().insertText(Character.toString(codePoint));
        return true;
      });
    }

    return this.limitLines(() -> super.charTyped(codePoint, modifiers));
  }

  @Override
  protected void renderDecorations(GuiGraphics graphics) {

    if (this.scrollbarVisible()) {
      int barHeight = Mth.clamp((int) ((float) (this.height * this.height) / (float) (this.getInnerHeight() + 4)), 32, this.height);
      int barY = Math.max(this.getY(), (int) this.scrollAmount() * (this.height - barHeight) / this.getMaxScrollAmount() + this.getY());
      graphics.blitSprite(SCROLLER_SPRITE, this.getX() + this.width, barY, DescriptionPanel.SCROLLBAR_WIDTH, barHeight);
    }
  }

  @Override
  protected void renderBackground(GuiGraphics graphics) {
  }

  @Override
  public void setFocused(boolean focused) {
    super.setFocused(focused);

    if (focused) {
      this.focusedTime = Util.getMillis();
    }
  }

  @Override
  protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

    LiteralFormatting.reset();

    MultilineTextField textField = ((MultiLineEditBoxAccessor) this).player_item_descriptions$getTextField();
    String value = textField.value();
    List<int[]> lines = LiteralFormatting.splitLines(this.font, value, this.width - this.totalInnerPadding());

    int cursor = textField.cursor();
    boolean showCursor = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L;
    boolean insertCursor = cursor < value.length();
    boolean cursorFound = false;
    int left = this.getX() + this.innerPadding();
    int top = this.getY() + this.innerPadding();
    int cursorX = left;
    int cursorY = top;
    int y = top;

    for (int[] line : lines) {

      if (this.withinContentAreaTopBottom(y, y + LINE_HEIGHT)) {
        graphics.drawString(this.font, LiteralFormatting.sequence(value, line[0], line[1]), left, y, DescriptionPanel.TEXT_COLOR, false);
      }

      if (!cursorFound && cursor >= line[0] && cursor <= line[1]) {
        cursorX = left + LiteralFormatting.width(this.font, value, line[0], cursor);
        cursorY = y;
        cursorFound = true;
      }

      y += LINE_HEIGHT;
    }

    if (!cursorFound) {
      int[] last = lines.getLast();
      cursorX = left + LiteralFormatting.width(this.font, value, last[0], last[1]);
      cursorY = top + (lines.size() - 1) * LINE_HEIGHT;
    }

    if (showCursor && this.withinContentAreaTopBottom(cursorY, cursorY + LINE_HEIGHT)) {

      if (insertCursor) {
        graphics.fill(cursorX, cursorY - 1, cursorX + 1, cursorY + 1 + LINE_HEIGHT, DescriptionPanel.TEXT_COLOR);
      } else {
        graphics.drawString(this.font, "_", cursorX, cursorY, DescriptionPanel.TEXT_COLOR, false);
      }
    }

    if (!textField.hasSelection()) {
      return;
    }

    int selectCursor = ((MultilineTextFieldAccessor) textField).player_item_descriptions$getSelectCursor();
    int selectionBegin = Math.min(selectCursor, cursor);
    int selectionEnd = Math.max(selectCursor, cursor);
    y = top;

    for (int[] line : lines) {

      if (selectionBegin > line[1]) {
        y += LINE_HEIGHT;
        continue;
      }

      if (line[0] > selectionEnd) {
        break;
      }

      if (this.withinContentAreaTopBottom(y, y + LINE_HEIGHT)) {
        int from = LiteralFormatting.width(this.font, value, line[0], Math.max(selectionBegin, line[0]));
        int to = selectionEnd > line[1] ? this.width - this.innerPadding() : LiteralFormatting.width(this.font, value, line[0], selectionEnd);
        graphics.fill(RenderType.guiTextHighlight(), left + from, y, left + to, y + LINE_HEIGHT, SELECTION_COLOR);
      }

      y += LINE_HEIGHT;
    }
  }

  private boolean isUsable() {
    return this.visible && this.active;
  }

  private boolean limitLines(BooleanSupplier edit) {

    String previous = this.getValue();
    boolean handled;
    this.editing = true;

    try {
      handled = edit.getAsBoolean();

      if (this.getInnerHeight() / LINE_HEIGHT > this.lineLimit) {
        this.setValue(previous);
      }
    } finally {
      this.editing = false;
    }

    if (!this.getValue().equals(previous)) {
      this.valueListener.accept(this.getValue());
    }

    return handled;
  }
}
