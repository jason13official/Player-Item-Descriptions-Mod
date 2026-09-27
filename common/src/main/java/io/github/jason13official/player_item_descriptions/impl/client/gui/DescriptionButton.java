package io.github.jason13official.player_item_descriptions.impl.client.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class DescriptionButton extends Button.Plain {

  public DescriptionButton(int x, int y, OnPress onPress) {
    super(x, y, 16, 16, Component.literal("T_"), onPress, DEFAULT_NARRATION);
  }

  @Override
  public boolean shouldTakeFocusAfterInteraction() {
    return false;
  }
}
