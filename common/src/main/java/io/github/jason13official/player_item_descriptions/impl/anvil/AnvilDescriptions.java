package io.github.jason13official.player_item_descriptions.impl.anvil;

import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class AnvilDescriptions {

  public static boolean apply(@Nullable String description, ItemStack input, ItemStack result) {

    if (description == null) {
      return false;
    }

    Component current = input.get(ModComponents.CUSTOM_DESCRIPTION);
    String currentDescription = current == null ? "" : current.getString();

    if (StringUtil.isBlank(description)) {

      if (currentDescription.isEmpty()) {
        return false;
      }

      result.remove(ModComponents.CUSTOM_DESCRIPTION);
      return true;
    }

    if (description.equals(currentDescription)) {
      return false;
    }

    result.set(ModComponents.CUSTOM_DESCRIPTION, Component.literal(description));
    return true;
  }

  public static String filter(String description) {

    StringBuilder builder = new StringBuilder();

    for (char character : description.toCharArray()) {

      if (StringUtil.isAllowedChatCharacter(character) || character == '§' || character == '\n') {
        builder.append(character);
      }
    }

    return builder.toString();
  }
}
