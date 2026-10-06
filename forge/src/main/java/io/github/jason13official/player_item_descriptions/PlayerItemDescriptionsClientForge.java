package io.github.jason13official.player_item_descriptions;

import net.minecraft.client.gui.screens.Screen;

public class PlayerItemDescriptionsClientForge {

  public PlayerItemDescriptionsClientForge() {

    PlayerItemDescriptionsClient.c2s = PlayerItemDescriptionsForge.CHANNEL::sendToServer;
    PlayerItemDescriptionsClient.shiftDown = Screen::hasShiftDown;
  }
}
