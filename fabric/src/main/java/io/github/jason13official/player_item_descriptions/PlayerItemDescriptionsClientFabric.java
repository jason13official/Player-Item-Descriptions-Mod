package io.github.jason13official.player_item_descriptions;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;

public class PlayerItemDescriptionsClientFabric implements ClientModInitializer {

  @Override
  public void onInitializeClient() {

    PlayerItemDescriptionsClient.c2s = packet -> {
      FriendlyByteBuf buf = PacketByteBufs.create();
      packet.write(buf);
      ClientPlayNetworking.send(packet.id(), buf);
    };
    PlayerItemDescriptionsClient.shiftDown = Screen::hasShiftDown;
  }
}
