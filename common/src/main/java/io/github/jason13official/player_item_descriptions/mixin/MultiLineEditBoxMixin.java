package io.github.jason13official.player_item_descriptions.mixin;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import io.github.jason13official.player_item_descriptions.api.common.access.IFormattingAccessor;
import io.github.jason13official.player_item_descriptions.impl.client.gui.LiteralFormatting;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.components.IMEPreeditOverlay;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxMixin extends AbstractTextAreaWidget implements IFormattingAccessor {

  @Shadow @Final private Font font;

  @Shadow @Final private MultilineTextField textField;

  @Shadow @Final private int textColor;

  @Shadow @Final private boolean textShadow;

  @Shadow @Final private int cursorColor;

  @Shadow private @Nullable IMEPreeditOverlay preeditOverlay;

  @Shadow private long focusedTime;

  public MultiLineEditBoxMixin(int x, int y, int width, int height, Component narration, ScrollbarSettings scrollbarSettings) {
    super(x, y, width, height, narration, scrollbarSettings);
  }

  @Override
  public void player_item_descriptions$setAllowFormatting(boolean allowFormatting) {

    ((IFormattingAccessor) this.textField).player_item_descriptions$setAllowFormatting(allowFormatting);
  }

  @Override
  public boolean player_item_descriptions$allowsFormatting() {

    return ((IFormattingAccessor) this.textField).player_item_descriptions$allowsFormatting();
  }

  @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
  private void player_item_descriptions$charTyped(CharacterEvent event, CallbackInfoReturnable<Boolean> cir) {

    if (event.codepoint() == '§' && this.visible && this.isFocused() && this.player_item_descriptions$allowsFormatting()) {

      this.textField.insertText(event.codepointAsString());
      cir.setReturnValue(true);
    }
  }

  @Inject(method = "extractContents", at = @At("HEAD"), cancellable = true)
  private void player_item_descriptions$extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {

    if (!this.player_item_descriptions$allowsFormatting()) {
      return;
    }

    ci.cancel();

    String value = this.textField.value();
    List<int[]> lines = LiteralFormatting.splitLines(this.font, value, this.width - this.totalInnerPadding());

    int cursor = this.textField.cursor();
    boolean showCursor = this.isFocused() && TextCursorUtils.isCursorVisible(Util.getMillis() - this.focusedTime);
    boolean insertCursor = cursor < value.length();
    boolean cursorFound = false;
    int left = this.getInnerLeft();
    int top = this.getInnerTop();
    int cursorX = left;
    int cursorY = top;
    int y = top;

    for (int[] line : lines) {

      if (this.withinContentAreaTopBottom(y, y + 9)) {
        graphics.text(this.font, LiteralFormatting.sequence(value, line[0], line[1]), left, y, this.textColor, this.textShadow);
      }

      if (!cursorFound && cursor >= line[0] && cursor <= line[1]) {
        cursorX = left + LiteralFormatting.width(this.font, value, line[0], cursor);
        cursorY = y;
        cursorFound = true;
      }

      y += 9;
    }

    if (showCursor && this.withinContentAreaTopBottom(cursorY, cursorY + 9)) {

      if (insertCursor) {
        TextCursorUtils.extractInsertCursor(graphics, cursorX, cursorY, this.cursorColor, 9 + 1);
      } else {
        TextCursorUtils.extractAppendCursor(graphics, this.font, cursorX, cursorY, this.cursorColor, this.textShadow);
      }
    }

    if (this.textField.hasSelection()) {

      int selectCursor = ((MultilineTextFieldAccessor) this.textField).player_item_descriptions$getSelectCursor();
      int selectionBegin = Math.min(selectCursor, cursor);
      int selectionEnd = Math.max(selectCursor, cursor);
      y = top;

      for (int[] line : lines) {

        if (selectionBegin > line[1]) {
          y += 9;
          continue;
        }

        if (line[0] > selectionEnd) {
          break;
        }

        if (this.withinContentAreaTopBottom(y, y + 9)) {
          int from = LiteralFormatting.width(this.font, value, line[0], Math.max(selectionBegin, line[0]));
          int to = selectionEnd > line[1] ? this.width - this.innerPadding() : LiteralFormatting.width(this.font, value, line[0], selectionEnd);
          graphics.textHighlight(left + from, y, left + to, y + 9, true);
        }

        y += 9;
      }
    }

    if (this.isHovered()) {
      graphics.requestCursor(CursorTypes.IBEAM);
    }

    if (this.preeditOverlay == null) {

      if (this.capturesInput()) {
        Minecraft.getInstance().textInputManager().setTextInputArea(cursorX, cursorY, cursorX + 1, cursorY + 9 + 1);
      }
    } else {
      this.preeditOverlay.updateInputPosition(cursorX, cursorY);
      graphics.setPreeditOverlay(this.preeditOverlay);
    }
  }
}
