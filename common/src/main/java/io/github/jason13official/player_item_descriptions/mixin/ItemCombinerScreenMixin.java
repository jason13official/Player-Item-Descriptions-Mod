package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilScreenAccessor;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCombinerScreen.class)
public abstract class ItemCombinerScreenMixin {

  @Inject(method = "init", at = @At("TAIL"))
  private void player_item_descriptions$init(CallbackInfo ci) {

    if ((Object) this instanceof IAnvilScreenAccessor accessor) {

      accessor.player_item_descriptions$init();
    }
  }

  @Inject(method = "removed", at = @At("TAIL"))
  private void player_item_descriptions$removed(CallbackInfo ci) {

    if ((Object) this instanceof IAnvilScreenAccessor accessor) {

      accessor.player_item_descriptions$removed();
    }
  }
}
