package io.github.jason13official.player_item_descriptions.mixin;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

  @Unique
  private Button player_item_descriptions$button;

  @Unique
  private MultiLineEditBox player_item_descriptions$page;

  @Unique
  private String player_item_descriptions$description = "";

  @Inject(at = @At("TAIL"), method = "subInit")
  private void player_item_descriptions$constructor(CallbackInfo ci) {

    AnvilScreen self = (AnvilScreen) (Object) this;

    // 176, 166
    int xo = (self.width - 176) / 2;
    int yo = (self.height - 166) / 2;

    player_item_descriptions$button = Button.builder(Component.literal("T_"),
        b -> this.player_item_descriptions$onButtonClick()).bounds(xo + 154, yo + 47, 16, 16).build();

    self.addRenderableWidget(player_item_descriptions$button);

    this.player_item_descriptions$page = MultiLineEditBox.builder().setShowDecorations(false).setTextColor(0xFFFFFFFF).setCursorColor(0xFFFFFFFF).setShowBackground(true).setTextShadow(false).setX((self.width - 114) / 2 - 8).setY(28).build(self.getFont(), 122, 134, CommonComponents.EMPTY);
    this.player_item_descriptions$page.setCharacterLimit(1024);
    this.player_item_descriptions$page.setLineLimit(14);
    this.player_item_descriptions$page.setValueListener(this::player_item_descriptions$setDescription);
    this.player_item_descriptions$page.visible = false;
    self.addRenderableWidget(this.player_item_descriptions$page);
  }

  @Unique
  private void player_item_descriptions$onButtonClick() {

    System.out.println("Player pressed button!");

    // toggle the visibility of the multi-line edit box
    this.player_item_descriptions$page.visible = !this.player_item_descriptions$page.visible;

    if (!this.player_item_descriptions$page.visible) {

      if (player_item_descriptions$getDescription().equalsIgnoreCase("")) {
        // if empty new description, and we had a description, remove it / set to empty
      } else {
        // set the new description
      }
    } else {

      // get original description, if any
      // populate our edit box
    }
  }

  @Unique
  private String player_item_descriptions$getDescription() {

    return player_item_descriptions$description;
  }

  @Unique
  private void player_item_descriptions$setDescription(String newDescription) {

    this.player_item_descriptions$description = newDescription;
  }
}
