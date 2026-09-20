package io.github.jason13official.player_item_descriptions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class PlayerItemDescriptionsClientNeoForge {

  public PlayerItemDescriptionsClientNeoForge(final IEventBus modEventBus) {

    PlayerItemDescriptionsClient.c2s = ClientPacketDistributor::sendToServer;
  }
}
