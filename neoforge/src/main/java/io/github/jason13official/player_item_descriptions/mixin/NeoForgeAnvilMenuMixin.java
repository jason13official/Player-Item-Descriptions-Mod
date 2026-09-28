package io.github.jason13official.player_item_descriptions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.impl.anvil.AnvilDescriptions;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class NeoForgeAnvilMenuMixin extends ItemCombinerMenu implements IAnvilMenuAccessor {

  @Shadow private String itemName;

  @Shadow @Final private DataSlot cost;

  @Unique
  private String player_item_descriptions$itemDescription;

  @Unique
  private @Nullable Boolean player_item_descriptions$itemLock;

  public NeoForgeAnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition itemInputSlots) {
    super(menuType, containerId, inventory, access, itemInputSlots);
  }

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
    this.createResult();
    return true;
  }

  @Override
  public @Nullable Boolean player_item_descriptions$getItemLock() {
    return this.player_item_descriptions$itemLock;
  }

  @Override
  public boolean player_item_descriptions$setItemLock(boolean locked) {

    if (Boolean.valueOf(locked).equals(this.player_item_descriptions$itemLock)) {
      return false;
    }

    this.player_item_descriptions$itemLock = locked;
    this.createResult();
    return true;
  }

  @Inject(
      method = "createResultInternal",
      slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/StringUtil;isBlank(Ljava/lang/String;)Z")),
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V", ordinal = 0, shift = At.Shift.AFTER)
  )
  private void player_item_descriptions$applyDescription(CallbackInfo ci,
      @Local(name = "input") ItemStack input,
      @Local(name = "result") ItemStack result,
      @Local(name = "tax") long tax,
      @Local(name = "price") LocalIntRef price,
      @Local(name = "namingCost") LocalIntRef namingCost) {

    if (AnvilDescriptions.apply(this.player_item_descriptions$itemDescription, this.player_item_descriptions$itemLock, input, result, this.player)) {
      namingCost.set(namingCost.get() + 1);
      price.set(price.get() + 1);
      this.cost.set((int) Mth.clamp(tax + price.get(), 0L, Integer.MAX_VALUE));
    }
  }
}
