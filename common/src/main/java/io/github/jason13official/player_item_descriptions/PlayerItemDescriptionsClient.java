package io.github.jason13official.player_item_descriptions;

import io.github.jason13official.player_item_descriptions.impl.network.packet.ModPacket;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class PlayerItemDescriptionsClient {

  public static Consumer<ModPacket> c2s;

  public static BooleanSupplier shiftDown = () -> false;
}
