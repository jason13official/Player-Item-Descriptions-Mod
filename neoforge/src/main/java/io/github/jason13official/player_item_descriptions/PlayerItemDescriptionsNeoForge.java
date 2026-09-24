package io.github.jason13official.player_item_descriptions;

import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class PlayerItemDescriptionsNeoForge {

  public static IEventBus EVENT_BUS;

  public PlayerItemDescriptionsNeoForge(IEventBus modEventBus) {

    EVENT_BUS = modEventBus;

    PlayerItemDescriptions.init();

    bind(Registries.DATA_COMPONENT_TYPE, ModComponents::register);

    // RegisterPayloadHandlersEvent
    modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {

      PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);
      registrar.playToServer(DescribeItemC2SPacket.TYPE, DescribeItemC2SPacket.STREAM_CODEC, (payload, context) -> {

        // System.out.println("Describe item packet received on server!");
        if (context.player() instanceof ServerPlayer player) DescribeItemC2SPacket.handleOnServer(payload, player);
      });
    });

    if (FMLLoader.getDist() == Dist.CLIENT) {

      new PlayerItemDescriptionsClientNeoForge(modEventBus);
    }
  }

  public <T> void bind(ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
    EVENT_BUS.addListener((Consumer<RegisterEvent>) event -> {
      if (registry.equals(event.getRegistryKey())) {
        source.accept((t, id) -> event.register(registry, id, () -> t));
      }
    });
  }
}