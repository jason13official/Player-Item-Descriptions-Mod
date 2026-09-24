package io.github.jason13official.player_item_descriptions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin implements IAnvilMenuAccessor {

  @Shadow private String itemName;

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

    String validated = player_item_descriptions$validateDescription(description);

    if (validated == null || validated.equals(this.player_item_descriptions$itemDescription)) {
      return false;
    }

    this.player_item_descriptions$itemDescription = validated;
    ((AnvilMenu) (Object) this).createResult();
    return true;
  }

  // 1.21.1 has no local variable names at runtime (mappings yay), so locals are captured by ordinal instead;
  // ItemStack 0 = input, ItemStack 1 = result, int 0 = price, int 1 = namingCost (26.3 backport fix)
  @Inject(
      method = {"createResult"},
      require = 1,
      at = @At(
          value = "FIELD",
          target = "Lnet/minecraft/world/inventory/AnvilMenu;repairItemCountCost:I",
          opcode = Opcodes.PUTFIELD,
          ordinal = 0
      )
  )
  private void player_item_descriptions$applyDescription(CallbackInfo ci,
      @Local(ordinal = 0) ItemStack input, // <- DO NOT TRUST THE SQUIGGLY LINES
      @Local(ordinal = 1) ItemStack result,
      @Local(ordinal = 0) LocalIntRef price,
      @Local(ordinal = 1) LocalIntRef namingCost) {

    String description = this.player_item_descriptions$itemDescription;

    if (description == null) {
      return;
    }

    Component current = input.get(ModComponents.CUSTOM_DESCRIPTION);
    String currentDescription = current == null ? "" : current.getString();

    if (StringUtil.isBlank(description)) {

      if (!currentDescription.isEmpty()) {
        result.remove(ModComponents.CUSTOM_DESCRIPTION);
        namingCost.set(namingCost.get() + 1);
        price.set(price.get() + 1);
      }
    } else if (!description.equals(currentDescription)) {

      result.set(ModComponents.CUSTOM_DESCRIPTION, Component.literal(description));
      namingCost.set(namingCost.get() + 1);
      price.set(price.get() + 1);
    }
  }

  @Unique
  private static String player_item_descriptions$validateDescription(String description) {

    String filtered = StringUtil.filterText(description, true);
    return filtered.length() <= 1024 ? filtered : null;
  }
}
