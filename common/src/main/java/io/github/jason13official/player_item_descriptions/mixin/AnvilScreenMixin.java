package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.PlayerItemDescriptionsClient;
import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {

  @Shadow private EditBox name;
  @Unique
  private Button player_item_descriptions$button;

  @Unique
  private MultiLineEditBox player_item_descriptions$page;

  @Unique
  private String player_item_descriptions$description = "";

  /// dummy
  public AnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title, Identifier menuResource) {
    super(menu, inventory, title, menuResource);
  }

//  @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
//  private void player_item_descriptions$keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
//
////    if (!this.player_item_descriptions$page.visible && !this.player_item_descriptions$page.keyPressed(event) && !this.player_item_descriptions$pageCanConsumeInput()) {
////
////      return;
////    }
//
//
//  }

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
    this.player_item_descriptions$page.active = false;
    this.player_item_descriptions$page.setCharacterLimit(1024);
    this.player_item_descriptions$page.setLineLimit(14);
    this.player_item_descriptions$page.setValueListener(this::player_item_descriptions$setDescription);
    this.player_item_descriptions$page.visible = false;
    self.addRenderableWidget(this.player_item_descriptions$page);
  }

  @Unique
  private void player_item_descriptions$onButtonClick() {

    System.out.println("Player pressed button!");

    AnvilScreen self = (AnvilScreen) (Object) this;


    // toggle the visibility of the multi-line edit box
    this.player_item_descriptions$page.visible = !this.player_item_descriptions$page.visible;

    Slot slot = ((AnvilMenu)this.menu).getSlot(0);

    if (!slot.hasItem()) {
      return;
    }

    if (!this.player_item_descriptions$page.visible) {

      this.name.active = true;
      this.player_item_descriptions$page.active = false;

      if (player_item_descriptions$getDescription().isEmpty() && !slot.getItem().get(ModComponents.CUSTOM_DESCRIPTION).getString().isEmpty()) {
        // if empty new description, and we had a description, remove it / set to empty
      } else {
        // set the new description

        if (!this.player_item_descriptions$description.equals(slot.getItem().get(ModComponents.CUSTOM_DESCRIPTION))) {

          // sets input slot
          // slot.getItem().set(ModComponents.CUSTOM_DESCRIPTION, Component.literal(this.player_item_descriptions$getDescription()));

          // if (((AnvilMenu)this.menu).setItemName(player_item_descriptions$description)) {
          // if (this.player_item_descriptions$setItemDescription(this.player_item_descriptions$description)) {
          if (DescribeItemC2SPacket.setItemDescription(((AnvilMenu)this.menu), this.player_item_descriptions$description)) {
            // this.minecraft.player.connection.send(new ServerboundRenameItemPacket(player_item_descriptions$description));
            PlayerItemDescriptionsClient.c2s.accept(new DescribeItemC2SPacket(this.player_item_descriptions$description));
          }
        }

      }
    } else {

      this.name.active = false;
      this.player_item_descriptions$page.active = true;

      // get original description, if any
      // populate our edit box
      if (slot.getItem().has(ModComponents.CUSTOM_DESCRIPTION) && slot.getItem().get(ModComponents.CUSTOM_DESCRIPTION).getString().isEmpty()) {
        this.player_item_descriptions$setDescription(slot.getItem().get(ModComponents.CUSTOM_DESCRIPTION).getString());
      }
    }
  }

  @Inject(at = @At("TAIL"), method = "slotChanged")
  private void player_item_descriptions$slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack, CallbackInfo ci) {

    // if first slot updated and not empty, has description
    if (slotIndex == 0 && !itemStack.isEmpty() && itemStack.has(ModComponents.CUSTOM_DESCRIPTION)) {
      this.player_item_descriptions$setDescription(itemStack.get(ModComponents.CUSTOM_DESCRIPTION).getString());
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

  @Unique
  public boolean player_item_descriptions$pageCanConsumeInput() {

    return true;
    // return this.player_item_descriptions$page.isActive() && this.player_item_descriptions$page.isFocused(); // && this.player_item_descriptions$page.isEditable();
  }

  @Unique
  private boolean player_item_descriptions$setItemDescription(String newDescription) {

    String validatedDesc = validateDesc(newDescription);
    if (validatedDesc != null && !validatedDesc.equals(this.player_item_descriptions$description)) {
      this.player_item_descriptions$description = validatedDesc;
      if (((AnvilMenu)this.menu).getSlot(2).hasItem()) {
        ItemStack itemStack = ((AnvilMenu)this.menu).getSlot(2).getItem();
        if (StringUtil.isBlank(validatedDesc)) {
          itemStack.remove(ModComponents.CUSTOM_DESCRIPTION);
        } else {
          itemStack.set(ModComponents.CUSTOM_DESCRIPTION, Component.literal(validatedDesc));
        }
      }

      ((AnvilMenu)this.menu).createResult();
      return true;
    } else {
      return false;
    }
  }

  private static @Nullable String validateDesc(String name) {
    String filteredName = StringUtil.filterText(name);
    return filteredName.length() <= 1024 ? filteredName : null;
  }
}
