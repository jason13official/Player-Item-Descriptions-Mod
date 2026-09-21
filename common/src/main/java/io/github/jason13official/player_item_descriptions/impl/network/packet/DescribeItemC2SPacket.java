package io.github.jason13official.player_item_descriptions.impl.network.packet;

import io.github.jason13official.player_item_descriptions.Constants;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;

public class DescribeItemC2SPacket implements CustomPacketPayload {

  public static final int MAX_DESCRIPTION_LENGTH = 1024;

  public static final CustomPacketPayload.Type<DescribeItemC2SPacket> TYPE = new Type<DescribeItemC2SPacket>(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "describe_item"));

  public static final StreamCodec<FriendlyByteBuf, DescribeItemC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(DescribeItemC2SPacket::write, DescribeItemC2SPacket::new);

  private final String description;

  public DescribeItemC2SPacket(String description) {
    this.description = description;
  }

  private DescribeItemC2SPacket(FriendlyByteBuf input) {
    this.description = input.readUtf(MAX_DESCRIPTION_LENGTH);
  }

  private void write(FriendlyByteBuf output) {
    output.writeUtf(this.description, MAX_DESCRIPTION_LENGTH);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public String getDescription() {

    return description;
  }

  public static void handleOnServer(DescribeItemC2SPacket packet, ServerPlayer player) {

    if (player.containerMenu instanceof AnvilMenu menu) {
      if (!menu.stillValid(player)) {
        Constants.LOG.debug("Player {} interacted with invalid menu {}", player, menu);
        return;
      }

      ((IAnvilMenuAccessor) menu).player_item_descriptions$setItemDescription(packet.getDescription());
    }
  }
}
