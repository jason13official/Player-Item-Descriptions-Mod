package io.github.jason13official.player_item_descriptions.mixin.compat.easyanvils;

import fuzs.easyanvils.common.world.inventory.state.AnvilMenuState;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "fuzs.easyanvils.common.world.inventory.ModAnvilMenu")
public abstract class ModAnvilMenuMixin {

  @Shadow @Final private AnvilMenuState builtInAnvilState;

  @Inject(method = "createAnvilResult()V", at = @At("HEAD"))
  private void player_item_descriptions$createAnvilResult(CallbackInfo ci) {

    String description = ((IAnvilMenuAccessor) (Object) this).player_item_descriptions$getItemDescription();

    if (description != null && this.builtInAnvilState instanceof IAnvilMenuAccessor builtInAnvilMenu) {

      builtInAnvilMenu.player_item_descriptions$setItemDescription(description);
    }
  }
}
