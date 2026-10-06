package io.github.jason13official.player_item_descriptions.impl.network.packet;

import io.github.jason13official.player_item_descriptions.Constants;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.impl.anvil.AnvilDescriptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;

public record DescribeItemC2SPacket(String description) implements ModPacket {

  public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "describe_item");

  public static DescribeItemC2SPacket read(FriendlyByteBuf input) {
    return new DescribeItemC2SPacket(input.readUtf(AnvilDescriptions.MAX_LENGTH));
  }

  @Override
  public void write(FriendlyByteBuf output) {
    output.writeUtf(this.description, AnvilDescriptions.MAX_LENGTH);
  }

  @Override
  public ResourceLocation id() {
    return ID;
  }

  public static void handleOnServer(DescribeItemC2SPacket packet, ServerPlayer player) {

    if (player.containerMenu instanceof AnvilMenu menu) {
      if (!menu.stillValid(player)) {
        Constants.LOG.debug("Player {} interacted with invalid menu {}", player, menu);
        return;
      }

      ((IAnvilMenuAccessor) menu).player_item_descriptions$setItemDescription(packet.description());
    }
  }
}
