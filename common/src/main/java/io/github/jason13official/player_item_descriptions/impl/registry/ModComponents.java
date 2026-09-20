package io.github.jason13official.player_item_descriptions.impl.registry;

import io.github.jason13official.player_item_descriptions.Constants;
import java.util.function.BiConsumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

public class ModComponents {

  public static DataComponentType<Component> CUSTOM_DESCRIPTION;

  public static void register(BiConsumer<DataComponentType<?>, Identifier> consumer) {

     CUSTOM_DESCRIPTION = DataComponentType.<Component>builder().persistent(ComponentSerialization.CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).cacheEncoding().build();

     consumer.accept(CUSTOM_DESCRIPTION, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "custom_description"));
  }
}
