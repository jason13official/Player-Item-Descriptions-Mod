package io.github.jason13official.player_item_descriptions.impl.anvil;

import io.github.jason13official.player_item_descriptions.impl.registry.ModComponents;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class AnvilDescriptions {

  public static final int MAX_LENGTH = 1024;
  public static final int MAX_LINES = 14;

  public static boolean canEdit(ItemStack input, Player player) {

    UUID lock = input.get(ModComponents.DESCRIPTION_LOCK);
    return lock == null || lock.equals(player.getUUID()) || player.getAbilities().instabuild;
  }

  public static boolean apply(@Nullable String description, @Nullable Boolean locked, ItemStack input, ItemStack result, Player player) {

    if (!canEdit(input, player)) {
      return false;
    }

    boolean described = describe(description, input, result);
    boolean lockChanged = lock(locked, input, result, player);
    return described || lockChanged;
  }

  private static boolean lock(@Nullable Boolean locked, ItemStack input, ItemStack result, Player player) {

    if (locked == null || locked == input.has(ModComponents.DESCRIPTION_LOCK)) {
      return false;
    }

    if (locked) {
      result.set(ModComponents.DESCRIPTION_LOCK, player.getUUID());
    } else {
      result.remove(ModComponents.DESCRIPTION_LOCK);
    }

    return true;
  }

  private static boolean describe(@Nullable String description, ItemStack input, ItemStack result) {

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

  public static @Nullable String validate(String description) {

    String filtered = filter(description);
    return filtered.length() <= MAX_LENGTH && filtered.chars().filter(character -> character == '\n').count() < MAX_LINES ? filtered : null;
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
