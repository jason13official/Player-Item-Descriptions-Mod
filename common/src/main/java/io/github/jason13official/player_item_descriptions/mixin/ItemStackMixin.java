package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider.Getter;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

  @Shadow public abstract <T> void addToTooltip(DataComponentType<T> type, Getter<T> tooltipGetter, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag);

  @Inject(at = @At(value = "HEAD"), method = "addDetailsToTooltip")
  private void player_item_descriptions$getTooltipLines(TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {

    ItemStack self = (ItemStack) (Object) this;

    if (self.has(ModComponents.CUSTOM_DESCRIPTION)) {

      builder.accept(self.get(ModComponents.CUSTOM_DESCRIPTION));
    }
  }
}
