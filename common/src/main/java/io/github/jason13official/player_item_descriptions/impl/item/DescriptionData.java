package io.github.jason13official.player_item_descriptions.impl.item;

import io.github.jason13official.player_item_descriptions.Constants;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public final class DescriptionData {

  private static final String DESCRIPTION = "description";
  private static final String LOCK = "lock";

  public static @Nullable String getDescription(ItemStack stack) {

    CompoundTag tag = stack.getTagElement(Constants.MOD_ID);
    return tag != null && tag.contains(DESCRIPTION, Tag.TAG_STRING) ? tag.getString(DESCRIPTION) : null;
  }

  public static void setDescription(ItemStack stack, @Nullable String description) {

    if (description == null) {
      remove(stack, DESCRIPTION);
    } else {
      stack.getOrCreateTagElement(Constants.MOD_ID).putString(DESCRIPTION, description);
    }
  }

  public static @Nullable UUID getLock(ItemStack stack) {

    CompoundTag tag = stack.getTagElement(Constants.MOD_ID);
    return tag != null && tag.hasUUID(LOCK) ? tag.getUUID(LOCK) : null;
  }

  public static boolean hasLock(ItemStack stack) {
    return getLock(stack) != null;
  }

  public static void setLock(ItemStack stack, @Nullable UUID lock) {

    if (lock == null) {
      remove(stack, LOCK);
    } else {
      stack.getOrCreateTagElement(Constants.MOD_ID).putUUID(LOCK, lock);
    }
  }

  private static void remove(ItemStack stack, String key) {

    CompoundTag tag = stack.getTagElement(Constants.MOD_ID);

    if (tag == null) {
      return;
    }

    tag.remove(key);

    if (tag.isEmpty()) {
      stack.removeTagKey(Constants.MOD_ID);
    }
  }
}
