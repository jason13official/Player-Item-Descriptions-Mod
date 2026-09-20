package io.github.jason13official.player_item_descriptions.impl.network.packet;

import io.github.jason13official.player_item_descriptions.Constants;
import io.github.jason13official.player_item_descriptions.api.common.access.IAnvilMenuAccessor;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;

public class DescribeItemC2SPacket implements CustomPacketPayload {

  public static final CustomPacketPayload.Type<DescribeItemC2SPacket> TYPE = new Type<DescribeItemC2SPacket>(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "describe_item"));

  public static final StreamCodec<FriendlyByteBuf, DescribeItemC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(DescribeItemC2SPacket::write, DescribeItemC2SPacket::new);

  private final String description;

  public DescribeItemC2SPacket(String description) {
    this.description = description;
  }

  private DescribeItemC2SPacket(FriendlyByteBuf input) {
    this.description = input.readUtf();
  }

  private void write(FriendlyByteBuf output) {
    output.writeUtf(this.description);
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

      // menu.setItemName(packet.getName());

      setItemDescription(menu, packet.getDescription());
    }
  }

  public static boolean setItemDescription(AnvilMenu menu, String newDescription) {
    String validatedDesc = validateDesc(newDescription);

    IAnvilMenuAccessor accessor = (IAnvilMenuAccessor) menu;

    // if (validatedDesc != null && !validatedDesc.equals(accessor.player_item_descriptions$getItemName())) {
    if (validatedDesc != null && (!menu.getSlot(2).getItem().has(ModComponents.CUSTOM_DESCRIPTION) || !validatedDesc.equals(menu.getSlot(2).getItem().get(ModComponents.CUSTOM_DESCRIPTION).getString()))) {

      // menu.itemName = validatedName;
      // accessor.player_item_descriptions$setItemName(validatedDesc);
      menu.getSlot(2).getItem().set(ModComponents.CUSTOM_DESCRIPTION, Component.literal(newDescription));

      if (menu.getSlot(2).hasItem()) {
        ItemStack itemStack = menu.getSlot(2).getItem();
        if (StringUtil.isBlank(validatedDesc)) {
          itemStack.remove(ModComponents.CUSTOM_DESCRIPTION);
        } else {
          itemStack.set(ModComponents.CUSTOM_DESCRIPTION, Component.literal(validatedDesc));
        }
      }

      menu.createResult();
      return true;
    } else {
      return false;
    }
  }

  private static String validateDesc(String description) {
    String filteredName = StringUtil.filterText(description);
    return filteredName.length() <= 1024 ? filteredName : null;
  }
}
