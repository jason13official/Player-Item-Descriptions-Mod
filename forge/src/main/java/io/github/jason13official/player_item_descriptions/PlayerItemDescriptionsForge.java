package io.github.jason13official.player_item_descriptions;

import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import io.github.jason13official.player_item_descriptions.impl.network.packet.LockDescriptionC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(Constants.MOD_ID)
public class PlayerItemDescriptionsForge {

  private static final String PROTOCOL_VERSION = "1";

  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MOD_ID, "main"),
      () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

  public PlayerItemDescriptionsForge() {

    PlayerItemDescriptions.init();

    CHANNEL.messageBuilder(DescribeItemC2SPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
        .encoder(DescribeItemC2SPacket::write)
        .decoder(DescribeItemC2SPacket::read)
        .consumerMainThread((packet, context) -> {
          ServerPlayer player = context.get().getSender();
          if (player != null) DescribeItemC2SPacket.handleOnServer(packet, player);
        })
        .add();

    CHANNEL.messageBuilder(LockDescriptionC2SPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
        .encoder(LockDescriptionC2SPacket::write)
        .decoder(LockDescriptionC2SPacket::read)
        .consumerMainThread((packet, context) -> {
          ServerPlayer player = context.get().getSender();
          if (player != null) LockDescriptionC2SPacket.handleOnServer(packet, player);
        })
        .add();

    if (FMLEnvironment.dist == Dist.CLIENT) {

      new PlayerItemDescriptionsClientForge();
    }
  }
}
