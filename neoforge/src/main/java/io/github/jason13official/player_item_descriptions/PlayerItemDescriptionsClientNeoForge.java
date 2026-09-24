package io.github.jason13official.player_item_descriptions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;

public class PlayerItemDescriptionsClientNeoForge {

  public PlayerItemDescriptionsClientNeoForge(final IEventBus modEventBus) {

    PlayerItemDescriptionsClient.c2s = PacketDistributor::sendToServer;
  }
}
