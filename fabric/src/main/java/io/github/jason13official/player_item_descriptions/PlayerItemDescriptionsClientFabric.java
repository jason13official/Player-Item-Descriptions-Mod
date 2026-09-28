package io.github.jason13official.player_item_descriptions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.Screen;

public class PlayerItemDescriptionsClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    PlayerItemDescriptionsClient.c2s = ClientPlayNetworking::send;
    PlayerItemDescriptionsClient.shiftDown = Screen::hasShiftDown;
  }
}
