package io.github.jason13official.player_item_descriptions.impl.network.packet;

import io.github.jason13official.player_item_descriptions.Constants;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;

public record LockDescriptionC2SPacket(boolean locked) implements ModPacket {

  public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "lock_description");

  public static LockDescriptionC2SPacket read(FriendlyByteBuf input) {
    return new LockDescriptionC2SPacket(input.readBoolean());
  }

  @Override
  public void write(FriendlyByteBuf output) {
    output.writeBoolean(this.locked);
  }

  @Override
  public ResourceLocation id() {
    return ID;
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
