package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.PlayerItemDescriptionsClient;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilScreenAccessor;
import io.github.jason13official.player_item_descriptions.api.common.access.IFormattingAccessor;
import io.github.jason13official.player_item_descriptions.impl.anvil.AnvilDescriptions;
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
import io.github.jason13official.player_item_descriptions.impl.client.gui.SlotChangeListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> implements IAnvilScreenAccessor {

  @Shadow private EditBox name;

  @Unique
  private Button player_item_descriptions$button;

  @Unique
  private MultiLineEditBox player_item_descriptions$page;

  @Unique
  private SlotChangeListener player_item_descriptions$slotListener;

  @Unique
  private int player_item_descriptions$panelX;

  @Unique
  private int player_item_descriptions$panelY;

  /// dummy
  public AnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title, Identifier menuResource) {
    super(menu, inventory, title, menuResource);
  }

  @Override
  public void player_item_descriptions$init() {

    this.player_item_descriptions$removed();

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
    this.player_item_descriptions$page.setCharacterLimit(AnvilDescriptions.MAX_LENGTH);
    this.player_item_descriptions$page.setLineLimit(DescriptionPanel.LINE_LIMIT);
    ((IFormattingAccessor) this.player_item_descriptions$page).player_item_descriptions$setAllowFormatting(true);
    this.player_item_descriptions$page.visible = false;
    this.player_item_descriptions$page.active = false;
    this.addWidget(this.player_item_descriptions$page);

    // init also runs on resize, when the menu may already hold a pending description
    String pending = ((IAnvilMenuAccessor) this.menu).player_item_descriptions$getItemDescription();

    if (pending != null) {
      this.player_item_descriptions$page.setValue(pending);
    } else {
      this.player_item_descriptions$readDescription(this.menu.getSlot(0).getItem());
    }

    this.player_item_descriptions$slotListener = new SlotChangeListener(this::player_item_descriptions$slotChanged);
    this.menu.addSlotListener(this.player_item_descriptions$slotListener);
  }

  @Override
  public void player_item_descriptions$removed() {

    if (this.player_item_descriptions$slotListener != null) {
      this.menu.removeSlotListener(this.player_item_descriptions$slotListener);
      this.player_item_descriptions$slotListener = null;
    }
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

    if (!this.player_item_descriptions$isPageVisible()) {
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

  @Unique
  private void player_item_descriptions$slotChanged(int slotIndex, ItemStack itemStack) {

    if (slotIndex != 0) {
      return;
    }

    this.player_item_descriptions$button.active = !itemStack.isEmpty();
    this.player_item_descriptions$readDescription(itemStack);

    if (itemStack.isEmpty()) {
      this.player_item_descriptions$closePage(false);
      return;
    }

    this.player_item_descriptions$applyDescription(this.player_item_descriptions$page.getValue());

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

    // the page still holds the pending description; re-reading the input item would drop it,
    // since only the output item carries a description that hasn't been taken yet
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
    this.player_item_descriptions$page.setValue(description == null ? "" : description.getString());
  }

  @Unique
  private void player_item_descriptions$applyDescription(String description) {

    if (((IAnvilMenuAccessor) this.menu).player_item_descriptions$setItemDescription(description)) {

      PlayerItemDescriptionsClient.c2s.accept(new DescribeItemC2SPacket(description));
    }
  }
}
