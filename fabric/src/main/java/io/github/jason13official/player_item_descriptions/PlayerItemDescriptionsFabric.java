package io.github.jason13official.player_item_descriptions;

import io.github.jason13official.player_item_descriptions.impl.network.packet.DescribeItemC2SPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PlayerItemDescriptionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        PlayerItemDescriptions.init();

      PayloadTypeRegistry.serverboundPlay().register(DescribeItemC2SPacket.TYPE, DescribeItemC2SPacket.STREAM_CODEC);
      ServerPlayNetworking.registerGlobalReceiver(DescribeItemC2SPacket.TYPE, (payload, context) -> {

        System.out.println("Describe item packet received on server!");
      });
    }
}
