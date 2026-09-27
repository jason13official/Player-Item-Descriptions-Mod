package io.github.jason13official.player_item_descriptions.impl.client.gui;

import java.util.function.BiConsumer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public record SlotChangeListener(BiConsumer<Integer, ItemStack> onSlotChanged) implements ContainerListener {

  @Override
  public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {
    this.onSlotChanged.accept(slotIndex, itemStack);
  }

  @Override
  public void dataChanged(AbstractContainerMenu container, int id, int value) {
  }
}
