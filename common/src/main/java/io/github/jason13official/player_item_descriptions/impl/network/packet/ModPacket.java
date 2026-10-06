package io.github.jason13official.player_item_descriptions.impl.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface ModPacket {

  ResourceLocation id();

  void write(FriendlyByteBuf output);
}
