package io.github.jason13official.player_item_descriptions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.impl.anvil.AnvilDescriptions;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin implements IAnvilMenuAccessor {

  @Shadow private String itemName;

  @Shadow @Final private DataSlot cost;

  @Unique
  private String player_item_descriptions$itemDescription;

  @Override
  public String player_item_descriptions$getItemName() {
    return this.itemName;
  }

  @Override
  public void player_item_descriptions$setItemName(String name) {
    this.itemName = name;
  }

  @Override
  public String player_item_descriptions$getItemDescription() {
    return this.player_item_descriptions$itemDescription;
  }

  @Override
  public boolean player_item_descriptions$setItemDescription(String description) {

    String validated = AnvilDescriptions.validate(description);

    if (validated == null || validated.equals(this.player_item_descriptions$itemDescription)) {
      return false;
    }

    this.player_item_descriptions$itemDescription = validated;
    ((AnvilMenu) (Object) this).createResult();
    return true;
  }

  @Inject(
      method = "createResult",
      slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringUtil;isBlank(Ljava/lang/String;)Z")),
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 0, shift = At.Shift.AFTER)
  )
  private void player_item_descriptions$applyDescription(CallbackInfo ci,
      @Local(ordinal = 0) ItemStack input,
      @Local(ordinal = 1) ItemStack result,
      @Local(ordinal = 0) long tax,
      @Local(ordinal = 0) LocalIntRef price,
      @Local(ordinal = 1) LocalIntRef namingCost) {

    if (AnvilDescriptions.apply(this.player_item_descriptions$itemDescription, input, result)) {
      namingCost.set(namingCost.get() + 1);
      price.set(price.get() + 1);
      this.cost.set((int) Mth.clamp(tax + price.get(), 0L, Integer.MAX_VALUE));
    }
  }
}
