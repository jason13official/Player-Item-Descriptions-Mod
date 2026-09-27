package io.github.jason13official.player_item_descriptions.mixin;

import io.github.jason13official.player_item_descriptions.PlayerItemDescriptionsClient;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilScreenAccessor;
import io.github.jason13official.player_item_descriptions.impl.anvil.AnvilDescriptions;
import io.github.jason13official.player_item_descriptions.impl.client.gui.DescriptionEditBox;
import io.github.jason13official.player_item_descriptions.impl.client.gui.DescriptionPanel;
import io.github.jason13official.player_item_descriptions.impl.client.gui.SlotChangeListener;
import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
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
  private DescriptionEditBox player_item_descriptions$page;

  @Unique
  private SlotChangeListener player_item_descriptions$slotListener;

  @Unique
  private int player_item_descriptions$panelX;

  @Unique
  private int player_item_descriptions$panelY;

  /// dummy
  public AnvilScreenMixin(AnvilMenu menu, Inventory inventory, Component title, ResourceLocation menuResource) {
    super(menu, inventory, title, menuResource);
  }

  @Override
  public void player_item_descriptions$init() {

    this.player_item_descriptions$removed();

    this.player_item_descriptions$button = Button.builder(Component.literal("T_"),
        b -> this.player_item_descriptions$togglePage()).bounds(this.leftPos + 154, this.topPos + 47, 16, 16).build();
    this.player_item_descriptions$button.active = this.menu.getSlot(0).hasItem();
    this.addRenderableWidget(this.player_item_descriptions$button);

    this.player_item_descriptions$panelX = this.leftPos + (this.imageWidth - DescriptionPanel.WIDTH) / 2;
    this.player_item_descriptions$panelY = this.topPos + 35;

    this.player_item_descriptions$page = new DescriptionEditBox(this.font,
        this.player_item_descriptions$panelX + DescriptionPanel.TEXT_BOX_X, this.player_item_descriptions$panelY + DescriptionPanel.TEXT_BOX_Y,
        DescriptionPanel.TEXT_BOX_WIDTH, DescriptionPanel.TEXT_BOX_HEIGHT,
        AnvilDescriptions.MAX_LENGTH, AnvilDescriptions.MAX_LINES);
    this.player_item_descriptions$page.setValueListener(this::player_item_descriptions$applyDescription);
    this.player_item_descriptions$page.visible = false;
    this.player_item_descriptions$page.active = false;
    this.addWidget(this.player_item_descriptions$page);

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
  public boolean player_item_descriptions$isOverPanel(double mouseX, double mouseY) {

    return this.player_item_descriptions$isPageVisible()
        && DescriptionPanel.isMouseOver(this.player_item_descriptions$panelX, this.player_item_descriptions$panelY, mouseX, mouseY);
  }

  @Override
  public void player_item_descriptions$mouseClicked() {

    if (this.getFocused() == this.player_item_descriptions$button) {
      this.setFocused(this.player_item_descriptions$isPageVisible() ? this.player_item_descriptions$page : this.name);
    }
  }

  @Override
  public boolean player_item_descriptions$isPageVisible() {

    return this.player_item_descriptions$page != null && this.player_item_descriptions$page.visible;
  }

  @Inject(at = @At("TAIL"), method = "renderFg")
  private void player_item_descriptions$renderFg(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {

    if (!this.player_item_descriptions$isPageVisible()) {
      return;
    }

    graphics.pose().pushPose();
    graphics.pose().translate(0.0F, 0.0F, 400.0F);
    DescriptionPanel.render(graphics, this.font, this.player_item_descriptions$panelX, this.player_item_descriptions$panelY,
        this.player_item_descriptions$page.getValue().length());
    this.player_item_descriptions$page.render(graphics, mouseX, mouseY, partialTick);
    graphics.pose().popPose();
  }

  @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
  private void player_item_descriptions$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {

    if (!this.player_item_descriptions$isPageVisible()) {
      return;
    }

    if (keyCode == GLFW.GLFW_KEY_ESCAPE) {

      this.player_item_descriptions$closePage();
      cir.setReturnValue(true);
      return;
    }

    if (this.player_item_descriptions$page.keyPressed(keyCode, scanCode, modifiers) || this.player_item_descriptions$page.isFocused()) {

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
      this.player_item_descriptions$closePage();
      return;
    }

    if (this.player_item_descriptions$page.visible) {
      this.name.setEditable(false);
      this.setFocused(this.player_item_descriptions$page);
    }
  }

  @Unique
  private void player_item_descriptions$togglePage() {

    if (this.player_item_descriptions$page.visible) {
      this.player_item_descriptions$closePage();
    } else {
      this.player_item_descriptions$openPage();
    }
  }

  @Unique
  private void player_item_descriptions$openPage() {

    if (!this.menu.getSlot(0).hasItem()) {
      return;
    }

    this.player_item_descriptions$page.visible = true;
    this.player_item_descriptions$page.active = true;
    this.name.setEditable(false);
    this.setFocused(this.player_item_descriptions$page);
  }

  @Unique
  private void player_item_descriptions$closePage() {

    this.player_item_descriptions$page.visible = false;
    this.player_item_descriptions$page.active = false;
    this.name.setEditable(this.menu.getSlot(0).hasItem());
    this.setFocused(this.name);
  }

  @Unique
  private void player_item_descriptions$readDescription(ItemStack itemStack) {

    Component description = itemStack.get(ModComponents.CUSTOM_DESCRIPTION);
    this.player_item_descriptions$page.setValue(description == null ? "" : description.getString());
  }

  @Unique
  private void player_item_descriptions$applyDescription(String description) {

    if (this.menu.getSlot(0).hasItem() && ((IAnvilMenuAccessor) this.menu).player_item_descriptions$setItemDescription(description)) {

      PlayerItemDescriptionsClient.c2s.accept(new DescribeItemC2SPacket(description));
    }
  }
}
