package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.impl.client.gui.LiteralFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StringDecomposer.class)
public abstract class StringDecomposerMixin {

  @Inject(
      method = "iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z",
      at = @At("HEAD"),
      cancellable = true
  )
  private static void player_item_descriptions$iterateFormatted(String text, int offset, Style currentStyle, Style resetStyle,
      FormattedCharSink sink, CallbackInfoReturnable<Boolean> cir) {

    if (LiteralFormatting.isActive()) {
      cir.setReturnValue(LiteralFormatting.iterate(text, offset, currentStyle, resetStyle, sink));
    }
  }
}
