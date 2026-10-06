package io.github.jason13official.player_item_descriptions;

import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import io.github.jason13official.player_item_descriptions.impl.network.packet.LockDescriptionC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PlayerItemDescriptionsFabric implements ModInitializer {

  @Override
  public void onInitialize() {

    PlayerItemDescriptions.init();

    ServerPlayNetworking.registerGlobalReceiver(DescribeItemC2SPacket.ID, (server, player, handler, buf, responseSender) -> {
      DescribeItemC2SPacket packet = DescribeItemC2SPacket.read(buf);
      server.execute(() -> DescribeItemC2SPacket.handleOnServer(packet, player));
    });

    ServerPlayNetworking.registerGlobalReceiver(LockDescriptionC2SPacket.ID, (server, player, handler, buf, responseSender) -> {
      LockDescriptionC2SPacket packet = LockDescriptionC2SPacket.read(buf);
      server.execute(() -> LockDescriptionC2SPacket.handleOnServer(packet, player));
    });
  }
}
