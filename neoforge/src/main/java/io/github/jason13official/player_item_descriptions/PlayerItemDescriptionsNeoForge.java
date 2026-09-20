package io.github.jason13official.player_item_descriptions;

import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import java.util.function.Consumer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MOD_ID)
public class PlayerItemDescriptionsNeoForge {

  public PlayerItemDescriptionsNeoForge(IEventBus modEventBus) {

    // This method is invoked by the NeoForge mod loader when it is ready
    // to load your mod. You can access NeoForge and Common code in this
    // project.

    // Use NeoForge to bootstrap the Common mod.
    Constants.LOG.info("Hello NeoForge world!");
    PlayerItemDescriptions.init();

    // RegisterPayloadHandlersEvent
    modEventBus.addListener((RegisterPayloadHandlersEvent event) -> {

      PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);
      registrar.playToServer(DescribeItemC2SPacket.TYPE, DescribeItemC2SPacket.STREAM_CODEC, (payload, context) -> {

        System.out.println("Describe item packet received on server!");
      });
    });

    if (FMLLoader.getCurrent().getDist() == Dist.CLIENT) {

      new PlayerItemDescriptionsClientNeoForge(modEventBus);
    }
  }
}