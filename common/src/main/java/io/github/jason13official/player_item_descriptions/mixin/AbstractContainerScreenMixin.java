package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilScreenAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

  @Inject(at = @At("HEAD"), method = "renderTooltip", cancellable = true)
  private void player_item_descriptions$renderTooltip(GuiGraphics graphics, int x, int y, CallbackInfo ci) {

    if ((Object) this instanceof IAnvilScreenAccessor accessor && accessor.player_item_descriptions$isPageVisible()) {

      ci.cancel();
    }
  }
}
