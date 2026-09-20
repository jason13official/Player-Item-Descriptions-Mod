package io.github.jason13official.player_item_descriptions.impl.network.packet;

import io.github.jason13official.player_item_descriptions.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.Identifier;

public class DescribeItemC2SPacket implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<DescribeItemC2SPacket> TYPE = new Type<DescribeItemC2SPacket>(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "describe_item"));

  public static final StreamCodec<FriendlyByteBuf, DescribeItemC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(DescribeItemC2SPacket::write, DescribeItemC2SPacket::new);

  private final String name;

  public DescribeItemC2SPacket(String name) {
    this.name = name;
  }

  private DescribeItemC2SPacket(FriendlyByteBuf input) {
    this.name = input.readUtf();
  }

  private void write(FriendlyByteBuf output) {
    output.writeUtf(this.name);
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
