package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.api.common.access.IFormattingAccessor;
import io.github.jason13official.player_item_descriptions.impl.anvil.AnvilDescriptions;
import io.github.jason13official.player_item_descriptions.impl.client.gui.LiteralFormatting;
import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultilineTextField.class)
public abstract class MultilineTextFieldMixin implements IFormattingAccessor {

  @Shadow private String value;

  @Shadow private int cursor;

  @Shadow private int selectCursor;

  @Unique
  private boolean player_item_descriptions$allowFormatting;

  @Shadow
  public abstract boolean hasSelection();

  @Shadow
  protected abstract String truncateInsertionText(String input);

  @Shadow
  protected abstract boolean overflowsLineLimit(String newValue);

  @Shadow
  protected abstract void onValueChange();

  @Override
  public void player_item_descriptions$setAllowFormatting(boolean allowFormatting) {

    this.player_item_descriptions$allowFormatting = allowFormatting;
  }

  @Override
  public boolean player_item_descriptions$allowsFormatting() {

    return this.player_item_descriptions$allowFormatting;
  }

  @Inject(method = "insertText", at = @At("HEAD"), cancellable = true)
  private void player_item_descriptions$insertText(String input, CallbackInfo ci) {

    if (!this.player_item_descriptions$allowFormatting) {
      return;
    }

    ci.cancel();

    if (input.isEmpty() && !this.hasSelection()) {
      return;
    }

    String text = this.truncateInsertionText(AnvilDescriptions.filter(input));
    int begin = Math.min(this.cursor, this.selectCursor);
    int end = Math.max(this.cursor, this.selectCursor);
    String newValue = new StringBuilder(this.value).replace(begin, end, text).toString();

    if (this.overflowsLineLimit(newValue)) {
      return;
    }

    this.value = newValue;
    this.cursor = begin + text.length();
    this.selectCursor = this.cursor;
    this.onValueChange();
  }

  @Inject(method = {"reflowDisplayLines", "seekCursorLine", "seekCursorToPoint"}, at = @At("HEAD"))
  private void player_item_descriptions$beginLiteralFormatting(CallbackInfo ci) {

    if (this.player_item_descriptions$allowFormatting) {
      LiteralFormatting.begin();
    }
  }

  @Inject(method = {"reflowDisplayLines", "seekCursorLine", "seekCursorToPoint"}, at = @At("RETURN"))
  private void player_item_descriptions$endLiteralFormatting(CallbackInfo ci) {

    if (this.player_item_descriptions$allowFormatting) {
      LiteralFormatting.end();
    }
  }

  @Inject(method = "overflowsLineLimit", at = @At("HEAD"))
  private void player_item_descriptions$beginLineLimit(String newValue, CallbackInfoReturnable<Boolean> cir) {

    if (this.player_item_descriptions$allowFormatting) {
      LiteralFormatting.begin();
    }
  }

  @Inject(method = "overflowsLineLimit", at = @At("RETURN"))
  private void player_item_descriptions$endLineLimit(String newValue, CallbackInfoReturnable<Boolean> cir) {

    if (this.player_item_descriptions$allowFormatting) {
      LiteralFormatting.end();
    }
  }
}
