package io.github.jason13official.player_item_descriptions.impl.network.packet;

import io.github.jason13official.player_item_descriptions.Constants;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;

public record LockDescriptionC2SPacket(boolean locked) implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<LockDescriptionC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "lock_description"));

  public static final StreamCodec<ByteBuf, LockDescriptionC2SPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, LockDescriptionC2SPacket::locked, LockDescriptionC2SPacket::new);

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handleOnServer(LockDescriptionC2SPacket packet, ServerPlayer player) {

    if (player.containerMenu instanceof AnvilMenu menu) {
      if (!menu.stillValid(player)) {
        Constants.LOG.debug("Player {} interacted with invalid menu {}", player, menu);
        return;
      }

      ((IAnvilMenuAccessor) menu).player_item_descriptions$setItemLock(packet.locked());
    }
  }
}
