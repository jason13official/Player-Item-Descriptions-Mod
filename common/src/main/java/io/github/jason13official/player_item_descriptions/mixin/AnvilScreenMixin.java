package io.github.jason13official.player_item_descriptions.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

  @Unique
  private Button player_item_descriptions$button;

  @Inject(at = @At("TAIL"), method = "subInit")
  private void player_item_descriptions$constructor(CallbackInfo ci) {

    AnvilScreen self = (AnvilScreen) (Object) this;

    // 176, 166
    int xo = (self.width - 176) / 2;
    int yo = (self.height - 166) / 2;

    player_item_descriptions$button = Button.builder(Component.literal("T_"),
        b -> this.player_item_descriptions$onButtonClick()).bounds(xo + 154, yo + 47, 16, 16).build();

    self.addRenderableWidget(player_item_descriptions$button);
  }

  @Unique
  private void player_item_descriptions$onButtonClick() {

    System.out.println("Player pressed button!");
  }
}
