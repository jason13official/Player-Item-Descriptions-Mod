package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.Constants;
import io.github.jason13official.player_item_descriptions.PlayerItemDescriptionsClient;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

  @Unique
  private static final Component LOCKED = Component.translatable("tooltip." + Constants.MOD_ID + ".description_locked").withStyle(ChatFormatting.GRAY);

  @Inject(at = @At(value = "HEAD"), method = "addDetailsToTooltip")
  private void player_item_descriptions$addDetailsToTooltip(TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {

    ItemStack self = (ItemStack) (Object) this;

    Component description = self.get(ModComponents.CUSTOM_DESCRIPTION);

    if (description != null && display.shows(ModComponents.CUSTOM_DESCRIPTION) && !description.getString().isEmpty()) {

      for (String line : description.getString().split("\n", -1)) {

        builder.accept(Component.literal(line));
      }
    }

    if (self.has(ModComponents.DESCRIPTION_LOCK) && display.shows(ModComponents.DESCRIPTION_LOCK) && PlayerItemDescriptionsClient.shiftDown.getAsBoolean()) {

      builder.accept(LOCKED);
    }
  }
}
