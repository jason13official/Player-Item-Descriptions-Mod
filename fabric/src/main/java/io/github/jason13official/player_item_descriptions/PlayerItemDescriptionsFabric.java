package io.github.jason13official.player_item_descriptions;

import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class PlayerItemDescriptionsFabric implements ModInitializer {

  @Override
  public void onInitialize() {

    PlayerItemDescriptions.init();

    bind(BuiltInRegistries.DATA_COMPONENT_TYPE, ModComponents::register);

    PayloadTypeRegistry.serverboundPlay().register(DescribeItemC2SPacket.TYPE, DescribeItemC2SPacket.STREAM_CODEC);
    ServerPlayNetworking.registerGlobalReceiver(DescribeItemC2SPacket.TYPE, (payload, context) -> {

      // System.out.println("Describe item packet received on server!");

      DescribeItemC2SPacket.handleOnServer(payload, context.player());
    });
  }

  public <T> void bind(Registry<T> registry, Consumer<BiConsumer<T, Identifier>> source) {
    source.accept((t, id) -> Registry.register(registry, id, t));
  }
}
