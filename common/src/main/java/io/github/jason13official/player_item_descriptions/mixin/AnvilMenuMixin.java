package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import net.minecraft.world.inventory.AnvilMenu;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin implements IAnvilMenuAccessor {

  @Shadow private @Nullable String itemName;

  @Override
  public String player_item_descriptions$getItemName() {
    return this.itemName;
  }

  @Override
  public void player_item_descriptions$setItemName(String name) {
    this.itemName = name;
  }
}
