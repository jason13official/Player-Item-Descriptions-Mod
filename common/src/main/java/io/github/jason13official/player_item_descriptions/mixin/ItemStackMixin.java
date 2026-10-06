package io.github.jason13official.player_item_descriptions.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.jason13official.player_item_descriptions.Constants;
import io.github.jason13official.player_item_descriptions.PlayerItemDescriptionsClient;
import io.github.jason13official.player_item_descriptions.impl.item.DescriptionData;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

  @Unique
  private static final Component LOCKED = Component.translatable("tooltip." + Constants.MOD_ID + ".description_locked").withStyle(ChatFormatting.GRAY);

  @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER), method = "getTooltipLines")
  private void player_item_descriptions$getTooltipLines(@Nullable Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {

    ItemStack self = (ItemStack) (Object) this;

    String description = DescriptionData.getDescription(self);

    if (description != null && !description.isEmpty()) {

      for (String line : description.split("\n", -1)) {

        list.add(Component.literal(line));
      }
    }

    if (DescriptionData.hasLock(self) && PlayerItemDescriptionsClient.shiftDown.getAsBoolean()) {

      list.add(LOCKED);
    }
  }
}
