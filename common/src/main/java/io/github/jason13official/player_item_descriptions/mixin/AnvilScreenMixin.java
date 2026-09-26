package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.PlayerItemDescriptionsClient;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilScreenAccessor;
import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.input.KeyEvent;
import io.github.jason13official.player_item_descriptions.impl.client.gui.DescriptionPanel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> implements IAnvilScreenAccessor {

  @Shadow private EditBox name;

  @Unique
  private Button player_item_descriptions$button;

  @Unique
  private MultiLineEditBox player_item_descriptions$page;

  @Unique
  private String player_item_descriptions$description = "";

  @Unique
  private int player_item_descriptions$panelX;

  @Unique
  private int player_item_descriptions$panelY;

  /// dummy
  public AnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title, Identifier menuResource) {
    super(menu, inventory, title, menuResource);
  }

  @Inject(at = @At("TAIL"), method = "subInit")
  private void player_item_descriptions$subInit(CallbackInfo ci) {

    this.player_item_descriptions$button = Button.builder(Component.literal("T_"),
        b -> this.player_item_descriptions$togglePage()).bounds(this.leftPos + 154, this.topPos + 47, 16, 16).build();
    this.player_item_descriptions$button.active = this.menu.getSlot(0).hasItem();
    this.addRenderableWidget(this.player_item_descriptions$button);

    // over the item slots, left of the toggle button; the panel draws its own frame and footer, so the box shows neither
    this.player_item_descriptions$panelX = this.leftPos + (this.imageWidth - DescriptionPanel.WIDTH) / 2;
    this.player_item_descriptions$panelY = this.topPos + 35;

    this.player_item_descriptions$page = MultiLineEditBox.builder()
        .setShowDecorations(false)
        .setTextColor(DescriptionPanel.TEXT_COLOR)
        .setCursorColor(DescriptionPanel.TEXT_COLOR)
        .setShowBackground(false)
        .setTextShadow(false)
        .setX(this.player_item_descriptions$panelX + DescriptionPanel.TEXT_BOX_X)
        .setY(this.player_item_descriptions$panelY + DescriptionPanel.TEXT_BOX_Y)
        .build(this.font, DescriptionPanel.TEXT_BOX_WIDTH, DescriptionPanel.TEXT_BOX_HEIGHT, DescriptionPanel.TITLE);
    this.player_item_descriptions$page.setCharacterLimit(DescriptionPanel.CHARACTER_LIMIT);
    this.player_item_descriptions$page.setLineLimit(DescriptionPanel.LINE_LIMIT);
    this.player_item_descriptions$page.setValueListener(this::player_item_descriptions$setDescription);
    this.player_item_descriptions$page.visible = false;
    this.player_item_descriptions$page.active = false;
    this.addWidget(this.player_item_descriptions$page);

    this.player_item_descriptions$readDescription(this.menu.getSlot(0).getItem());
  }

  @Override
  public boolean player_item_descriptions$isPageVisible() {

    return this.player_item_descriptions$page != null && this.player_item_descriptions$page.visible;
  }

  @Override
  public void player_item_descriptions$extractPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {

    if (!this.player_item_descriptions$isPageVisible()) {
      return;
    }

    // draw above the slot items
    graphics.nextStratum();
    DescriptionPanel.extract(graphics, this.font, this.player_item_descriptions$panelX, this.player_item_descriptions$panelY,
        this.player_item_descriptions$page.getValue().length());
    this.player_item_descriptions$page.extractRenderState(graphics, mouseX, mouseY, a);
  }

  @Override
  public boolean player_item_descriptions$blocksMouse(double mouseX, double mouseY) {

    return this.player_item_descriptions$isPageVisible()
        && DescriptionPanel.isMouseOver(this.player_item_descriptions$panelX, this.player_item_descriptions$panelY, mouseX, mouseY)
        && !this.player_item_descriptions$page.isMouseOver(mouseX, mouseY);
  }

  @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
  private void player_item_descriptions$keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {

    if (this.player_item_descriptions$page == null || !this.player_item_descriptions$page.visible) {
      return;
    }

    if (event.isEscape()) {

      this.player_item_descriptions$closePage(true);
      cir.setReturnValue(true);
      return;
    }

    if (this.player_item_descriptions$page.keyPressed(event) || this.player_item_descriptions$page.capturesInput()) {

      cir.setReturnValue(true);
    }
  }

  @Inject(at = @At("TAIL"), method = "slotChanged")
  private void player_item_descriptions$slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack, CallbackInfo ci) {

    if (slotIndex != 0) {
      return;
    }

    this.player_item_descriptions$button.active = !itemStack.isEmpty();
    this.player_item_descriptions$readDescription(itemStack);

    if (itemStack.isEmpty()) {
      this.player_item_descriptions$closePage(false);
      return;
    }

    this.player_item_descriptions$applyDescription(this.player_item_descriptions$description);

    if (this.player_item_descriptions$page.visible) {
      this.setFocused(this.player_item_descriptions$page);
    }
  }

  @Unique
  private void player_item_descriptions$togglePage() {

    if (this.player_item_descriptions$page.visible) {
      this.player_item_descriptions$closePage(true);
    } else {
      this.player_item_descriptions$openPage();
    }
  }

  @Unique
  private void player_item_descriptions$openPage() {

    if (!this.menu.getSlot(0).hasItem()) {
      return;
    }

    this.player_item_descriptions$readDescription(this.menu.getSlot(0).getItem());
    this.player_item_descriptions$page.visible = true;
    this.player_item_descriptions$page.active = true;
    this.name.active = false;
    this.setFocused(this.player_item_descriptions$page);
  }

  @Unique
  private void player_item_descriptions$closePage(boolean apply) {

    this.player_item_descriptions$page.visible = false;
    this.player_item_descriptions$page.active = false;
    this.name.active = true;
    this.setFocused(this.name);

    if (apply) {
      this.player_item_descriptions$applyDescription(this.player_item_descriptions$page.getValue());
    }
  }

  @Unique
  private void player_item_descriptions$readDescription(ItemStack itemStack) {

    Component description = itemStack.get(ModComponents.CUSTOM_DESCRIPTION);
    String value = description == null ? "" : description.getString();

    this.player_item_descriptions$page.setValue(value);
    this.player_item_descriptions$description = value;
  }

  @Unique
  private void player_item_descriptions$applyDescription(String description) {

    this.player_item_descriptions$description = description;

    if (((IAnvilMenuAccessor) this.menu).player_item_descriptions$setItemDescription(description)) {

      PlayerItemDescriptionsClient.c2s.accept(new DescribeItemC2SPacket(description));
    }
  }

  @Unique
  private void player_item_descriptions$setDescription(String newDescription) {

    this.player_item_descriptions$description = newDescription;
  }
}
