package io.github.jason13official.player_item_descriptions.impl.anvil;

import io.github.jason13official.player_item_descriptions.impl.item.DescriptionData;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class AnvilDescriptions {

  public static final int MAX_LENGTH = 1024;
  public static final int MAX_LINES = 14;

  public static boolean canEdit(ItemStack input, Player player) {

    UUID lock = DescriptionData.getLock(input);
    return lock == null || lock.equals(player.getUUID()) || player.getAbilities().instabuild;
  }

  public static boolean apply(@Nullable String description, @Nullable Boolean locked, ItemStack input, ItemStack result, Player player) {

    if (result.isEmpty() || !canEdit(input, player)) {
      return false;
    }

    boolean described = describe(description, input, result);
    boolean lockChanged = lock(locked, input, result, player);
    return described || lockChanged;
  }

  private static boolean lock(@Nullable Boolean locked, ItemStack input, ItemStack result, Player player) {

    if (locked == null || locked == DescriptionData.hasLock(input)) {
      return false;
    }

    DescriptionData.setLock(result, locked ? player.getUUID() : null);
    return true;
  }

  private static boolean describe(@Nullable String description, ItemStack input, ItemStack result) {

    if (description == null) {
      return false;
    }

    String current = DescriptionData.getDescription(input);
    String currentDescription = current == null ? "" : current;

    if (Util.isBlank(description)) {

      if (currentDescription.isEmpty()) {
        return false;
      }

      DescriptionData.setDescription(result, null);
      return true;
    }

    if (description.equals(currentDescription)) {
      return false;
    }

    DescriptionData.setDescription(result, description);
    return true;
  }

  public static @Nullable String validate(String description) {

    String filtered = filter(description);
    return filtered.length() <= MAX_LENGTH && filtered.chars().filter(character -> character == '\n').count() < MAX_LINES ? filtered : null;
  }

  public static String filter(String description) {

    StringBuilder builder = new StringBuilder();

    for (char character : description.toCharArray()) {

      if (SharedConstants.isAllowedChatCharacter(character) || character == '§' || character == '\n') {
        builder.append(character);
      }
    }

    return builder.toString();
  }
}
