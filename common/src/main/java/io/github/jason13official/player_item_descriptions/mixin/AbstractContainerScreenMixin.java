package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilScreenAccessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

  @Inject(at = @At("HEAD"), method = "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z", cancellable = true)
  private void player_item_descriptions$isHovering(Slot slot, double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {

    if ((Object) this instanceof IAnvilScreenAccessor accessor && accessor.player_item_descriptions$isOverPanel(mouseX, mouseY)) {

      cir.setReturnValue(false);
    }
  }

  @Inject(at = @At("RETURN"), method = "mouseClicked")
  private void player_item_descriptions$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

    if ((Object) this instanceof IAnvilScreenAccessor accessor) {

      accessor.player_item_descriptions$mouseClicked();
    }
  }
}
